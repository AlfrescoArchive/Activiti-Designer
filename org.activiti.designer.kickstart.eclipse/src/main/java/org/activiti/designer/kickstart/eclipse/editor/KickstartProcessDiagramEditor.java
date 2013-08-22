package org.activiti.designer.kickstart.eclipse.editor;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.kickstart.eclipse.ui.ActivitiEditorContextMenuProvider;
import org.activiti.designer.kickstart.eclipse.util.FileService;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.style.StyleUtil;
import org.activiti.workflow.simple.converter.json.SimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.command.BasicCommandStack;
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
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class KickstartProcessDiagramEditor extends DiagramEditor {

  private static GraphicalViewer activeGraphicalViewer;

  private KickstartProcessChangeListener activitiBpmnModelChangeListener;

  private TransactionalEditingDomain transactionalEditingDomain;

  public KickstartProcessDiagramEditor() {
    super();
  }

  @Override
  protected void registerBusinessObjectsListener() {
    activitiBpmnModelChangeListener = new KickstartProcessChangeListener(this);

    final TransactionalEditingDomain ted = getEditingDomain();
    ted.addResourceSetListener(activitiBpmnModelChangeListener);
  }

  @Override
  public TransactionalEditingDomain getEditingDomain() {
    TransactionalEditingDomain ted = super.getEditingDomain();

    if (ted == null) {
      ted = transactionalEditingDomain;
    }

    return ted;
  }

  @Override
  public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    IEditorInput finalInput = null;

    try {
      if (input instanceof KickstartProcessDiagramEditorInput) {
        finalInput = input;
      } else {
        finalInput = createNewDiagramEditorInput(input);
      }
    } catch (CoreException exception) {
      exception.printStackTrace();
    }

    super.init(site, finalInput);
  }

  private KickstartProcessDiagramEditorInput createNewDiagramEditorInput(final IEditorInput input) throws CoreException {

    final IFile dataFile = FileService.getDataFileForInput(input);

    // now generate the temporary diagram file
    final IPath dataFilePath = dataFile.getFullPath();

    // get or create the corresponding temporary folder
    final IFolder tempFolder = FileService.getOrCreateTempFolder(dataFilePath);

    // finally get the diagram file that corresponds to the data file
    final IFile diagramFile = FileService.getTemporaryDiagramFile(dataFilePath, tempFolder);

    // Create new temporary diagram file
    KickstartProcessDiagramCreator creator = new KickstartProcessDiagramCreator();

    return creator.creatProcessDiagram(dataFile, diagramFile, this, null, false);
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    super.doSave(monitor);

    final KickstartProcessDiagramEditorInput adei = (KickstartProcessDiagramEditorInput) getEditorInput();

    try {
      final IFile dataFile = adei.getDataFile();
      final String diagramFileString = dataFile.getLocationURI().getPath();

      KickstartProcessMemoryModel model = ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()));

      SimpleWorkflowJsonConverter converter = new SimpleWorkflowJsonConverter();
      File objectsFile = new File(diagramFileString);
      FileWriter writer = new FileWriter(objectsFile);
      converter.writeWorkflowDefinition(model.getWorkflowDefinition(), writer);
      writer.close();
      
      dataFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ((BasicCommandStack) getEditingDomain().getCommandStack()).saveIsDone();
    updateDirtyState();
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

    final KickstartProcessDiagramEditorInput adei = (KickstartProcessDiagramEditorInput) input;
    final IFile dataFile = adei.getDataFile();

    final KickstartProcessMemoryModel model = new KickstartProcessMemoryModel(getDiagramTypeProvider().getFeatureProvider(), dataFile);
    ModelHandler.addModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()), model);

    String filePath = dataFile.getLocationURI().getPath();
    File kickstartProcessFile = new File(filePath);
    try {
      if (kickstartProcessFile.exists() == false) {
        model.setWorkflowDefinition(new WorkflowDefinition());
        kickstartProcessFile.createNewFile();
        dataFile.refreshLocal(IResource.DEPTH_INFINITE, null);
      } else {
        FileInputStream fileStream = new FileInputStream(kickstartProcessFile);
        SimpleWorkflowJsonConverter converter = new SimpleWorkflowJsonConverter();
        WorkflowDefinition definition = null;
        try {
          definition = converter.readWorkflowDefinition(fileStream);
        } catch(Exception e) {
          definition = new WorkflowDefinition();
        }
        model.setWorkflowDefinition(definition);

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

  private void importDiagram(final KickstartProcessMemoryModel model) {
    final Diagram diagram = getDiagramTypeProvider().getDiagram();
    diagram.setActive(true);

    getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {

      @Override
      protected void doExecute() {
        addContainerElement(diagram, model);
              
        for (StepDefinition step : model.getWorkflowDefinition().getSteps()) {
          // draw step
        }
      }
    });
  }

  private void addContainerElement(ContainerShape parent, final KickstartProcessMemoryModel model) {
    final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(parent, true);
    final IGaService gaService = Graphiti.getGaService();
    
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, 40, 40, 400, 400);
    
    RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(invisibleRectangle, 20, 20);
    gaService.setLocationAndSize(roundedRectangle, 0, 0, 400, 400);
    roundedRectangle.setStyle(StyleUtil.getStyleForPool(getDiagramTypeProvider().getDiagram()));
    featureProvider.link(containerShape, model.getWorkflowDefinition());
  }

  private void drawFlowElements(Collection<FlowElement> elementList, Map<String, GraphicInfo> locationMap, ContainerShape parentShape, Process process) {

    final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();

    List<FlowElement> noDIList = new ArrayList<FlowElement>();
    for (FlowElement flowElement : elementList) {

      if (flowElement instanceof SequenceFlow) {
        continue;
      }

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
        context.setSize((int) graphicInfo.getWidth(), (int) graphicInfo.getHeight());

        ContainerShape parentContainer = null;
        if (parentShape instanceof Diagram) {
          parentContainer = getParentContainer(flowElement.getId(), process, (Diagram) parentShape);
        } else {
          parentContainer = parentShape;
        }
        
        context.setTargetContainer(parentContainer);
        if (parentContainer instanceof Diagram == false) {
          Point location = getLocation(parentContainer);
          context.setLocation((int) graphicInfo.getX() - location.x, (int) graphicInfo.getY() - location.y);
        } else {
          context.setLocation((int) graphicInfo.getX(), (int) graphicInfo.getY());
        }

        if (flowElement instanceof BoundaryEvent) {
          BoundaryEvent boundaryEvent = (BoundaryEvent) flowElement;
          if (boundaryEvent.getAttachedToRef() != null) {
            ContainerShape container = (ContainerShape) featureProvider.getPictogramElementForBusinessObject(boundaryEvent.getAttachedToRef());

            if (container != null) {
              AddContext boundaryContext = new AddContext(new AreaContext(), boundaryEvent);
              boundaryContext.setTargetContainer(container);
              Point location = getLocation(container);
              boundaryContext.setLocation((int) graphicInfo.getX() - location.x, (int) graphicInfo.getY() - location.y);

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

  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    // hides grid on diagram, but you can reenable it
    if (getGraphicalViewer() != null && getGraphicalViewer().getEditPartRegistry() != null) {
      ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) getGraphicalViewer().getEditPartRegistry().get(LayerManager.ID);
      IFigure gridFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.GRID_LAYER);
      gridFigure.setVisible(false);
    }
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

    final KickstartProcessDiagramEditorInput adei = (KickstartProcessDiagramEditorInput) getEditorInput();

    ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()));
    KickstartProcessDiagramCreator.dispose(adei.getDiagramFile());
  }
}
