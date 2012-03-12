package org.activiti.designer.util.eclipse;

import java.util.List;

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.EventDefinition;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.AlgorithmsFactory;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class ActivitiUiUtil {

  private static final String ID_PATTERN = "%s%s";

  public static void runModelChange(final Runnable runnable, final TransactionalEditingDomain editingDomain, final String label) {

    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain, label) {

      protected void doExecute() {
        runnable.run();
      }
    });
  }

  @SuppressWarnings("rawtypes")
  public static boolean contextPertainsToBusinessObject(final ICustomContext context, final Class businessClass) {
    boolean result = false;
    EList<EObject> businessObjects = context.getInnerPictogramElement().getLink().getBusinessObjects();
    for (final EObject eobj : businessObjects) {
      if (businessClass.equals(eobj.getClass())) {
        result = true;
        break;
      }
    }
    return result;
  }

  @SuppressWarnings("rawtypes")
  public static Object getBusinessObjectFromContext(final ICustomContext context, final Class businessClass) {
    Object result = null;
    EList<EObject> businessObjects = context.getInnerPictogramElement().getLink().getBusinessObjects();
    for (final EObject eobj : businessObjects) {
      if (businessClass.equals(eobj.getClass())) {
        result = eobj;
        break;
      }
    }
    return result;
  }

  public static Ellipse createInvisibleEllipse(GraphicsAlgorithmContainer gaContainer, IGaService gaService) {
    Ellipse ret = AlgorithmsFactory.eINSTANCE.createEllipse();
    ret.setX(0);
    ret.setY(0);
    ret.setWidth(0);
    ret.setHeight(0);
    ret.setFilled(false);
    ret.setLineVisible(false);
    if (gaContainer instanceof PictogramElement) {
      PictogramElement pe = (PictogramElement) gaContainer;
      pe.setGraphicsAlgorithm(ret);
    } else if (gaContainer instanceof GraphicsAlgorithm) {
      GraphicsAlgorithm parentGa = (GraphicsAlgorithm) gaContainer;
      parentGa.getGraphicsAlgorithmChildren().add(ret);
    }
    return ret;
  }

  public static Process getProcessObject(Diagram diagram) {
    return ModelHandler.getModel(EcoreUtil.getURI(diagram)).getProcess();
  }

  public static void doProjectReferenceChange(IProject currentProject, IJavaProject containerProject, String className) throws CoreException {

    if (currentProject.equals(containerProject.getProject())) {
      return;
    }

    IProjectDescription desc = currentProject.getDescription();
    IProject[] refs = desc.getReferencedProjects();
    IProject[] newRefs = new IProject[refs.length + 1];
    System.arraycopy(refs, 0, newRefs, 0, refs.length);
    newRefs[refs.length] = containerProject.getProject();
    desc.setReferencedProjects(newRefs);
    currentProject.setDescription(desc, new NullProgressMonitor());

    IPath dependsOnPath = containerProject.getProject().getFullPath();

    IJavaProject javaProject = (IJavaProject) currentProject.getNature(JavaCore.NATURE_ID);
    IClasspathEntry prjEntry = JavaCore.newProjectEntry(dependsOnPath, true);

    boolean dependsOnPresent = false;
    for (IClasspathEntry cpEntry : javaProject.getRawClasspath()) {
      if (cpEntry.equals(prjEntry)) {
        dependsOnPresent = true;
      }
    }

    if (!dependsOnPresent) {
      IClasspathEntry[] entryList = new IClasspathEntry[1];
      entryList[0] = prjEntry;
      IClasspathEntry[] newEntries = (IClasspathEntry[]) ArrayUtils.addAll(javaProject.getRawClasspath(), entryList);
      javaProject.setRawClasspath(newEntries, null);
    }

  }

  public static IProject getProjectFromDiagram(Diagram diagram) {

    IProject currentProject = null;
    Resource resource = diagram.eResource();

    URI uri = resource.getURI();
    URI uriTrimmed = uri.trimFragment();

    if (uriTrimmed.isPlatformResource()) {

      String platformString = uriTrimmed.toPlatformString(true);
      IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);

      if (fileResource != null) {
        currentProject = fileResource.getProject();
      }
    } else {
    	IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(uriTrimmed.toString());
    	
    	if (fileResource != null) {
        currentProject = fileResource.getProject();
      }
    }
    return currentProject;
  }

  /**
   * Gets the {@link ActionRegistry} for the currently active editor.
   * 
   * @return the ActionRegistry or null
   */
  public static final ActionRegistry getActionRegistry() {
    IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
    if (part instanceof DiagramEditor) {
      DiagramEditor editor = (DiagramEditor) part;
      return (ActionRegistry) editor.getAdapter(ActionRegistry.class);
    }
    return null;
  }

  /**
   * Runs the action with the provided id for the currently active editor, if
   * there is one.
   * 
   * @param actionId
   *          the id of the action to run
   */
  public static final void runAction(final String actionId) {
    final ActionRegistry registry = getActionRegistry();
    if (registry != null) {
      final IAction action = registry.getAction(actionId);
      if (action != null) {
        action.run();
      }
    }
  }

  public static final String getNextId(final Class featureClass, final String featureIdKey, final Diagram diagram) {

    int determinedId = 0;
    Bpmn2MemoryModel model =  ModelHandler.getModel(EcoreUtil.getURI(diagram));
    for (FlowElement element : model.getProcess().getFlowElements()) {
      
      if(element instanceof SubProcess) {
        
        for (FlowElement flowElement : ((SubProcess) element).getFlowElements()) {
          
          if (flowElement.getClass() == featureClass) {
            String contentObjectId = flowElement.getId().replace(featureIdKey, "");
            determinedId = getId(contentObjectId, determinedId);
          }
          if (flowElement instanceof Activity) {
          	List<BoundaryEvent> eventList = ((Activity) flowElement).getBoundaryEvents();
          	for (BoundaryEvent boundaryEvent : eventList) {
	            List<EventDefinition> definitionList = boundaryEvent.getEventDefinitions();
	            for (EventDefinition eventDefinition : definitionList) {
	              if(eventDefinition.getClass() == featureClass) {
	              	String contentObjectId = boundaryEvent.getId().replace(featureIdKey, "");
	                determinedId = getId(contentObjectId, determinedId);
	              }
              }
            }
          }
        }
      }
      
      if (element.getClass() == featureClass) {
        String contentObjectId = element.getId().replace(featureIdKey, "");
        determinedId = getId(contentObjectId, determinedId);
      }
      if (element instanceof Activity) {
      	List<BoundaryEvent> eventList = ((Activity) element).getBoundaryEvents();
      	for (BoundaryEvent boundaryEvent : eventList) {
          List<EventDefinition> definitionList = boundaryEvent.getEventDefinitions();
          for (EventDefinition eventDefinition : definitionList) {
            if(eventDefinition.getClass() == featureClass) {
            	String contentObjectId = boundaryEvent.getId().replace(featureIdKey, "");
              determinedId = getId(contentObjectId, determinedId);
            }
          }
        }
      }
    }
    determinedId++;
    return String.format(ID_PATTERN, featureIdKey, determinedId);

  }
  
  private static int getId(String contentObjectId, int determinedId) {
    int newdId = determinedId;
    boolean isNumber = true;
    if(contentObjectId != null && contentObjectId.length() > 0) {
      
      for(int i = 0; i < contentObjectId.length(); i++) {
        if(Character.isDigit(contentObjectId.charAt(i)) == false) {
          isNumber = false;
        }
      }
      if(isNumber == true) {
        Integer intNumber = Integer.valueOf(contentObjectId);
        if (intNumber > newdId) {
          newdId = intNumber;
        }
      }
    }
    return newdId;
  }

}
