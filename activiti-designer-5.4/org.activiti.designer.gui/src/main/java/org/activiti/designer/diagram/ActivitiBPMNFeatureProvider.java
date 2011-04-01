package org.activiti.designer.diagram;

import org.activiti.designer.features.AddBoundaryErrorFeature;
import org.activiti.designer.features.AddBoundaryTimerFeature;
import org.activiti.designer.features.AddBusinessRuleTaskFeature;
import org.activiti.designer.features.AddCallActivityFeature;
import org.activiti.designer.features.AddEmbeddedSubProcessFeature;
import org.activiti.designer.features.AddEndEventFeature;
import org.activiti.designer.features.AddErrorEndEventFeature;
import org.activiti.designer.features.AddExclusiveGatewayFeature;
import org.activiti.designer.features.AddMailTaskFeature;
import org.activiti.designer.features.AddManualTaskFeature;
import org.activiti.designer.features.AddParallelGatewayFeature;
import org.activiti.designer.features.AddReceiveTaskFeature;
import org.activiti.designer.features.AddScriptTaskFeature;
import org.activiti.designer.features.AddSequenceFlowFeature;
import org.activiti.designer.features.AddServiceTaskFeature;
import org.activiti.designer.features.AddStartEventFeature;
import org.activiti.designer.features.AddUserTaskFeature;
import org.activiti.designer.features.CopyFlowElementFeature;
import org.activiti.designer.features.CreateBoundaryErrorFeature;
import org.activiti.designer.features.CreateBoundaryTimerFeature;
import org.activiti.designer.features.CreateBusinessRuleTaskFeature;
import org.activiti.designer.features.CreateCallActivityFeature;
import org.activiti.designer.features.CreateEmbeddedSubProcessFeature;
import org.activiti.designer.features.CreateEndEventFeature;
import org.activiti.designer.features.CreateErrorEndEventFeature;
import org.activiti.designer.features.CreateExclusiveGatewayFeature;
import org.activiti.designer.features.CreateMailTaskFeature;
import org.activiti.designer.features.CreateManualTaskFeature;
import org.activiti.designer.features.CreateParallelGatewayFeature;
import org.activiti.designer.features.CreateReceiveTaskFeature;
import org.activiti.designer.features.CreateScriptTaskFeature;
import org.activiti.designer.features.CreateSequenceFlowFeature;
import org.activiti.designer.features.CreateServiceTaskFeature;
import org.activiti.designer.features.CreateStartEventFeature;
import org.activiti.designer.features.CreateUserTaskFeature;
import org.activiti.designer.features.DeleteFlowElementFeature;
import org.activiti.designer.features.DeleteSequenceFlowFeature;
import org.activiti.designer.features.DirectEditFlowElementFeature;
import org.activiti.designer.features.MoveBoundaryEventFeature;
import org.activiti.designer.features.PasteFlowElementFeature;
import org.activiti.designer.features.SaveBpmnModelFeature;
import org.activiti.designer.features.SubProcessResizeFeature;
import org.activiti.designer.features.UpdateFlowElementFeature;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.MailTask;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.UserTask;
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
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

public class ActivitiBPMNFeatureProvider extends DefaultFeatureProvider {

	public ActivitiBPMNFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		// is object for add request a EClass?
		if (context.getNewObject() instanceof StartEvent) {
			return new AddStartEventFeature(this);
		} else if (context.getNewObject() instanceof EndEvent) {
		  if(((EndEvent) context.getNewObject()).getEventDefinitions().size() > 0) {
		    return new AddErrorEndEventFeature(this);
		  } else {
		    return new AddEndEventFeature(this);
		  }
		} else if (context.getNewObject() instanceof SequenceFlow) {
			return new AddSequenceFlowFeature(this);
		} else if (context.getNewObject() instanceof UserTask) {
			return new AddUserTaskFeature(this);
		} else if (context.getNewObject() instanceof ScriptTask) {
			return new AddScriptTaskFeature(this);
		} else if (context.getNewObject() instanceof ServiceTask) {
			return new AddServiceTaskFeature(this);
		} else if (context.getNewObject() instanceof MailTask) {
			return new AddMailTaskFeature(this);
		} else if (context.getNewObject() instanceof ManualTask) {
			return new AddManualTaskFeature(this);
		} else if (context.getNewObject() instanceof ReceiveTask) {
			return new AddReceiveTaskFeature(this);
		} else if (context.getNewObject() instanceof BusinessRuleTask) {
      return new AddBusinessRuleTaskFeature(this);
		} else if (context.getNewObject() instanceof ExclusiveGateway) {
			return new AddExclusiveGatewayFeature(this);
		} else if (context.getNewObject() instanceof ParallelGateway) {
			return new AddParallelGatewayFeature(this);
		} else if (context.getNewObject() instanceof BoundaryEvent) {
		  if(((BoundaryEvent) context.getNewObject()).getEventDefinitions().size() > 0) {
		    EventDefinition definition = ((BoundaryEvent) context.getNewObject()).getEventDefinitions().get(0);
		    if(definition instanceof ErrorEventDefinition) {
		      return new AddBoundaryErrorFeature(this);
		    } else {
		      return new AddBoundaryTimerFeature(this);
		    }
		  }
		} else if (context.getNewObject() instanceof SubProcess) {
      return new AddEmbeddedSubProcessFeature(this);
		} else if (context.getNewObject() instanceof CallActivity) {
			return new AddCallActivityFeature(this);
		}
		return super.getAddFeature(context);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return new ICreateFeature[] { new CreateStartEventFeature(this), 
		        new CreateEndEventFeature(this),
		        new CreateErrorEndEventFeature(this),
		        new CreateUserTaskFeature(this), 
		        new CreateScriptTaskFeature(this), 
		        new CreateServiceTaskFeature(this),
		        new CreateMailTaskFeature(this), 
		        new CreateManualTaskFeature(this), 
		        new CreateReceiveTaskFeature(this),
		        new CreateBusinessRuleTaskFeature(this),
		        new CreateParallelGatewayFeature(this), 
		        new CreateExclusiveGatewayFeature(this), 
		        new CreateBoundaryTimerFeature(this),
		        new CreateBoundaryErrorFeature(this),
		        new CreateEmbeddedSubProcessFeature(this), 
		        new CreateCallActivityFeature(this) };
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		IDeleteFeature ret = new DeleteFlowElementFeature(this);
		return ret;
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
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof FlowElement) {
				return new UpdateFlowElementFeature(this);
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
    if (bo instanceof SubProcess) {
        return new SubProcessResizeFeature(this);
    }
    return super.getResizeShapeFeature(context);
  }

  @Override
  public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
    Shape shape = context.getShape();
    Object bo = getBusinessObjectForPictogramElement(shape);
    if(bo instanceof BoundaryEvent) {
      return new MoveBoundaryEventFeature(this);
    }
    return super.getMoveShapeFeature(context);
  }

  @Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		return new ICustomFeature[] { new SaveBpmnModelFeature(this), new DeleteSequenceFlowFeature(this) };
	}

}
