package org.activiti.designer.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.extension.AbstractDiagramWorker;
import org.activiti.designer.eclipse.extension.validation.ProcessValidator;
import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.activiti.designer.features.CreateBoundaryErrorFeature;
import org.activiti.designer.features.CreateBoundaryTimerFeature;
import org.activiti.designer.features.CreateBusinessRuleTaskFeature;
import org.activiti.designer.features.CreateCallActivityFeature;
import org.activiti.designer.features.CreateCustomServiceTaskFeature;
import org.activiti.designer.features.CreateEmbeddedSubProcessFeature;
import org.activiti.designer.features.CreateEndEventFeature;
import org.activiti.designer.features.CreateErrorEndEventFeature;
import org.activiti.designer.features.CreateExclusiveGatewayFeature;
import org.activiti.designer.features.CreateMailTaskFeature;
import org.activiti.designer.features.CreateManualTaskFeature;
import org.activiti.designer.features.CreateParallelGatewayFeature;
import org.activiti.designer.features.CreateScriptTaskFeature;
import org.activiti.designer.features.CreateServiceTaskFeature;
import org.activiti.designer.features.CreateStartEventFeature;
import org.activiti.designer.features.CreateUserTaskFeature;
import org.activiti.designer.features.DeleteSequenceFlowFeature;
import org.activiti.designer.features.ExpandCollapseSubProcessFeature;
import org.activiti.designer.features.SaveBpmnModelFeature;
import org.activiti.designer.integration.palette.PaletteEntry;
import org.activiti.designer.property.extension.CustomServiceTaskContext;
import org.activiti.designer.property.extension.util.ExtensionUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.eclipse.graphiti.ui.internal.GraphitiUIPlugin;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

public class ActivitiToolBehaviorProvider extends DefaultToolBehaviorProvider {

  private static final Map<Class< ? extends ICreateFeature>, PaletteEntry> toolMapping = new HashMap<Class< ? extends ICreateFeature>, PaletteEntry>();

  public ActivitiToolBehaviorProvider(IDiagramTypeProvider dtp) {
    super(dtp);

    // Setup tool mappings to palette entries
    toolMapping.put(CreateStartEventFeature.class, PaletteEntry.START_EVENT);
    toolMapping.put(CreateEndEventFeature.class, PaletteEntry.END_EVENT);
    toolMapping.put(CreateErrorEndEventFeature.class, PaletteEntry.ERROR_END_EVENT);
    toolMapping.put(CreateExclusiveGatewayFeature.class, PaletteEntry.EXCLUSIVE_GATEWAY);
    toolMapping.put(CreateMailTaskFeature.class, PaletteEntry.MAIL_TASK);
    toolMapping.put(CreateManualTaskFeature.class, PaletteEntry.MANUAL_TASK);
    toolMapping.put(CreateParallelGatewayFeature.class, PaletteEntry.PARALLEL_GATEWAY);
    toolMapping.put(CreateScriptTaskFeature.class, PaletteEntry.SCRIPT_TASK);
    toolMapping.put(CreateServiceTaskFeature.class, PaletteEntry.SERVICE_TASK);
    toolMapping.put(CreateCallActivityFeature.class, PaletteEntry.CALL_ACTIVITY);
    toolMapping.put(CreateEmbeddedSubProcessFeature.class, PaletteEntry.SUBPROCESS);
    toolMapping.put(CreateUserTaskFeature.class, PaletteEntry.USER_TASK);
    toolMapping.put(CreateBoundaryTimerFeature.class, PaletteEntry.BOUNDARY_TIMER);
    toolMapping.put(CreateBoundaryErrorFeature.class, PaletteEntry.ERROR_END_EVENT);
    toolMapping.put(CreateBusinessRuleTaskFeature.class, PaletteEntry.BUSINESSRULE_TASK);
  }

