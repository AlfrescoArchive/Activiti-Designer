package org.activiti.designer.features;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class ChangeElementTypeFeature extends AbstractCustomFeature {
	
  public static final String TASK_SERVICE = "servicetask";
  public static final String TASK_BUSINESSRULE = "businessruletask";
  public static final String TASK_MAIL = "mailtask";
  public static final String TASK_MANUAL = "manualtask";
  public static final String TASK_RECEIVE = "receivetask";
  public static final String TASK_SCRIPT = "scripttask";
  public static final String TASK_USER = "usertask";
  
  public static final String GATEWAY_EXCLUSIVE = "exclusivegateway";
  public static final String GATEWAY_INCLUSIVE = "inclusivegateway";
  public static final String GATEWAY_PARALLEL = "parallelgateway";
  public static final String GATEWAY_EVENT = "eventgateway";
  
  public static final String EVENT_START_NONE = "nonestartevent";
  public static final String EVENT_START_TIMER = "timerstartevent";
  public static final String EVENT_START_MESSAGE = "messagestartevent";
  public static final String EVENT_START_ERROR = "errorstartevent";
  
  public static final String EVENT_END_NONE = "noneendevent";
  public static final String EVENT_END_ERROR = "errorendevent";
  public static final String EVENT_END_TERMINATE = "terminateendevent";
  
  public static final String EVENT_BOUNDARY_TIMER = "timerboundaryevent";
  public static final String EVENT_BOUNDARY_ERROR = "errorboundaryevent";
  public static final String EVENT_BOUNDARY_MESSAGE = "messageboundaryevent";
  public static final String EVENT_BOUNDARY_SIGNAL = "signalboundaryevent";
  public static final String EVENT_BOUNDARY_COMPENSATE = "compensateboundaryevent";
  
  public static final String EVENT_CATCH_TIMER = "timercatchevent";
  public static final String EVENT_CATCH_MESSAGE = "messagecatchevent";
  public static final String EVENT_CATCH_SIGNAL = "signalcatchevent";
  
  public static final String EVENT_THROW_NONE = "nonethrowevent";
  public static final String EVENT_THROW_SIGNAL = "signalthrowevent";
  
  protected Map<String, AbstractCreateFeature> createFeatureMap = new HashMap<String, AbstractCreateFeature>();
  
	private String newType;
	
	public ChangeElementTypeFeature(IFeatureProvider fp) {
		super(fp);
		createFeatureMap.put(TASK_SERVICE, new CreateServiceTaskFeature(fp));
		createFeatureMap.put(TASK_BUSINESSRULE, new CreateBusinessRuleTaskFeature(fp));
		createFeatureMap.put(TASK_MAIL, new CreateMailTaskFeature(fp));
		createFeatureMap.put(TASK_MANUAL, new CreateManualTaskFeature(fp));
		createFeatureMap.put(TASK_RECEIVE, new CreateReceiveTaskFeature(fp));
		createFeatureMap.put(TASK_SCRIPT, new CreateScriptTaskFeature(fp));
		createFeatureMap.put(TASK_USER, new CreateUserTaskFeature(fp));
		
		createFeatureMap.put(GATEWAY_EXCLUSIVE, new CreateExclusiveGatewayFeature(fp));
		createFeatureMap.put(GATEWAY_INCLUSIVE, new CreateInclusiveGatewayFeature(fp));
		createFeatureMap.put(GATEWAY_PARALLEL, new CreateParallelGatewayFeature(fp));
		createFeatureMap.put(GATEWAY_EVENT, new CreateEventGatewayFeature(fp));
		
		createFeatureMap.put(EVENT_START_NONE, new CreateStartEventFeature(fp));
		createFeatureMap.put(EVENT_START_TIMER, new CreateTimerStartEventFeature(fp));
		createFeatureMap.put(EVENT_START_MESSAGE, new CreateMessageStartEventFeature(fp));
		createFeatureMap.put(EVENT_START_ERROR, new CreateErrorStartEventFeature(fp));
		
		createFeatureMap.put(EVENT_BOUNDARY_TIMER, new CreateBoundaryTimerFeature(fp));
    createFeatureMap.put(EVENT_BOUNDARY_ERROR, new CreateBoundaryErrorFeature(fp));
    createFeatureMap.put(EVENT_BOUNDARY_MESSAGE, new CreateBoundaryMessageFeature(fp));
    createFeatureMap.put(EVENT_BOUNDARY_SIGNAL, new CreateBoundarySignalFeature(fp));
    createFeatureMap.put(EVENT_BOUNDARY_COMPENSATE, new CreateBoundarySignalFeature(fp));
    
    createFeatureMap.put(EVENT_CATCH_TIMER, new CreateTimerCatchingEventFeature(fp));
    createFeatureMap.put(EVENT_CATCH_MESSAGE, new CreateMessageCatchingEventFeature(fp));
    createFeatureMap.put(EVENT_CATCH_SIGNAL, new CreateSignalCatchingEventFeature(fp));
    
    createFeatureMap.put(EVENT_THROW_NONE, new CreateNoneThrowingEventFeature(fp));
    createFeatureMap.put(EVENT_THROW_SIGNAL, new CreateSignalThrowingEventFeature(fp));
	}

	public ChangeElementTypeFeature(IFeatureProvider fp, String newType) {
		this(fp);
		this.newType = newType;
	}
	
	@Override
  public boolean canExecute(ICustomContext context) {
	  return true;
  }

	@Override
  public void execute(ICustomContext context) {
	  Shape element = (Shape) context.getProperty("org.activiti.designer.changetype.pictogram");
	  GraphicsAlgorithm elementGraphics = element.getGraphicsAlgorithm();
	  int x = elementGraphics.getX();
	  int y = elementGraphics.getY();
	  
	  CreateContext taskContext = new CreateContext();
	  ContainerShape targetContainer = (ContainerShape) element.getContainer();
  	taskContext.setTargetContainer(targetContainer);
  	taskContext.setLocation(x, y);
  	taskContext.setHeight(elementGraphics.getHeight());
  	taskContext.setWidth(elementGraphics.getWidth());
  	
  	FlowNode oldObject = (FlowNode) getFeatureProvider().getBusinessObjectForPictogramElement(element);
  	if (oldObject instanceof BoundaryEvent) {
  	  BoundaryEvent boundaryEvent = (BoundaryEvent) oldObject;
  	  ContainerShape parentShape = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(boundaryEvent.getAttachedToRef());
  	  taskContext.setTargetContainer(parentShape);
  	  taskContext.setLocation(x - parentShape.getGraphicsAlgorithm().getX(), y - parentShape.getGraphicsAlgorithm().getY());
  	}
	  
	  List<SequenceFlow> sourceList = oldObject.getOutgoingFlows();
	  List<SequenceFlow> targetList = oldObject.getIncomingFlows();
	  
	  taskContext.putProperty("org.activiti.designer.changetype.sourceflows", sourceList);
	  taskContext.putProperty("org.activiti.designer.changetype.targetflows", targetList);
	  taskContext.putProperty("org.activiti.designer.changetype.name", oldObject.getName());
	  
	  targetContainer.getChildren().remove(element);
	  List<Process> processes = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().getProcesses();
    for (Process process : processes) {
      process.removeFlowElement(oldObject.getId());
      for (Lane lane : process.getLanes()) {
        lane.getFlowReferences().remove(oldObject.getId());
      }
      removeElement(oldObject, process);
    }
	  
    if (createFeatureMap.containsKey(newType)) {
      createFeatureMap.get(newType).create(taskContext);
    }
  }
	
	private void removeElement(FlowElement element, BaseElement parentElement) {
	  Collection<FlowElement> elementList = null;
	  if (parentElement instanceof Process) {
	    elementList = ((Process) parentElement).getFlowElements();
	  } else if (parentElement instanceof SubProcess) {
	    elementList = ((SubProcess) parentElement).getFlowElements();
	    ((SubProcess) parentElement).getBoundaryEvents().remove(element);
	  }
	  
    for (FlowElement flowElement : elementList) {
      if (flowElement instanceof SubProcess) {
        SubProcess subProcess = (SubProcess) flowElement;
        subProcess.removeFlowElement(element.getId());
        removeElement(element, subProcess);
      }
      if (flowElement instanceof Activity) {
        ((Activity) flowElement).getBoundaryEvents().remove(element);
      }
    }
  }
}
