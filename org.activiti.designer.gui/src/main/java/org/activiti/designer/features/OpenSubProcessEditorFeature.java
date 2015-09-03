package org.activiti.designer.features;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.ValuedDataObject;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.editor.ActivitiDiagramEditor;
import org.activiti.designer.eclipse.editor.ActivitiDiagramEditorInput;
import org.activiti.designer.eclipse.util.FileService;
import org.activiti.designer.eclipse.util.Util;
import org.activiti.designer.util.ActivitiConstants;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.AbstractDrillDownFeature;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

@SuppressWarnings("restriction")
public class OpenSubProcessEditorFeature extends AbstractDrillDownFeature {
  SubProcess subprocess;
  IFile dataFile;
  IFile diagramFile;

  public OpenSubProcessEditorFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public String getName() {
    return "Open Sub Process Editor"; //$NON-NLS-1$
  }

  @Override
  public String getDescription() {
    return "Expand or collapse the sub process"; //$NON-NLS-1$
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    if (null != context.getPictogramElements() && context.getPictogramElements().length > 0) {
      Object bo = getBusinessObjectForPictogramElement(context.getPictogramElements()[0]);
      return SubProcess.class.equals(bo.getClass());
    }
    return false;
  }

  @Override
  public void execute(ICustomContext context) {
    try {
      subprocess = (SubProcess) getBusinessObjectForPictogramElement(context.getPictogramElements()[0]);
      initFiles4Subprocess();
    } catch (Exception e) {
      e.printStackTrace();
    }
    super.execute(context);
  }

  private void initFiles4Subprocess() {
    Resource resource = getDiagram().eResource();

    URI uri = resource.getURI();
    URI uriTrimmed = uri.trimFragment();

    IPath path = new Path(String.format(
        uriTrimmed.trimFileExtension().toPlatformString(true) + ".%s" + ActivitiConstants.DATA_FILE_EXTENSION,
        subprocess.getId()));
    dataFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
    diagramFile = ResourcesPlugin.getWorkspace().getRoot()
        .getFile(new Path(Util.getSubProcessURI(getDiagram(), subprocess.getId()).toPlatformString(true)));
    if (!dataFile.exists())
      saveSubprocessElements();
  }

  private Diagram getTopLevelDiagram() {
    ActivitiDiagramEditor editor = (ActivitiDiagramEditor) getDiagramBehavior().getDiagramContainer();
    ActivitiDiagramEditorInput adei = (ActivitiDiagramEditorInput) editor.getDiagramEditorInput();
    ActivitiDiagramEditor parent = adei.getParentEditor();
    while (parent != null) {
      editor = parent;
      adei = (ActivitiDiagramEditorInput) parent.getDiagramEditorInput();
      parent = adei.getParentEditor();
    }
    return editor.getDiagramTypeProvider().getDiagram();
  }

