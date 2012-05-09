package org.activiti.designer.eclipse.editor;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.BaseElement;
import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.CustomProperty;
import org.activiti.designer.bpmn2.model.FieldExtension;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.FlowNode;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.eclipse.bpmn.BpmnParser;
import org.activiti.designer.eclipse.bpmn.SequenceFlowModel;
import org.activiti.designer.eclipse.ui.ActivitiEditorContextMenuProvider;
import org.activiti.designer.export.bpmn20.export.BPMN20ExportMarshaller;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.eclipse.ExtensionConstants;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.GraphicInfo;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.extension.ExtensionUtil;
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
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
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
import org.eclipse.swt.widgets.Composite;
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
	
	private BpmnParser parser;
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		
		try {
			if (input instanceof IFileEditorInput) { // Opened from the Open with... menu
				modelFile = ((IFileEditorInput) input).getFile();
				input = createNewDiagramEditorInput();
			} else if (input instanceof DiagramEditorInput) { // Opened by the default associated file extension .bpmn
				getModelPathFromInput((DiagramEditorInput) input);
				input = createNewDiagramEditorInput();
			} else if (input instanceof IURIEditorInput) { // Opened external to Eclipse
				IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
        java.net.URI uri= ((IURIEditorInput) input).getURI();
        String path = uri.getPath();
        if(wsRoot.getProject("import").exists() == false) {
        	wsRoot.getProject("import").create(null);
        }
        wsRoot.getProject("import").open(null);
        try {
        	InputStream inputStream = new FileInputStream(path);
        	String fileName = null;
        	if(path.contains("/")) {
        		fileName = path.substring(path.lastIndexOf("/") + 1);
        	} else {
        		fileName = path.substring(path.lastIndexOf("\\") + 1);
        	}
        	
        	if(wsRoot.getProject("import").getFile(fileName).exists()) {
        		wsRoot.getProject("import").getFile(fileName).delete(true, null);
        	}
        	
        	wsRoot.getProject("import").getFile(fileName).create(inputStream, true, null);
        	modelFile = wsRoot.getProject("import").getFile(fileName);
        	input = createNewDiagramEditorInput();
        } catch(Exception e) {
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
		IDiagramTypeProvider diagramTypeProvider = this.getDiagramTypeProvider();
		
		try {
			
			String diagramFileString = modelFile.getLocationURI().getPath();
			
			BPMN20ExportMarshaller marshaller = new BPMN20ExportMarshaller();
			marshaller.marshallDiagram(ModelHandler.getModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram())), 
					diagramFileString, diagramTypeProvider.getFeatureProvider());
			
			modelFile.refreshLocal(IResource.DEPTH_INFINITE, null);
			
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
		Bpmn2MemoryModel model = new Bpmn2MemoryModel(getDiagramTypeProvider().getFeatureProvider(), modelFile);
		ModelHandler.addModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()), model);
		
		String filePath = modelFile.getLocationURI().getPath();
		File bpmnFile = new File(filePath);
    try {
    	if(bpmnFile.exists() == false) {
  			bpmnFile.createNewFile();
  			modelFile.refreshLocal(IResource.DEPTH_INFINITE, null);
  		} else {
	    	FileInputStream fileStream = new FileInputStream(bpmnFile);
	      XMLInputFactory xif = XMLInputFactory.newInstance();
	      InputStreamReader in = new InputStreamReader(fileStream, "UTF-8");
	      XMLStreamReader xtr = xif.createXMLStreamReader(in);
	      parser = new BpmnParser();
	      parser.parseBpmn(xtr, model);
	      
	      BasicCommandStack basicCommandStack = (BasicCommandStack) getEditingDomain().getCommandStack();
	
	  		if (input instanceof DiagramEditorInput) {
	
	  			basicCommandStack.execute(new RecordingCommand(getEditingDomain()) {
	
	  				@Override
	  				protected void doExecute() {
	  					importDiagram();
	  				}
	  			});
	  		}
	  		basicCommandStack.saveIsDone();
	  		basicCommandStack.flush();
  		}
      
    } catch(Exception e) {
      e.printStackTrace();
    }
	}
	
	private void importDiagram() {
		final Diagram diagram = getDiagramTypeProvider().getDiagram();
		diagram.setActive(true);
		
		getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {
			@Override
			protected void doExecute() {
				
				Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
				
				if(model.getPools().size() > 0) {
				  for (Pool pool : model.getPools()) {
				    PictogramElement poolElement = addContainerElement(pool, model, diagram);
				    if(poolElement == null) continue;
				    
				    Process process = model.getProcess(pool.getId());
				    for (Lane lane : process.getLanes()) {
				      addContainerElement(lane, model, (ContainerShape) poolElement);
            }
          }
				}
				
				for (Process process : model.getProcesses()) {
				  drawFlowElements(process.getFlowElements(), model.getLocationMap(), diagram, process);
        }
				drawSequenceFlows(model.getProcesses());
			}
		});
	}
	
	private PictogramElement addContainerElement(BaseElement element, Bpmn2MemoryModel model, ContainerShape parent) {
	  GraphicInfo graphicInfo = model.getLocationMap().get(element.getId());
    if(graphicInfo == null) return null;
    
    final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();
    
    AddContext context = new AddContext(new AreaContext(), element);
    IAddFeature addFeature = featureProvider.getAddFeature(context);
    context.setNewObject(element);
    context.setSize(graphicInfo.width, graphicInfo.height);
    context.setTargetContainer(parent);
    
    int x = graphicInfo.x;
    int y = graphicInfo.y;
    
    if(parent instanceof Diagram == false) {
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
	
	private void drawFlowElements(List<FlowElement> elementList, Map<String, GraphicInfo> locationMap, 
	        ContainerShape parentShape, Process process) {
		
		final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();
		
		List<FlowElement> noDIList = new ArrayList<FlowElement>();
		for (FlowElement flowElement : elementList) {
			
			AddContext context = new AddContext(new AreaContext(), flowElement);
			IAddFeature addFeature = featureProvider.getAddFeature(context);
			
			if (addFeature == null) {
				System.out.println("Element not supported: " + flowElement);
				return;
			}
			
			GraphicInfo graphicInfo = locationMap.get(flowElement.getId());
			if(graphicInfo == null) {
			
				noDIList.add(flowElement);
				
			} else {
				
				context.setNewObject(flowElement);
				context.setSize(graphicInfo.width, graphicInfo.height);
				
				ContainerShape parentContainer = null;
				if(parentShape instanceof Diagram) {
				  parentContainer = getParentContainer(flowElement.getId(), process, (Diagram) parentShape);
				} else {
				  parentContainer = parentShape;
				}
				
				context.setTargetContainer(parentContainer);
				if(parentContainer instanceof Diagram == false) {
					Point location = getLocation(parentContainer);
					context.setLocation(graphicInfo.x - location.x, graphicInfo.y - location.y);
				} else {
					context.setLocation(graphicInfo.x, graphicInfo.y);
				}
				
				if(flowElement instanceof ServiceTask) {
					// Customize the name displayed by default
		      final List<CustomServiceTask> customServiceTasks = ExtensionUtil.getCustomServiceTasks(
		      		ActivitiUiUtil.getProjectFromDiagram(getDiagramTypeProvider().getDiagram()));

		      ServiceTask serviceTask = (ServiceTask) flowElement;
		      CustomServiceTask targetTask = null;

		      for (final CustomServiceTask customServiceTask : customServiceTasks) {
		        if (customServiceTask.getRuntimeClassname().equals(serviceTask.getImplementation())) {
		          targetTask = customServiceTask;
		          break;
		        }
		      }
		      
		      if(targetTask != null) {
		      	CustomProperty customServiceTaskProperty = new CustomProperty();

		        customServiceTaskProperty.setId(ExtensionUtil.wrapCustomPropertyId(serviceTask, ExtensionConstants.PROPERTY_ID_CUSTOM_SERVICE_TASK));
		        customServiceTaskProperty.setName(ExtensionConstants.PROPERTY_ID_CUSTOM_SERVICE_TASK);
		        customServiceTaskProperty.setSimpleValue(targetTask.getId());

		        serviceTask.getCustomProperties().add(customServiceTaskProperty);
		        
		        for (FieldExtension field : serviceTask.getFieldExtensions()) {
		        	CustomProperty customFieldProperty = new CustomProperty();
		        	customFieldProperty.setName(field.getFieldName());
		        	customFieldProperty.setSimpleValue(field.getExpression());
		        	serviceTask.getCustomProperties().add(customFieldProperty);
            }
		        
		        serviceTask.getFieldExtensions().clear();
		      }
				}
				
				if (addFeature.canAdd(context)) {
					PictogramElement newContainer = addFeature.add(context);
					featureProvider.link(newContainer, new Object[] { flowElement });
					
					if (flowElement instanceof SubProcess) {
						drawFlowElements(((SubProcess) flowElement).getFlowElements(), locationMap, 
						        (ContainerShape) newContainer, process);
					}
					
					if (flowElement instanceof Activity) {
						Activity activity = (Activity) flowElement;
						for (BoundaryEvent boundaryEvent : activity.getBoundaryEvents()) {
							AddContext boundaryContext = new AddContext(new AreaContext(), boundaryEvent);
							IAddFeature boundaryAddFeature = featureProvider.getAddFeature(boundaryContext);
							
							if (boundaryAddFeature == null) {
								System.out.println("Element not supported: " + boundaryEvent);
								return;
							}
							
							GraphicInfo boundaryGraphicInfo = locationMap.get(boundaryEvent.getId());
							if(boundaryGraphicInfo == null) {
								
								noDIList.add(boundaryEvent);
							
							} else {

								context.setNewObject(boundaryEvent);
								context.setSize(boundaryGraphicInfo.width, boundaryGraphicInfo.height);
								
								if(boundaryEvent.getAttachedToRef() != null) {
									ContainerShape container = (ContainerShape) featureProvider.getPictogramElementForBusinessObject(
											boundaryEvent.getAttachedToRef());
									
									if(container != null) {
									
										boundaryContext.setTargetContainer(container);
										Point location = getLocation(container);
										boundaryContext.setLocation(boundaryGraphicInfo.x - location.x, boundaryGraphicInfo.y - location.y);
					
										if (boundaryAddFeature.canAdd(boundaryContext)) {
											PictogramElement newBoundaryContainer = boundaryAddFeature.add(boundaryContext);
											featureProvider.link(newBoundaryContainer, new Object[] { boundaryEvent });
										}
									}
								}
							}
						}
					}
				}
			}
    }
		
		for (FlowElement flowElement : noDIList) {
			if(flowElement instanceof BoundaryEvent) {
				((BoundaryEvent) flowElement).getAttachedToRef().getBoundaryEvents().remove(flowElement);
			} else {
				elementList.remove(flowElement);
			}
		}
	}
	
	private ContainerShape getParentContainer(String flowElementId, Process process, Diagram diagram) {
	  Lane foundLane = null;
	  for (Lane lane : process.getLanes()) {
      for (String flowNodeRef : lane.getFlowReferences()) {
        if(flowNodeRef.equals(flowElementId)) {
          foundLane = lane;
          break;
        }
      }
    }
	  
	  if(foundLane != null) {
	    final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();
	    return (ContainerShape) featureProvider.getPictogramElementForBusinessObject(foundLane);
	  } else {
	    return diagram;
	  }
	}
	
	private Point getLocation(ContainerShape containerShape) {
		if(containerShape instanceof Diagram == true) {
			return new Point(containerShape.getGraphicsAlgorithm().getX(), containerShape.getGraphicsAlgorithm().getY());
		}
			
		Point location = getLocation(containerShape.getContainer());
		return new Point(location.x + containerShape.getGraphicsAlgorithm().getX(), location.y + containerShape.getGraphicsAlgorithm().getY());
	}
	
	private void drawSequenceFlows(List<Process> processes) {
    int sequenceCounter = 1;
    for(SequenceFlowModel sequenceFlowModel : parser.sequenceFlowList) {
      SequenceFlow sequenceFlow = new SequenceFlow();
      if(StringUtils.isEmpty(sequenceFlowModel.id) || sequenceFlowModel.id.matches("sid-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}")) {
      	sequenceFlow.setId("flow" + sequenceCounter);
      	sequenceCounter++;
      } else {
      	sequenceFlow.setId(sequenceFlowModel.id);
      }
      
      if(StringUtils.isNotEmpty(sequenceFlowModel.name)) {
        sequenceFlow.setName(sequenceFlowModel.name);
      }
      
      sequenceFlow.setSourceRef(getFlowNode(sequenceFlowModel.sourceRef, processes));
      sequenceFlow.setTargetRef(getFlowNode(sequenceFlowModel.targetRef, processes));
      
      if(sequenceFlow.getSourceRef() == null || sequenceFlow.getSourceRef().getId() == null || 
              sequenceFlow.getTargetRef() == null || sequenceFlow.getTargetRef().getId() == null) continue;
      if(sequenceFlowModel.conditionExpression != null) {
        sequenceFlow.setConditionExpression(sequenceFlowModel.conditionExpression);
      }
      if(sequenceFlowModel.listenerList.size() > 0) {
        sequenceFlow.getExecutionListeners().addAll(sequenceFlowModel.listenerList);
      }
      
      SubProcess subProcessContainsFlow = null;
      for (FlowElement flowElement : sequenceFlowModel.parentProcess.getFlowElements()) {
	      if(flowElement instanceof SubProcess) {
	      	SubProcess subProcess = (SubProcess) flowElement;
	      	if(subProcess.getFlowElements().contains(sequenceFlow.getSourceRef())) {
	      		subProcessContainsFlow = subProcess;
	      	}
	      }
      }
      
      if(subProcessContainsFlow != null) {
      	subProcessContainsFlow.getFlowElements().add(sequenceFlow);
      } else {
        sequenceFlowModel.parentProcess.getFlowElements().add(sequenceFlow);
      }
      
      sequenceFlow.getSourceRef().getOutgoing().add(sequenceFlow);
      sequenceFlow.getTargetRef().getIncoming().add(sequenceFlow);
      
      Anchor sourceAnchor = null;
      Anchor targetAnchor = null;
      ContainerShape sourceShape = (ContainerShape) getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(sequenceFlow.getSourceRef());
      
      if(sourceShape == null) continue;
      
      EList<Anchor> anchorList = sourceShape.getAnchors();
      for (Anchor anchor : anchorList) {
        if(anchor instanceof ChopboxAnchor) {
          sourceAnchor = anchor;
          break;
        }
      }
      
      ContainerShape targetShape = (ContainerShape) getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(sequenceFlow.getTargetRef());
      
      if(targetShape == null) continue;
      
      anchorList = targetShape.getAnchors();
      for (Anchor anchor : anchorList) {
        if(anchor instanceof ChopboxAnchor) {
          targetAnchor = anchor;
          break;
        }
      }
      
      AddConnectionContext addContext = new AddConnectionContext(sourceAnchor, targetAnchor);
      
      List<GraphicInfo> bendpointList = new ArrayList<GraphicInfo>();
      for (String sequenceGraphElement : parser.flowLocationMap.keySet()) {
	      if(sequenceFlowModel.id.equalsIgnoreCase(sequenceGraphElement)) {
	      	List<GraphicInfo> pointList = parser.flowLocationMap.get(sequenceGraphElement);
	      	if(pointList.size() > 2) {
	      		for(int i = 1; i < pointList.size() - 1; i++) {
	      			bendpointList.add(pointList.get(i));
	      		}
	      	}
	      }
      }
    
      addContext.putProperty("org.activiti.designer.bendpoints", bendpointList);
      
      addContext.setNewObject(sequenceFlow);
      getDiagramTypeProvider().getFeatureProvider().addIfPossible(addContext);
    }
  }
	
	private FlowNode getFlowNode(String elementid, List<Process> processes) {
	  FlowNode flowNode = null;
	  for (Process process : processes) {
	    FlowNode processFlowNode = getFlowNodeInProcess(elementid, process.getFlowElements());
      if(processFlowNode != null) {
        flowNode = processFlowNode;
        break;
      }
    }
	  return flowNode;
	}
	
	private FlowNode getFlowNodeInProcess(String elementid, List<FlowElement> elementList) {
    FlowNode flowNode = null;
    for(FlowElement flowElement : elementList) {
      if(flowElement.getId().equalsIgnoreCase(elementid)) {
        flowNode = (FlowNode) flowElement;
        break;
      }
      
      if(flowElement instanceof SubProcess) {
        FlowNode subFlowNode = getFlowNodeInProcess(elementid, ((SubProcess) flowElement).getFlowElements());
        if(subFlowNode != null) {
          flowNode = subFlowNode;
          break;
        }
      }
      
      if(flowElement instanceof Activity) {
      	List<BoundaryEvent> eventList = ((Activity) flowElement).getBoundaryEvents();
      	for (BoundaryEvent boundaryEvent : eventList) {
	        if(boundaryEvent.getId().equalsIgnoreCase(elementid)) {
	        	flowNode = boundaryEvent;
	        	break;
	        }
        }
      }
    }
    return flowNode;
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
	}

	@Override
	protected ContextMenuProvider createContextMenuProvider() {
		return new ActivitiEditorContextMenuProvider(getGraphicalViewer(),
				getActionRegistry(), getDiagramTypeProvider());
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
