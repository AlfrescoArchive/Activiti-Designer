package org.activiti.designer.kickstart.process.diagram;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.TextAnnotation;
import org.activiti.designer.kickstart.process.features.AddSequenceFlowFeature;
import org.activiti.designer.kickstart.process.features.AddServiceTaskFeature;
import org.activiti.designer.kickstart.process.features.ChangeElementTypeFeature;
import org.activiti.designer.kickstart.process.features.ContainerResizeFeature;
import org.activiti.designer.kickstart.process.features.CopyFlowElementFeature;
import org.activiti.designer.kickstart.process.features.CreateSequenceFlowFeature;
import org.activiti.designer.kickstart.process.features.CreateServiceTaskFeature;
import org.activiti.designer.kickstart.process.features.DeleteFlowElementFeature;
import org.activiti.designer.kickstart.process.features.DirectEditFlowElementFeature;
import org.activiti.designer.kickstart.process.features.DirectEditTextAnnotationFeature;
import org.activiti.designer.kickstart.process.features.LayoutTextAnnotationFeature;
import org.activiti.designer.kickstart.process.features.MoveActivityFeature;
import org.activiti.designer.kickstart.process.features.PasteFlowElementFeature;
import org.activiti.designer.kickstart.process.features.ReconnectSequenceFlowFeature;
import org.activiti.designer.kickstart.process.features.TaskResizeFeature;
import org.activiti.designer.kickstart.process.features.UpdateFlowElementFeature;
import org.activiti.designer.util.editor.POJOIndependenceSolver;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
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
import org.eclipse.graphiti.features.context.ILayoutContext;
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

public class KickstartProcessFeatureProvider extends DefaultFeatureProvider {

	public POJOIndependenceSolver independenceResolver;

	public KickstartProcessFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		setIndependenceSolver(new POJOIndependenceSolver());
		independenceResolver = (POJOIndependenceSolver) getIndependenceSolver();
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		if (context.getNewObject() instanceof SequenceFlow) {
		  return new AddSequenceFlowFeature(this);
		  
		} else if (context.getNewObject() instanceof ServiceTask) {
		  ServiceTask serviceTask = (ServiceTask) context.getNewObject();
		  return new AddServiceTaskFeature(this);
    } 
		return super.getAddFeature(context);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return new ICreateFeature[] {
		        new CreateServiceTaskFeature(this)};
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
	  PictogramElement pictogramElement = context.getPictogramElement();
    Object bo = getBusinessObjectForPictogramElement(pictogramElement);

    if(bo instanceof FlowElement) {
      return new DeleteFlowElementFeature(this);
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
		return new ICreateConnectionFeature[] { new CreateSequenceFlowFeature(this)};
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
	  return new ReconnectSequenceFlowFeature(this);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);

		if (pictogramElement instanceof ContainerShape) {
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
		} else if (bo instanceof TextAnnotation) {
			return new DirectEditTextAnnotationFeature(this);
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
    if (bo instanceof Activity) {
    	// in case an activity is moved, make sure, attached boundary events will move too
    	return new MoveActivityFeature(this);

    }
    return super.getMoveShapeFeature(context);
  }


  @Override
  public ILayoutFeature getLayoutFeature(ILayoutContext context) {
	  final PictogramElement pe = context.getPictogramElement();
	  final Object bo = getBusinessObjectForPictogramElement(pe);

	  if (bo instanceof TextAnnotation) {
		  return new LayoutTextAnnotationFeature(this);
	  }

	  return super.getLayoutFeature(context);
  }

  @Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		return new ICustomFeature[] { new ChangeElementTypeFeature(this) };
	}

  public POJOIndependenceSolver getPojoIndependenceSolver() {
    return independenceResolver;
  }
}