  private void saveSubprocessElements() {
    BpmnMemoryModel model = new BpmnMemoryModel(getFeatureProvider(), dataFile);
    BpmnModel bpmnModel = new BpmnModel();
    Process process = new Process();
    process.setName(subprocess.getName());
    process.setId(subprocess.getId());

    BpmnModel pMmodel = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel();

    process.getFlowElements().addAll(subprocess.getFlowElements());
    if (process.getFlowElements().isEmpty()) // init with a start event, as
                                             // empty process will be ignored
    {
      StartEvent start = new StartEvent();
      start.setId(
          ActivitiUiUtil.getNextId(StartEvent.class, CreateStartEventFeature.FEATURE_ID_KEY, getTopLevelDiagram()));
      start.setName("Start");
      process.addFlowElement(start);
      GraphicInfo gi = new GraphicInfo();
      gi.setHeight(35);
      gi.setWidth(35);
      gi.setX(10);
      gi.setY(10);
      gi.setElement(start);
      bpmnModel.addGraphicInfo(start.getId(), gi);
      subprocess.addFlowElement(start);
      pMmodel.addGraphicInfo(start.getId(), gi);
    } else {
      if (pMmodel.getLocationMap().size() > 0) {
        for (FlowElement element : process.getFlowElements()) {
          if (element instanceof SequenceFlow) {
            bpmnModel.addFlowGraphicInfoList(element.getId(), pMmodel.getFlowLocationGraphicInfo(element.getId()));
            continue;
          } else if (ValuedDataObject.class.isAssignableFrom(element.getClass())) {
            continue;
          } else if (element instanceof SubProcess) {
            // loop
          }
          bpmnModel.addGraphicInfo(element.getId(), pMmodel.getGraphicInfo(element.getId()));
        }
      }
    }

    process.getArtifacts().addAll(subprocess.getArtifacts());
    if (pMmodel.getLocationMap().size() > 0) {
      for (Artifact element : process.getArtifacts()) {
        if (element instanceof Association) {
          bpmnModel.addFlowGraphicInfoList(element.getId(), pMmodel.getFlowLocationGraphicInfo(element.getId()));
          continue;
        }
        bpmnModel.addGraphicInfo(element.getId(), pMmodel.getGraphicInfo(element.getId()));
      }
    }

    bpmnModel.addProcess(process);
    model.setBpmnModel(bpmnModel);
    String dataFileString = dataFile.getLocationURI().getPath();
    try {
      BpmnXMLConverter converter = new BpmnXMLConverter();

      byte[] xmlBytes = converter.convertToXML(model.getBpmnModel());

      File objectsFile = new File(dataFileString);
      FileOutputStream fos = new FileOutputStream(objectsFile);
      fos.write(xmlBytes);
      fos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void openDiagramEditor(Diagram diagram) {
    final String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
    final ActivitiDiagramEditorInput result = new ActivitiDiagramEditorInput(EcoreUtil.getURI(diagram), providerId);
    final ActivitiDiagramEditor diagramEditor = (ActivitiDiagramEditor) getFeatureProvider().getDiagramTypeProvider()
        .getDiagramBehavior().getDiagramContainer();
    result.setDiagramFile(diagramFile);
    result.setDataFile(dataFile);
    result.setParentEditor(diagramEditor);
    result.setSubprocess(subprocess);
    final IWorkbench workbench = PlatformUI.getWorkbench();

    workbench.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          ActivitiDiagramEditor childEditor = (ActivitiDiagramEditor)IDE.openEditor(workbench.getActiveWorkbenchWindow().getActivePage(), result, ActivitiConstants.DIAGRAM_EDITOR_ID);
          diagramEditor.getChildEditors().add(childEditor);
        } catch (PartInitException exception) {
          exception.printStackTrace();
        }
      }
    });
  }

  @Override
  protected Collection<Diagram> getLinkedDiagrams(PictogramElement pe) {
    return getDiagrams();
  }

  @Override
  protected Collection<Diagram> getDiagrams() {

    Collection<Diagram> result = new ArrayList<Diagram>();
    Resource resource = getDiagram().eResource();

    URI uri = resource.getURI();
    URI uriTrimmed = uri.trimFragment();

    if (uriTrimmed.isPlatformResource()) {

      String platformString = uriTrimmed.toPlatformString(true);

      IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);

      if (fileResource != null) {
        IProject project = fileResource.getProject();

        if (diagramFile.exists()) {
          result.add(getExistingDiagram(project, diagramFile));
        } else {
          result.add(getNewDiagram(project, diagramFile));
        }
      }
    }

    return result;
  }

  private Diagram getNewDiagram(final IProject project, final IFile targetFile) {
    Diagram diagram = null;
    URI uri = URI.createPlatformResourceURI(targetFile.getFullPath().toString(), true);

    TransactionalEditingDomain domain = null;

    boolean createContent = PreferencesUtil.getBooleanPreference(Preferences.EDITOR_ADD_DEFAULT_CONTENT_TO_DIAGRAMS,
        ActivitiPlugin.getDefault());

    final ActivitiDiagramEditor diagramEditor = (ActivitiDiagramEditor) getFeatureProvider().getDiagramTypeProvider()
        .getDiagramEditor();

    if (createContent) {
      final InputStream contentStream = Util.getContentStream(Util.Content.NEW_SUBPROCESS_CONTENT);
      InputStream replacedStream = Util.swapStreamContents(subprocess.getName(), contentStream);
      domain = FileService.createEmfFileForDiagram(uri, null, diagramEditor, replacedStream, targetFile);
      diagram = org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal.getEmfService()
          .getDiagramFromFile(targetFile, domain.getResourceSet());
    } else {
      diagram = Graphiti.getPeCreateService().createDiagram("BPMNdiagram", subprocess.getId(), true);
      ResourceSet resourceSet = new ResourceSetImpl();
      final Resource resource = resourceSet.createResource(uri);
      resource.getContents().add(diagram);
      try {
        resource.save(null);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    return diagram;
  }

  private Diagram getExistingDiagram(final IProject project, final IFile targetFile) {
    final ResourceSet rSet = new ResourceSetImpl();
    Diagram diagram = GraphitiUiInternal.getEmfService().getDiagramFromFile(targetFile, rSet);
    return diagram;
  }
}
