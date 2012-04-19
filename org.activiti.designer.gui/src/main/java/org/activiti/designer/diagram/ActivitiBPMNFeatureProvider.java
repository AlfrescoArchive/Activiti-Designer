package org.activiti.designer.diagram;

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.BusinessRuleTask;
import org.activiti.designer.bpmn2.model.CallActivity;
import org.activiti.designer.bpmn2.model.EndEvent;
import org.activiti.designer.bpmn2.model.ErrorEventDefinition;
import org.activiti.designer.bpmn2.model.Event;
import org.activiti.designer.bpmn2.model.EventDefinition;
import org.activiti.designer.bpmn2.model.EventGateway;
import org.activiti.designer.bpmn2.model.EventSubProcess;
import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.Gateway;
import org.activiti.designer.bpmn2.model.InclusiveGateway;
import org.activiti.designer.bpmn2.model.IntermediateCatchEvent;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.MailTask;
import org.activiti.designer.bpmn2.model.ManualTask;
import org.activiti.designer.bpmn2.model.ParallelGateway;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.ReceiveTask;
import org.activiti.designer.bpmn2.model.ScriptTask;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.bpmn2.model.SignalEventDefinition;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.Task;
import org.activiti.designer.bpmn2.model.ThrowEvent;
import org.activiti.designer.bpmn2.model.TimerEventDefinition;
import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoScriptTask;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoStartEvent;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoUserTask;
import org.activiti.designer.features.AddBoundaryErrorFeature;
import org.activiti.designer.features.AddBoundarySignalFeature;
import org.activiti.designer.features.AddBoundaryTimerFeature;
import org.activiti.designer.features.AddBusinessRuleTaskFeature;
import org.activiti.designer.features.AddCallActivityFeature;
import org.activiti.designer.features.AddEmbeddedSubProcessFeature;
import org.activiti.designer.features.AddEndEventFeature;
import org.activiti.designer.features.AddErrorEndEventFeature;
import org.activiti.designer.features.AddErrorStartEventFeature;
import org.activiti.designer.features.AddEventGatewayFeature;
import org.activiti.designer.features.AddEventSubProcessFeature;
import org.activiti.designer.features.AddExclusiveGatewayFeature;
import org.activiti.designer.features.AddInclusiveGatewayFeature;
import org.activiti.designer.features.AddLaneFeature;
import org.activiti.designer.features.AddMailTaskFeature;
import org.activiti.designer.features.AddManualTaskFeature;
import org.activiti.designer.features.AddNoneThrowingEventFeature;
import org.activiti.designer.features.AddParallelGatewayFeature;
import org.activiti.designer.features.AddPoolFeature;
import org.activiti.designer.features.AddReceiveTaskFeature;
import org.activiti.designer.features.AddScriptTaskFeature;
import org.activiti.designer.features.AddSequenceFlowFeature;
import org.activiti.designer.features.AddServiceTaskFeature;
import org.activiti.designer.features.AddSignalCatchingEventFeature;
import org.activiti.designer.features.AddSignalThrowingEventFeature;
import org.activiti.designer.features.AddStartEventFeature;
import org.activiti.designer.features.AddTimerCatchingEventFeature;
import org.activiti.designer.features.AddTimerStartEventFeature;
import org.activiti.designer.features.AddUserTaskFeature;
import org.activiti.designer.features.ChangeElementTypeFeature;
import org.activiti.designer.features.ContainerResizeFeature;
import org.activiti.designer.features.CopyFlowElementFeature;
import org.activiti.designer.features.CreateBoundaryErrorFeature;
import org.activiti.designer.features.CreateBoundarySignalFeature;
import org.activiti.designer.features.CreateBoundaryTimerFeature;
import org.activiti.designer.features.CreateBusinessRuleTaskFeature;
import org.activiti.designer.features.CreateCallActivityFeature;
import org.activiti.designer.features.CreateEmbeddedSubProcessFeature;
import org.activiti.designer.features.CreateEndEventFeature;
import org.activiti.designer.features.CreateErrorEndEventFeature;
import org.activiti.designer.features.CreateErrorStartEventFeature;
import org.activiti.designer.features.CreateEventGatewayFeature;
import org.activiti.designer.features.CreateEventSubProcessFeature;
import org.activiti.designer.features.CreateExclusiveGatewayFeature;
import org.activiti.designer.features.CreateInclusiveGatewayFeature;
import org.activiti.designer.features.CreateLaneFeature;
import org.activiti.designer.features.CreateMailTaskFeature;
import org.activiti.designer.features.CreateManualTaskFeature;
import org.activiti.designer.features.CreateNoneThrowingEventFeature;
import org.activiti.designer.features.CreateParallelGatewayFeature;
import org.activiti.designer.features.CreatePoolFeature;
import org.activiti.designer.features.CreateReceiveTaskFeature;
import org.activiti.designer.features.CreateScriptTaskFeature;
import org.activiti.designer.features.CreateSequenceFlowFeature;
import org.activiti.designer.features.CreateServiceTaskFeature;
import org.activiti.designer.features.CreateSignalCatchingEventFeature;
import org.activiti.designer.features.CreateSignalThrowingEventFeature;
import org.activiti.designer.features.CreateStartEventFeature;
import org.activiti.designer.features.CreateTimerCatchingEventFeature;
import org.activiti.designer.features.CreateTimerStartEventFeature;
import org.activiti.designer.features.CreateUserTaskFeature;
import org.activiti.designer.features.DeleteFlowElementFeature;
import org.activiti.designer.features.DeleteLaneFeature;
import org.activiti.designer.features.DeletePoolFeature;
import org.activiti.designer.features.DeleteSequenceFlowFeature;
import org.activiti.designer.features.DirectEditFlowElementFeature;
import org.activiti.designer.features.MoveActivityFeature;
import org.activiti.designer.features.MoveBoundaryEventFeature;
import org.activiti.designer.features.MoveEventFeature;
import org.activiti.designer.features.MoveGatewayFeature;
import org.activiti.designer.features.MoveLaneFeature;
import org.activiti.designer.features.PasteFlowElementFeature;
import org.activiti.designer.features.ReconnectSequenceFlowFeature;
import org.activiti.designer.features.SaveBpmnModelFeature;
import org.activiti.designer.features.TaskResizeFeature;
import org.activiti.designer.features.UpdateFlowElementFeature;
import org.activiti.designer.features.UpdatePoolAndLaneFeature;
import org.activiti.designer.util.editor.POJOIndependenceSolver;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

