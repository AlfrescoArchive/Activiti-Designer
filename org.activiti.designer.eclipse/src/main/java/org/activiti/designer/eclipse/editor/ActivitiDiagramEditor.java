package org.activiti.designer.eclipse.editor;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.CustomProperty;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.eclipse.ui.ActivitiEditorContextMenuProvider;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;

public class ActivitiDiagramEditor extends DiagramEditor {

  public final static String ID = "org.activiti.designer.editor.diagramEditor";
  private static GraphicalViewer activeGraphicalViewer;

  private IFile modelFile;
  private IFile diagramFile;
  
  @Override
  public void init(IEditorSite site, IEditorInput input) throws PartInitException {

    try {
      if (input instanceof IFileEditorInput) { // Opened from the Open with...
                                               // menu
        modelFile = ((IFileEditorInput) input).getFile();
        input = createNewDiagramEditorInput();
      } else if (input instanceof DiagramEditorInput) { // Opened by the default
                                                        // associated file
                                                        // extension .bpmn
        getModelPathFromInput((DiagramEditorInput) input);
        input = createNewDiagramEditorInput();
      } else if (input instanceof IURIEditorInput) { // Opened external to
                                                     // Eclipse
        IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
        java.net.URI uri = ((IURIEditorInput) input).getURI();
        String path = uri.getPath();
        if (wsRoot.getProject("import").exists() == false) {
          wsRoot.getProject("import").create(null);
        }
        wsRoot.getProject("import").open(null);
        try {
          InputStream inputStream = new FileInputStream(path);
          String fileName = null;
          if (path.contains("/")) {
            fileName = path.substring(path.lastIndexOf("/") + 1);
          } else {
            fileName = path.substring(path.lastIndexOf("\\") + 1);
          }

          if (wsRoot.getProject("import").getFile(fileName).exists()) {
            wsRoot.getProject("import").getFile(fileName).delete(true, null);
          }

          wsRoot.getProject("import").getFile(fileName).create(inputStream, true, null);
          modelFile = wsRoot.getProject("import").getFile(fileName);
          input = createNewDiagramEditorInput();
        } catch (Exception e) {
          e.printStackTrace();
          return;
        }
      }

    } catch (CoreException e) {
      e.printStackTrace();
      return;
    }

    super.init(site, input);
  }

  private void getModelPathFromInput(DiagramEditorInput input) {
    URI uri = input.getUri();
    String uriString = uri.trimFragment().toPlatformString(true);
    modelFile = Bpmn2DiagramCreator.getModelFile(new Path(uriString));
  }

  /**
   * Beware, creates a new input and changes this editor!
   */
  private DiagramEditorInput createNewDiagramEditorInput() throws CoreException {
    IPath fullPath = modelFile.getFullPath();

    IFolder folder = Bpmn2DiagramCreator.getTempFolder(fullPath);
    diagramFile = Bpmn2DiagramCreator.getTempFile(fullPath, folder);

    // Create new temporary diagram file
    Bpmn2DiagramCreator creator = new Bpmn2DiagramCreator();
    creator.setDiagramFile(diagramFile);

    DiagramEditorInput input = creator.createDiagram(false, null);

    return input;
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    try {

      String diagramFileString = modelFile.getLocationURI().getPath();

      boolean saveImage = PreferencesUtil.getBooleanPreference(Preferences.SAVE_IMAGE);
      Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()));
      
      // add sequence flow bendpoints to the model
      final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();
      new GraphitiToBpmnDI().processGraphitiElements(model, featureProvider);
      
      BpmnXMLConverter converter = new BpmnXMLConverter();
      byte[] xmlBytes = converter.convertToXML(model.getBpmnModel());
      
      File objectsFile = new File(diagramFileString);
      FileOutputStream fos = new FileOutputStream(objectsFile);
      fos.write(xmlBytes);
      fos.close();
      
      if (saveImage) {
        marshallImage(model, diagramFileString);
      }

      modelFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ((BasicCommandStack) getEditingDomain().getCommandStack()).saveIsDone();
    updateDirtyState();
  }
  
  private void marshallImage(Bpmn2MemoryModel model, String modelFileName) {
    try {
      final GraphicalViewer graphicalViewer =  (GraphicalViewer) ((DiagramEditor) model.getFeatureProvider()
              .getDiagramTypeProvider().getDiagramEditor()).getAdapter(GraphicalViewer.class);

      if (graphicalViewer == null || graphicalViewer.getEditPartRegistry() == null)
        return;
      final ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) graphicalViewer.getEditPartRegistry().get(LayerManager.ID);
      final IFigure rootFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.PRINTABLE_LAYERS);
      final IFigure gridFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.GRID_LAYER);
      final Rectangle rootFigureBounds = rootFigure.getBounds();

      final boolean toggleRequired = gridFigure.isShowing();

      final Display display = Display.getDefault();

      final Image img = new Image(display, rootFigureBounds.width, rootFigureBounds.height);
      final GC imageGC = new GC(img);
      final SWTGraphics grap = new SWTGraphics(imageGC);

      // Access UI thread from runnable to print the canvas to the image
      display.syncExec(new Runnable() {

        public void run() {
          if (toggleRequired) {
            // Disable any grids temporarily
            gridFigure.setVisible(false);
          }
          // Deselect any selections
          graphicalViewer.deselectAll();
          rootFigure.paint(grap);
        }
      });

      ImageLoader imgLoader = new ImageLoader();
      imgLoader.data = new ImageData[] { img.getImageData() };

      ByteArrayOutputStream baos = new ByteArrayOutputStream(imgLoader.data.length);

      imgLoader.save(baos, SWT.IMAGE_PNG);

      imageGC.dispose();
      img.dispose();

      // Access UI thread from runnable
      display.syncExec(new Runnable() {

        public void run() {
          if (toggleRequired) {
            // Re-enable any grids
            gridFigure.setVisible(true);
          }
        }
      });

      String imageFileName = modelFileName.substring(0, modelFileName.lastIndexOf(".")) + ".png";
      File imageFile = new File(imageFileName);
      FileOutputStream outStream = new FileOutputStream(imageFile);
      baos.writeTo(outStream);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean isDirty() {
    TransactionalEditingDomain editingDomain = getEditingDomain();
    // Check that the editor is not yet disposed
    if (editingDomain != null && editingDomain.getCommandStack() != null) {
      return ((BasicCommandStack) editingDomain.getCommandStack()).isSaveNeeded();
    }
    return false;
  }

  @Override
  protected void setInput(IEditorInput input) {
    super.setInput(input);
    final Bpmn2MemoryModel model = new Bpmn2MemoryModel(getDiagramTypeProvider().getFeatureProvider(), modelFile);
    ModelHandler.addModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()), model);

    String filePath = modelFile.getLocationURI().getPath();
    File bpmnFile = new File(filePath);
    try {
      if (bpmnFile.exists() == false) {
        model.setBpmnModel(new BpmnModel());
        model.addMainProcess();
        bpmnFile.createNewFile();
        modelFile.refreshLocal(IResource.DEPTH_INFINITE, null);
      } else {
        FileInputStream fileStream = new FileInputStream(bpmnFile);
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(fileStream, "UTF-8");
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnXMLConverter bpmnConverter = new BpmnXMLConverter();
        bpmnConverter.setUserTaskFormTypes(PreferencesUtil.getStringArray(Preferences.ALFRESCO_FORMTYPES_USERTASK));
        bpmnConverter.setStartEventFormTypes(PreferencesUtil.getStringArray(Preferences.ALFRESCO_FORMTYPES_STARTEVENT));
        BpmnModel bpmnModel = bpmnConverter.convertToBpmnModel(xtr);
        model.setBpmnModel(bpmnModel);

        BasicCommandStack basicCommandStack = (BasicCommandStack) getEditingDomain().getCommandStack();

        if (input instanceof DiagramEditorInput) {

          basicCommandStack.execute(new RecordingCommand(getEditingDomain()) {

            @Override
            protected void doExecute() {
              importDiagram(model);
            }
          });
        }
        basicCommandStack.saveIsDone();
        basicCommandStack.flush();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void importDiagram(final Bpmn2MemoryModel model) {
    final Diagram diagram = getDiagramTypeProvider().getDiagram();
    diagram.setActive(true);

    getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {

      @Override
      protected void doExecute() {

        if (model.getBpmnModel().getPools().size() > 0) {
          for (Pool pool : model.getBpmnModel().getPools()) {
            PictogramElement poolElement = addContainerElement(pool, model, diagram);
            if (poolElement == null)
              continue;

            Process process = model.getBpmnModel().getProcess(pool.getId());
            for (Lane lane : process.getLanes()) {
              addContainerElement(lane, model, (ContainerShape) poolElement);
            }
          }
        }

        for (Process process : model.getBpmnModel().getProcesses()) {
          drawFlowElements(process.getFlowElements(), model.getBpmnModel().getLocationMap(), diagram, process);
          drawArtifacts(process.getArtifacts(), model.getBpmnModel().getLocationMap(), diagram, process);
        }
        drawAllFlows(model);
      }
    });
  }

  private PictogramElement addContainerElement(BaseElement element, Bpmn2MemoryModel model, ContainerShape parent) {
    GraphicInfo graphicInfo = model.getBpmnModel().getGraphicInfo(element.getId());
    if (graphicInfo == null)
      return null;

    final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();

    AddContext context = new AddContext(new AreaContext(), element);
    IAddFeature addFeature = featureProvider.getAddFeature(context);
    context.setNewObject(element);
    context.setSize((int) graphicInfo.width, (int) graphicInfo.height);
    context.setTargetContainer(parent);

    int x = (int) graphicInfo.x;
    int y = (int) graphicInfo.y;

    if (parent instanceof Diagram == false) {
      x = x - parent.getGraphicsAlgorithm().getX();
      y = y - parent.getGraphicsAlgorithm().getY();
    }

    context.setLocation(x, y);

    PictogramElement pictElement = null;
    if (addFeature.canAdd(context)) {
      pictElement = addFeature.add(context);
      featureProvider.link(pictElement, new Object[] { element });
    }

    return pictElement;
  }

  private void drawFlowElements(Collection<FlowElement> elementList, Map<String, GraphicInfo> locationMap, ContainerShape parentShape, Process process) {

    final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();

    List<FlowElement> noDIList = new ArrayList<FlowElement>();
    for (FlowElement flowElement : elementList) {

      if (flowElement instanceof SequenceFlow) continue;
      
      AddContext context = new AddContext(new AreaContext(), flowElement);
      IAddFeature addFeature = featureProvider.getAddFeature(context);

      if (addFeature == null) {
        System.out.println("Element not supported: " + flowElement);
        return;
      }

      GraphicInfo graphicInfo = locationMap.get(flowElement.getId());
      if (graphicInfo == null) {

        noDIList.add(flowElement);

      } else {

        context.setNewObject(flowElement);
        context.setSize((int) graphicInfo.width, (int) graphicInfo.height);

        ContainerShape parentContainer = null;
        if (parentShape instanceof Diagram) {
          parentContainer = getParentContainer(flowElement.getId(), process, (Diagram) parentShape);
        } else {
          parentContainer = parentShape;
        }

        context.setTargetContainer(parentContainer);
        if (parentContainer instanceof Diagram == false) {
          Point location = getLocation(parentContainer);
          context.setLocation((int) graphicInfo.x - location.x, (int) graphicInfo.y - location.y);
        } else {
          context.setLocation((int) graphicInfo.x, (int) graphicInfo.y);
        }

        if (flowElement instanceof ServiceTask) {

          ServiceTask serviceTask = (ServiceTask) flowElement;

          if (serviceTask.isExtended()) {

            CustomServiceTask targetTask = findCustomServiceTask(serviceTask);

            if (targetTask != null) {
              
              for (FieldExtension field : serviceTask.getFieldExtensions()) {
                CustomProperty customFieldProperty = new CustomProperty();
                customFieldProperty.setName(field.getFieldName());
                if (StringUtils.isNotEmpty(field.getExpression())) {
                  customFieldProperty.setSimpleValue(field.getExpression());
                } else {
                  customFieldProperty.setSimpleValue(field.getStringValue());
                }
                serviceTask.getCustomProperties().add(customFieldProperty);
              }

              serviceTask.getFieldExtensions().clear();
            }
          }

        }

        if (flowElement instanceof BoundaryEvent) {
          BoundaryEvent boundaryEvent = (BoundaryEvent) flowElement;
          if (boundaryEvent.getAttachedToRef() != null) {
            ContainerShape container = (ContainerShape) featureProvider.getPictogramElementForBusinessObject(boundaryEvent.getAttachedToRef());

            if (container != null) {
              AddContext boundaryContext = new AddContext(new AreaContext(), boundaryEvent);
              boundaryContext.setTargetContainer(container);
              Point location = getLocation(container);
              boundaryContext.setLocation((int) graphicInfo.x - location.x, (int) graphicInfo.y - location.y);

              if (addFeature.canAdd(boundaryContext)) {
                PictogramElement newBoundaryContainer = addFeature.add(boundaryContext);
                featureProvider.link(newBoundaryContainer, new Object[] { boundaryEvent });
              }
            }
          }
        } else if (addFeature.canAdd(context)) {
          PictogramElement newContainer = addFeature.add(context);
          featureProvider.link(newContainer, new Object[] { flowElement });
          
          if (flowElement instanceof SubProcess) {
            drawFlowElements(((SubProcess) flowElement).getFlowElements(), locationMap, (ContainerShape) newContainer, process);
          }
        }
      }
    }

    for (FlowElement flowElement : noDIList) {
      if (flowElement instanceof BoundaryEvent) {
        ((BoundaryEvent) flowElement).getAttachedToRef().getBoundaryEvents().remove(flowElement);
      } else {
        elementList.remove(flowElement);
      }
    }
  }

  private CustomServiceTask findCustomServiceTask(ServiceTask serviceTask) {
    CustomServiceTask result = null;
    if (serviceTask.isExtended()) {

      final List<CustomServiceTask> customServiceTasks = ExtensionUtil.getCustomServiceTasks(
              ActivitiUiUtil.getProjectFromDiagram(getDiagramTypeProvider().getDiagram()));

      for (final CustomServiceTask customServiceTask : customServiceTasks) {
        if (serviceTask.getExtensionId().equals(customServiceTask.getId())) {
          result = customServiceTask;
          break;
        }
      }
    }
    return result;
  }

  private ContainerShape getParentContainer(String flowElementId, Process process, Diagram diagram) {
    Lane foundLane = null;
    for (Lane lane : process.getLanes()) {
      if (lane.getFlowReferences().contains(flowElementId)) {
        foundLane = lane;
        break;
      }
    }

    if (foundLane != null) {
      final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();
      return (ContainerShape) featureProvider.getPictogramElementForBusinessObject(foundLane);
    } else {
      return diagram;
    }
  }

  private Point getLocation(ContainerShape containerShape) {
    if (containerShape instanceof Diagram == true) {
      return new Point(containerShape.getGraphicsAlgorithm().getX(), containerShape.getGraphicsAlgorithm().getY());
    }

    Point location = getLocation(containerShape.getContainer());
    return new Point(location.x + containerShape.getGraphicsAlgorithm().getX(), location.y + containerShape.getGraphicsAlgorithm().getY());
  }

  private void drawArtifacts(final Collection<Artifact> artifacts, final Map<String, GraphicInfo> locationMap, final ContainerShape parent, final Process process) {

    final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();

    final List<Artifact> artifactsWithoutDI = new ArrayList<Artifact>();
    for (final Artifact artifact : artifacts) {
      
      if (artifact instanceof Association) continue;
      
      final AddContext context = new AddContext(new AreaContext(), artifact);
      final IAddFeature addFeature = featureProvider.getAddFeature(context);

      if (addFeature == null) {
        System.out.println("Element not supported: " + artifact);
        return;
      }

      final GraphicInfo gi = locationMap.get(artifact.getId());
      if (gi == null) {
        artifactsWithoutDI.add(artifact);
      } else {
        context.setNewObject(artifact);
        context.setSize((int) gi.width, (int) gi.height);

        ContainerShape parentContainer = null;
        if (parent instanceof Diagram) {
          parentContainer = getParentContainer(artifact.getId(), process, (Diagram) parent);
        } else {
          parentContainer = parent;
        }

        context.setTargetContainer(parentContainer);
        if (parentContainer instanceof Diagram) {
          context.setLocation((int) gi.x, (int) gi.y);
        } else {
          final Point location = getLocation(parentContainer);

          context.setLocation((int) gi.x - location.x, (int) gi.y - location.y);
        }

        if (addFeature.canAdd(context)) {
          final PictogramElement newContainer = addFeature.add(context);
          featureProvider.link(newContainer, new Object[] { artifact });
        }
      }
    }

    for (final Artifact artifact : artifactsWithoutDI) {
      artifacts.remove(artifact);
    }
  }

  private void drawAllFlows(Bpmn2MemoryModel model) {
    BpmnModel bpmnModel = model.getBpmnModel();
    
    for (Process process : bpmnModel.getProcesses()) {
      drawSequenceFlowsInList(process.getFlowElements(), model);
      drawAssociationsInList(process.getArtifacts(), model);
    }
  }
  
  private void drawSequenceFlowsInList(Collection<FlowElement> flowList, Bpmn2MemoryModel model) {
    for (FlowElement flowElement : flowList) {
      
      if (flowElement instanceof SubProcess) {
        drawSequenceFlowsInList(((SubProcess) flowElement).getFlowElements(), model);
        drawAssociationsInList(((SubProcess) flowElement).getArtifacts(), model);
        
      } else if (flowElement instanceof SequenceFlow == false) {
        continue;
      } else {
        SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
        drawSequenceFlow(sequenceFlow, model);
      }
    }
  }
  
  private void drawSequenceFlow(SequenceFlow sequenceFlow, Bpmn2MemoryModel model) {
    Anchor sourceAnchor = null;
    Anchor targetAnchor = null;
    ContainerShape sourceShape = (ContainerShape) getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(
            model.getFlowElement(sequenceFlow.getSourceRef()));

    if (sourceShape == null)
      return;

    EList<Anchor> anchorList = sourceShape.getAnchors();
    for (Anchor anchor : anchorList) {
      if (anchor instanceof ChopboxAnchor) {
        sourceAnchor = anchor;
        break;
      }
    }

    ContainerShape targetShape = (ContainerShape) getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(
            model.getFlowElement(sequenceFlow.getTargetRef()));

    if (targetShape == null)
      return;

    anchorList = targetShape.getAnchors();
    for (Anchor anchor : anchorList) {
      if (anchor instanceof ChopboxAnchor) {
        targetAnchor = anchor;
        break;
      }
    }

    AddConnectionContext addContext = new AddConnectionContext(sourceAnchor, targetAnchor);

    List<GraphicInfo> bendpointList = new ArrayList<GraphicInfo>();
    if (model.getBpmnModel().getFlowLocationMap().containsKey(sequenceFlow.getId())) {
      List<GraphicInfo> pointList = model.getBpmnModel().getFlowLocationGraphicInfo(sequenceFlow.getId());
      if (pointList.size() > 2) {
        for (int i = 1; i < pointList.size() - 1; i++) {
          bendpointList.add(pointList.get(i));
        }
      }
    }
    addContext.putProperty("org.activiti.designer.bendpoints", bendpointList);
    addContext.putProperty("org.activiti.designer.connectionlabel", model.getBpmnModel().getLabelGraphicInfo(sequenceFlow.getId()));

    addContext.setNewObject(sequenceFlow);
    getDiagramTypeProvider().getFeatureProvider().addIfPossible(addContext);
  }
  
  private void drawAssociationsInList(Collection<Artifact> artifactList, Bpmn2MemoryModel model) {
    for (Artifact artifact : artifactList) {
      
      if (artifact instanceof Association == false) {
        continue;
      } else {
        Association association = (Association) artifact;
        drawAssociation(association, model);
      }
    }
  }

  private void drawAssociation(Association association, Bpmn2MemoryModel model) {
    
    Anchor sourceAnchor = null;
    Anchor targetAnchor = null;
    BaseElement sourceElement = model.getFlowElement(association.getSourceRef());
    if (sourceElement == null) {
      sourceElement = model.getArtifact(association.getSourceRef());
    }
    if (sourceElement == null)
      return;
    ContainerShape sourceShape = (ContainerShape) getDiagramTypeProvider().getFeatureProvider()
            .getPictogramElementForBusinessObject(sourceElement);

    if (sourceShape == null)
      return;

    EList<Anchor> anchorList = sourceShape.getAnchors();
    for (Anchor anchor : anchorList) {
      if (anchor instanceof ChopboxAnchor) {
        sourceAnchor = anchor;
        break;
      }
    }

    BaseElement targetElement = model.getFlowElement(association.getTargetRef());
    if (targetElement == null) {
      targetElement = model.getArtifact(association.getTargetRef());
    }
    if (targetElement == null)
      return;
    ContainerShape targetShape = (ContainerShape) getDiagramTypeProvider().getFeatureProvider()
            .getPictogramElementForBusinessObject(targetElement);

    if (targetShape == null)
      return;

    anchorList = targetShape.getAnchors();
    for (Anchor anchor : anchorList) {
      if (anchor instanceof ChopboxAnchor) {
        targetAnchor = anchor;
        break;
      }
    }

    AddConnectionContext addContext = new AddConnectionContext(sourceAnchor, targetAnchor);

    List<GraphicInfo> bendpointList = new ArrayList<GraphicInfo>();
    if (model.getBpmnModel().getFlowLocationMap().containsKey(association.getId())) {
      List<GraphicInfo> pointList = model.getBpmnModel().getFlowLocationGraphicInfo(association.getId());
      if (pointList.size() > 2) {
        for (int i = 1; i < pointList.size() - 1; i++) {
          bendpointList.add(pointList.get(i));
        }
      }
    }

    addContext.putProperty("org.activiti.designer.bendpoints", bendpointList);

    addContext.setNewObject(association);
    getDiagramTypeProvider().getFeatureProvider().addIfPossible(addContext);
  }

  public IFile getModelFile() {
    return modelFile;
  }

  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    // hides grid on diagram, but you can reenable it
    if (getGraphicalViewer() != null && getGraphicalViewer().getEditPartRegistry() != null) {
      ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) getGraphicalViewer().getEditPartRegistry().get(LayerManager.ID);
      IFigure gridFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.GRID_LAYER);
      gridFigure.setVisible(false);
    }
    // setPartName("MyDiagram2");
  }

  @Override
  protected ContextMenuProvider createContextMenuProvider() {
    return new ActivitiEditorContextMenuProvider(getGraphicalViewer(), getActionRegistry(), getDiagramTypeProvider());
  }

  public static GraphicalViewer getActiveGraphicalViewer() {
    return activeGraphicalViewer;
  }

  @Override
  public void dispose() {
    super.dispose();
    ModelHandler.removeModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()));
    Bpmn2DiagramCreator.dispose(diagramFile);
  }
}