  @Override
  public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
    /*ICustomFeature customFeature = new ExpandCollapseSubProcessFeature(getFeatureProvider());
    if (customFeature.canExecute(context)) {
      return customFeature;
    }*/
    return super.getDoubleClickFeature(context);
  }

  @Override
  public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
    IContextButtonPadData data = super.getContextButtonPad(context);
    PictogramElement pe = context.getPictogramElement();

    setGenericContextButtons(data, pe, CONTEXT_BUTTON_DELETE | CONTEXT_BUTTON_UPDATE);

    CreateConnectionContext ccc = new CreateConnectionContext();
    ccc.setSourcePictogramElement(pe);
    Anchor anchor = null;
    if (pe instanceof Anchor) {
      anchor = (Anchor) pe;
    } else if (pe instanceof AnchorContainer) {
      anchor = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
    }
    ccc.setSourceAnchor(anchor);

    ContextButtonEntry button = new ContextButtonEntry(null, context);
    button.setText("Create connection"); //$NON-NLS-1$
    button.setIconId(ActivitiImageProvider.IMG_EREFERENCE);
    ICreateConnectionFeature[] features = getFeatureProvider().getCreateConnectionFeatures();
    for (ICreateConnectionFeature feature : features) {
      if (feature.isAvailable(ccc) && feature.canStartConnection(ccc))
        button.addDragAndDropFeature(feature);
    }

    Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
    if (bo instanceof SubProcess) {

      CustomContext newContext = new CustomContext(new PictogramElement[] { pe });
      newContext.setInnerGraphicsAlgorithm(pe.getGraphicsAlgorithm());
      newContext.setInnerPictogramElement(pe);

      ExpandCollapseSubProcessFeature feature = new ExpandCollapseSubProcessFeature(getFeatureProvider());

      ContextButtonEntry drillDownButton = new ContextButtonEntry(feature, newContext);
      drillDownButton.setText("Expand subprocess"); //$NON-NLS-1$
      drillDownButton.setDescription("Drill down into this subprocess and edit its diagram"); //$NON-NLS-1$
      drillDownButton.setIconId(ActivitiImageProvider.IMG_ACTION_ZOOM);

      data.getDomainSpecificContextButtons().add(drillDownButton);
    }

    if (button.getDragAndDropFeatures().size() > 0) {
      data.getDomainSpecificContextButtons().add(button);
    }

    return data;
  }

  @Override
  public IContextMenuEntry[] getContextMenu(ICustomContext context) {
    List<IContextMenuEntry> menuList = new ArrayList<IContextMenuEntry>();
    
    if(context.getPictogramElements() != null) {
      for (PictogramElement pictogramElement : context.getPictogramElements()) {
        if(pictogramElement.getLink() == null) continue;
        EList<EObject> boList = pictogramElement.getLink().getBusinessObjects();
        if(boList != null) {
          for (EObject bObject : boList) {
            if(bObject instanceof SequenceFlow) {
              ContextMenuEntry subMenuDelete = new ContextMenuEntry(new DeleteSequenceFlowFeature(getFeatureProvider()), context);
              subMenuDelete.setText("Delete sequence flow"); //$NON-NLS-1$
              subMenuDelete.setSubmenu(false);
              menuList.add(subMenuDelete);
            }
          }
        }
      }
    }

    ContextMenuEntry subMenuExport = new ContextMenuEntry(new SaveBpmnModelFeature(getFeatureProvider()), context);
    subMenuExport.setText("Export to BPMN 2.0 XML"); //$NON-NLS-1$
    subMenuExport.setSubmenu(false);
    menuList.add(subMenuExport);

    ContextMenuEntry subMenuExpandOrCollapse = new ContextMenuEntry(new ExpandCollapseSubProcessFeature(getFeatureProvider()), context);
    subMenuExpandOrCollapse.setText("Expand/Collapse Subprocess"); //$NON-NLS-1$
    subMenuExpandOrCollapse.setSubmenu(false);

    return menuList.toArray(new IContextMenuEntry[menuList.size()]);
  }

  @Override
  public IPaletteCompartmentEntry[] getPalette() {

    final IProject project = ActivitiUiUtil.getProjectFromDiagram(getDiagramTypeProvider().getDiagram());

    final List<IPaletteCompartmentEntry> ret = new ArrayList<IPaletteCompartmentEntry>();

    // add compartments from super class if not disabled
    IPaletteCompartmentEntry[] superCompartments = super.getPalette();

    // create new compartments
    IPaletteCompartmentEntry connectionCompartmentEntry = new PaletteCompartmentEntry("Connection", null);
    IPaletteCompartmentEntry eventCompartmentEntry = new PaletteCompartmentEntry("Event", null);
    IPaletteCompartmentEntry taskCompartmentEntry = new PaletteCompartmentEntry("Task", null);
    IPaletteCompartmentEntry gatewayCompartmentEntry = new PaletteCompartmentEntry("Gateway", null);
    IPaletteCompartmentEntry boundaryEventCompartmentEntry = new PaletteCompartmentEntry("Boundary event", null);

    for (int i = 0; i < superCompartments.length; i++) {

      final IPaletteCompartmentEntry entry = superCompartments[i];

      // Prune any disabled palette entries in the Objects compartment
      if ("Objects".equals(entry.getLabel())) {
        pruneDisabledPaletteEntries(project, entry);
      }
    }

    for (IPaletteCompartmentEntry iPaletteCompartmentEntry : superCompartments) {
      for (IToolEntry toolEntry : iPaletteCompartmentEntry.getToolEntries()) {
        if ("sequenceflow".equalsIgnoreCase(toolEntry.getLabel())) {
          connectionCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("startevent".equalsIgnoreCase(toolEntry.getLabel())) {
          eventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("endevent".equalsIgnoreCase(toolEntry.getLabel())) {
          eventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("errorendevent".equalsIgnoreCase(toolEntry.getLabel())) {
          eventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("usertask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("scripttask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("servicetask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("mailtask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("manualtask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("receivetask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("businessruletask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("timerboundaryevent".equalsIgnoreCase(toolEntry.getLabel())) {
          boundaryEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("errorboundaryevent".equalsIgnoreCase(toolEntry.getLabel())) {
          boundaryEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("parallelgateway".equalsIgnoreCase(toolEntry.getLabel())) {
          gatewayCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("exclusivegateway".equalsIgnoreCase(toolEntry.getLabel())) {
          gatewayCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("subprocess".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("callactivity".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        }
      }
    }
    // Always add the connection compartment
    ret.add(connectionCompartmentEntry);

    if (eventCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(eventCompartmentEntry);
    }
    if (taskCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(taskCompartmentEntry);
    }
    if (gatewayCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(gatewayCompartmentEntry);
    }
    if (boundaryEventCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(boundaryEventCompartmentEntry);
    }

    final Map<String, List<CustomServiceTaskContext>> tasksInDrawers = new HashMap<String, List<CustomServiceTaskContext>>();

    final List<CustomServiceTaskContext> customServiceTaskContexts = ExtensionUtil.getCustomServiceTaskContexts(project);

    final ImageRegistry reg = GraphitiUIPlugin.getDefault().getImageRegistry();
    for (final CustomServiceTaskContext taskContext : customServiceTaskContexts) {
      if (reg.get(taskContext.getSmallImageKey()) == null) {
        reg.put(taskContext.getSmallImageKey(),
                new Image(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay(), taskContext.getSmallIconStream()));
      }
      if (reg.get(taskContext.getLargeImageKey()) == null) {
        reg.put(taskContext.getLargeImageKey(),
                new Image(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay(), taskContext.getLargeIconStream()));
      }
      if (reg.get(taskContext.getShapeImageKey()) == null) {
        reg.put(taskContext.getShapeImageKey(),
                new Image(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay(), taskContext.getShapeIconStream()));
      }
    }

    for (final CustomServiceTaskContext taskContext : customServiceTaskContexts) {
      if (!tasksInDrawers.containsKey(taskContext.getServiceTask().contributeToPaletteDrawer())) {
        tasksInDrawers.put(taskContext.getServiceTask().contributeToPaletteDrawer(), new ArrayList<CustomServiceTaskContext>());
      }
      tasksInDrawers.get(taskContext.getServiceTask().contributeToPaletteDrawer()).add(taskContext);
    }

    for (final Entry<String, List<CustomServiceTaskContext>> drawer : tasksInDrawers.entrySet()) {

      // Sort the list
      Collections.sort(drawer.getValue());

      final IPaletteCompartmentEntry paletteCompartmentEntry = new PaletteCompartmentEntry(drawer.getKey(), null);

      for (final CustomServiceTaskContext currentDrawerItem : drawer.getValue()) {
        final CreateCustomServiceTaskFeature feature = new CreateCustomServiceTaskFeature(getFeatureProvider(), currentDrawerItem.getServiceTask().getName(),
                currentDrawerItem.getServiceTask().getDescription(), currentDrawerItem.getServiceTask().getClass().getCanonicalName());
        final IToolEntry entry = new ObjectCreationToolEntry(currentDrawerItem.getServiceTask().getName(), currentDrawerItem.getServiceTask().getDescription(),
                currentDrawerItem.getSmallImageKey(), null, feature);
        paletteCompartmentEntry.getToolEntries().add(entry);
      }
      ret.add(paletteCompartmentEntry);
    }

    return ret.toArray(new IPaletteCompartmentEntry[ret.size()]);
  }

  /**
   * Prunes the disabled palette entries from the
   * {@link IPaletteCompartmentEntry}.
   * 
   * @param entry
   *          the compartment being pruned
   */
  private void pruneDisabledPaletteEntries(final IProject project, final IPaletteCompartmentEntry entry) {

    final Set<PaletteEntry> disabledPaletteEntries = ExtensionUtil.getDisabledPaletteEntries(project);

    if (!disabledPaletteEntries.isEmpty()) {

      final Iterator<IToolEntry> entryIterator = entry.getToolEntries().iterator();

      while (entryIterator.hasNext()) {

        final IToolEntry toolEntry = entryIterator.next();

        if (disabledPaletteEntries.contains(PaletteEntry.ALL)) {
          entryIterator.remove();
        } else {
          if (toolEntry instanceof ObjectCreationToolEntry) {
            final ObjectCreationToolEntry objToolEntry = (ObjectCreationToolEntry) toolEntry;
            if (toolMapping.containsKey(objToolEntry.getCreateFeature().getClass())) {
              if (disabledPaletteEntries.contains(toolMapping.get(objToolEntry.getCreateFeature().getClass()))) {
                entryIterator.remove();
              }
            }
          }
        }
      }
    }
  }

  @Override
  public IDecorator[] getDecorators(PictogramElement pe) {
    IFeatureProvider featureProvider = getFeatureProvider();
    Object bo = featureProvider.getBusinessObjectForPictogramElement(pe);
    if (bo instanceof StartEvent) {
      StartEvent startEvent = (StartEvent) bo;
      if (startEvent.getOutgoing().size() != 1) {
        IDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
        imageRenderingDecorator.setMessage("A start event should have exactly one outgoing sequence flow"); //$NON-NLS-1$
        return new IDecorator[] { imageRenderingDecorator };
      }
    /*} else if (bo instanceof SubProcess) {
      SubProcess subProcess = (SubProcess) bo;

      if (!subProcessDiagramExists(subProcess)) {
        IDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_INFORMATION_TSK);
        imageRenderingDecorator.setMessage("This subprocess does not have a diagram model yet");//$NON-NLS-1$
        return new IDecorator[] { imageRenderingDecorator };
      }*/
    } else if (bo instanceof ServiceTask && bo instanceof EObject && ExtensionUtil.isCustomServiceTask((EObject) bo)) {

      final Resource resource = pe.getLink().eResource();
      final IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(resource.getURI().toPlatformString(true));

      final List<IMarker> markers = getMarkers(res, (ServiceTask) bo);

      if (markers.size() > 0) {

        int maximumSeverity = 0;

        try {
          for (final IMarker marker : markers) {
            final Object severity = marker.getAttribute(IMarker.SEVERITY);
            if (severity != null && severity instanceof Integer) {
              maximumSeverity = Math.max(maximumSeverity, (Integer) severity);
            }
          }
        } catch (CoreException e) {
          e.printStackTrace();
        }

        String decoratorImagePath = null;

        switch (maximumSeverity) {
        case IMarker.SEVERITY_INFO:
          decoratorImagePath = IPlatformImageConstants.IMG_ECLIPSE_INFORMATION_TSK;
          break;
        case IMarker.SEVERITY_WARNING:
          decoratorImagePath = IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK;
          break;
        default:
          decoratorImagePath = IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK;
        }

        final ImageDecorator imageRenderingDecorator = new ImageDecorator(decoratorImagePath);
        imageRenderingDecorator.setMessage("There are validation markers for the properties of this node");//$NON-NLS-1$ 
        imageRenderingDecorator.setX(pe.getGraphicsAlgorithm().getWidth() / 2 - 10);
        imageRenderingDecorator.setY(4);

        return new IDecorator[] { imageRenderingDecorator };

      } else {
        return new IDecorator[] {};
      }
    }
    return super.getDecorators(pe);
  }
  protected List<IMarker> getMarkers(IResource resource, ServiceTask serviceTask) {

    final List<IMarker> result = new ArrayList<IMarker>();

    try {
      final IMarker[] markers = resource.findMarkers(ProcessValidator.MARKER_ID, true, IResource.DEPTH_INFINITE);
      for (final IMarker marker : markers) {
        Object attribute = marker.getAttribute(AbstractDiagramWorker.ATTRIBUTE_NODE_ID);
        if (attribute != null) {
          if (StringUtils.equals((String) attribute, serviceTask.getId())) {
            result.add(marker);
          }
        }

      }
    } catch (CoreException e) {
      e.printStackTrace();
    }

    return result;
  }
  private boolean subProcessDiagramExists(SubProcess subProcess) {
    Resource resource = getDiagramTypeProvider().getDiagram().eResource();

    URI uri = resource.getURI();
    URI uriTrimmed = uri.trimFragment();

    if (uriTrimmed.isPlatformResource()) {

      String platformString = uriTrimmed.toPlatformString(true);

      IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);

      if (fileResource != null) {
        IProject project = fileResource.getProject();
        final String parentDiagramName = uriTrimmed.trimFileExtension().lastSegment();

        IFile file = project.getFile(String.format(ActivitiBPMNDiagramConstants.DIAGRAM_FOLDER + "%s.%s" + ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION,
                parentDiagramName, subProcess.getId()));

        Diagram diagram = GraphitiUiInternal.getEmfService().getDiagramFromFile(file, new ResourceSetImpl());

        return diagram != null;
      }
    }
    // Safe default assumption
    return true;
  }
}