import com.alfresco.designer.gui.features.AddAlfrescoScriptTaskFeature;
import com.alfresco.designer.gui.features.AddAlfrescoStartEventFeature;
import com.alfresco.designer.gui.features.AddAlfrescoUserTaskFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoMailTaskFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoScriptTaskFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoStartEventFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoUserTaskFeature;

public class ActivitiBPMNFeatureProvider extends DefaultFeatureProvider {

	public POJOIndependenceSolver independenceResolver;
	
	public ActivitiBPMNFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		setIndependenceSolver(new POJOIndependenceSolver());
		independenceResolver = (POJOIndependenceSolver) getIndependenceSolver();
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		// is object for add request a EClass?
		if (context.getNewObject() instanceof StartEvent) {
		  if(context.getNewObject() instanceof AlfrescoStartEvent) {
		    return new AddAlfrescoStartEventFeature(this);
		  } else {
		  	if(((StartEvent) context.getNewObject()).getEventDefinitions().size() > 0) {
		  		if(((StartEvent) context.getNewObject()).getEventDefinitions().get(0) instanceof TimerEventDefinition) {
		  			return new AddTimerStartEventFeature(this);
		  		} else {
		  			return new AddErrorStartEventFeature(this);
		  		}
		  	} else {
		  		return new AddStartEventFeature(this);
		  	}
		  }
		} else if (context.getNewObject() instanceof EndEvent) {
		  if(((EndEvent) context.getNewObject()).getEventDefinitions().size() > 0) {
		    return new AddErrorEndEventFeature(this);
		  } else {
		    return new AddEndEventFeature(this);
		  }
		} else if (context.getNewObject() instanceof SequenceFlow) {
		  return new AddSequenceFlowFeature(this);
		} else if (context.getNewObject() instanceof UserTask) {
		  if(context.getNewObject() instanceof AlfrescoUserTask) {
		    return new AddAlfrescoUserTaskFeature(this);
		  } else {
		    return new AddUserTaskFeature(this);
		  }
		} else if (context.getNewObject() instanceof MailTask) {
		  return new AddMailTaskFeature(this);
		} else if (context.getNewObject() instanceof ManualTask) {
		  return new AddManualTaskFeature(this);
		} else if (context.getNewObject() instanceof ReceiveTask) {
		  return new AddReceiveTaskFeature(this);
		} else if (context.getNewObject() instanceof BusinessRuleTask) {
      return new AddBusinessRuleTaskFeature(this);
		} else if (context.getNewObject() instanceof ScriptTask) {
		  return new AddScriptTaskFeature(this);
		} else if (context.getNewObject() instanceof ServiceTask) {
		  return new AddServiceTaskFeature(this);
		} else if (context.getNewObject() instanceof ExclusiveGateway) {
		  return new AddExclusiveGatewayFeature(this);
		} else if (context.getNewObject() instanceof InclusiveGateway) {
		  return new AddInclusiveGatewayFeature(this);
		} else if (context.getNewObject() instanceof ParallelGateway) {
		  return new AddParallelGatewayFeature(this);
		} else if (context.getNewObject() instanceof EventGateway) {
      return new AddEventGatewayFeature(this);
		} else if (context.getNewObject() instanceof BoundaryEvent) {
		  if(((BoundaryEvent) context.getNewObject()).getEventDefinitions().size() > 0) {
		    EventDefinition definition = ((BoundaryEvent) context.getNewObject()).getEventDefinitions().get(0);
		    if(definition instanceof ErrorEventDefinition) {
		      return new AddBoundaryErrorFeature(this);
		    } else if(definition instanceof SignalEventDefinition) {
		    	return new AddBoundarySignalFeature(this);
		    } else {
		      return new AddBoundaryTimerFeature(this);
		    }
		  }
		} else if (context.getNewObject() instanceof IntermediateCatchEvent) {
			if(((IntermediateCatchEvent) context.getNewObject()).getEventDefinitions().size() > 0) {
		    EventDefinition definition = ((IntermediateCatchEvent) context.getNewObject()).getEventDefinitions().get(0);
		    if(definition instanceof SignalEventDefinition) {
		    	return new AddSignalCatchingEventFeature(this);
		    } else {
		      return new AddTimerCatchingEventFeature(this);
		    }
		  }
		} else if (context.getNewObject() instanceof ThrowEvent) {
		  if(((ThrowEvent) context.getNewObject()).getEventDefinitions().size() > 0) {
		    return new AddSignalThrowingEventFeature(this);
		  } else {
		    return new AddNoneThrowingEventFeature(this);
		  }
		} else if (context.getNewObject() instanceof EventSubProcess) {
      return new AddEventSubProcessFeature(this);
		} else if (context.getNewObject() instanceof SubProcess) {
      return new AddEmbeddedSubProcessFeature(this);
		} else if (context.getNewObject() instanceof Pool) {
      return new AddPoolFeature(this);
		} else if (context.getNewObject() instanceof Lane) {
      return new AddLaneFeature(this);
		} else if (context.getNewObject() instanceof CallActivity) {
			return new AddCallActivityFeature(this);
		} else if (context.getNewObject() instanceof AlfrescoScriptTask) {
      return new AddAlfrescoScriptTaskFeature(this);
    }
		return super.getAddFeature(context);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return new ICreateFeature[] { new CreateAlfrescoStartEventFeature(this),
						new CreateStartEventFeature(this),
						new CreateTimerStartEventFeature(this),
						new CreateErrorStartEventFeature(this),
		        new CreateEndEventFeature(this),
		        new CreateErrorEndEventFeature(this),
		        new CreateUserTaskFeature(this),
		        new CreateAlfrescoUserTaskFeature(this),
		        new CreateScriptTaskFeature(this), 
		        new CreateServiceTaskFeature(this),
		        new CreateMailTaskFeature(this), 
		        new CreateManualTaskFeature(this), 
		        new CreateReceiveTaskFeature(this),
		        new CreateBusinessRuleTaskFeature(this),
		        new CreateParallelGatewayFeature(this), 
		        new CreateExclusiveGatewayFeature(this), 
		        new CreateInclusiveGatewayFeature(this),
		        new CreateEventGatewayFeature(this),
		        new CreateBoundaryTimerFeature(this),
		        new CreateBoundaryErrorFeature(this),
		        new CreateBoundarySignalFeature(this),
		        new CreateTimerCatchingEventFeature(this),
		        new CreateSignalCatchingEventFeature(this),
		        new CreateSignalThrowingEventFeature(this),
		        new CreateNoneThrowingEventFeature(this),
		        new CreateEventSubProcessFeature(this), 
		        new CreateEmbeddedSubProcessFeature(this),
		        new CreatePoolFeature(this),
		        new CreateLaneFeature(this),
		        new CreateCallActivityFeature(this),
		        new CreateAlfrescoScriptTaskFeature(this),
		        new CreateAlfrescoMailTaskFeature(this)};
	}
	
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
	  PictogramElement pictogramElement = context.getPictogramElement();
    Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    if(bo instanceof FlowElement) {
      return new DeleteFlowElementFeature(this);
    } else if(bo instanceof Lane) {
      return new DeleteLaneFeature(this);
    }
	  return super.getDeleteFeature(context);
	}

	@Override
	public ICopyFeature getCopyFeature(ICopyContext context) {
		return new CopyFlowElementFeature(this);
	}

	@Override
	public IPasteFeature getPasteFeature(IPasteContext context) {
		return new PasteFlowElementFeature(this);
	}
	
	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] { new CreateSequenceFlowFeature(this) };
	}
	
	@Override
	public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
	  return new ReconnectSequenceFlowFeature(this);
	}
	
	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof FlowElement) {
				return new UpdateFlowElementFeature(this);
			} else if (bo instanceof Pool || bo instanceof Lane) {
			  return new UpdatePoolAndLaneFeature(this);
			}
		}
		return super.getUpdateFeature(context);
	}

	@Override
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
		// simply return all create connection features
		return getCreateConnectionFeatures();
	}
	
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof FlowElement) {
			return new DirectEditFlowElementFeature(this);
		}
		return super.getDirectEditingFeature(context);
	}

	@Override
  public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
	  Shape shape = context.getShape();
    Object bo = getBusinessObjectForPictogramElement(shape);
    if (bo instanceof SubProcess || bo instanceof Pool || bo instanceof Lane) {
    	return new ContainerResizeFeature(this);
    } else if (bo instanceof Task || bo instanceof CallActivity) {
    	return new TaskResizeFeature(this);
    }
    return super.getResizeShapeFeature(context);
  }

  @Override
  public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
    Shape shape = context.getShape();
    Object bo = getBusinessObjectForPictogramElement(shape);
    if(bo instanceof BoundaryEvent) {
      return new MoveBoundaryEventFeature(this);
    
    } else if (bo instanceof Activity) {
    	// in case an activity is moved, make sure, attached boundary events will move too
    	return new MoveActivityFeature(this);
    
    } else if (bo instanceof Gateway) {
      return new MoveGatewayFeature(this);
    
    } else if (bo instanceof Event) {
      return new MoveEventFeature(this);
      
    } else if (bo instanceof Lane) {
      return new MoveLaneFeature(this);
    } 
    return super.getMoveShapeFeature(context);
  }

  @Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		return new ICustomFeature[] { new SaveBpmnModelFeature(this), 
				new DeleteSequenceFlowFeature(this), new DeletePoolFeature(this), new ChangeElementTypeFeature(this) };
	}

}
