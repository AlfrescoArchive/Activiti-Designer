package org.activiti.designer.kickstart.process.diagram;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Task;
import org.activiti.designer.kickstart.process.command.KickstartProcessModelUpdater;
import org.activiti.designer.kickstart.process.command.StepDefinitionModelUpdater;
import org.activiti.designer.kickstart.process.diagram.shape.BusinessObjectShapeController;
import org.activiti.designer.kickstart.process.diagram.shape.HumanStepShapeController;
import org.activiti.designer.kickstart.process.features.AddStepDefinitionFeature;
import org.activiti.designer.kickstart.process.features.ContainerResizeFeature;
import org.activiti.designer.kickstart.process.features.CopyFlowElementFeature;
import org.activiti.designer.kickstart.process.features.CreateHumanStepFeature;
import org.activiti.designer.kickstart.process.features.DeleteFlowElementFeature;
import org.activiti.designer.kickstart.process.features.DirectEditStepDefinitionFeature;
import org.activiti.designer.kickstart.process.features.MoveStepDefinitionFeature;
import org.activiti.designer.kickstart.process.features.PasteFlowElementFeature;
import org.activiti.designer.kickstart.process.features.TaskResizeFeature;
import org.activiti.designer.kickstart.process.features.UpdateStepDefinitionFeature;
import org.activiti.designer.kickstart.process.layout.KickstartProcessLayouter;
import org.activiti.designer.util.editor.KickstartProcessIndependenceSolver;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

public class KickstartProcessFeatureProvider extends DefaultFeatureProvider {

  protected KickstartProcessIndependenceSolver independenceResolver;
	protected KickstartProcessLayouter processLayouter;
	protected List<BusinessObjectShapeController> shapeControllers;

	public KickstartProcessFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		setIndependenceSolver(new KickstartProcessIndependenceSolver(dtp));
		independenceResolver = (KickstartProcessIndependenceSolver) getIndependenceSolver();
		this.processLayouter = new KickstartProcessLayouter();
		
		this.shapeControllers = new ArrayList<BusinessObjectShapeController>();
    shapeControllers.add(new HumanStepShapeController(this));
	}
	
	/**
   * @param businessObject object to get a {@link BusinessObjectShapeController} for
   * @return a {@link BusinessObjectShapeController} capable of creating/updating shapes
   * of for the given businessObject.
   * @throws IllegalArgumentException When no controller can be found for the given object.
   */
  public BusinessObjectShapeController getShapeController(Object businessObject) {
    for(BusinessObjectShapeController controller : shapeControllers) {
      if(controller.canControlShapeFor(businessObject)) {
        return controller;
      }
    }
    throw new IllegalArgumentException("No controller can be found for object: " + businessObject);
  }
  
  /**
   * @return true, if a {@link BusinessObjectShapeController} is available for the given business object.
   */
  public boolean hasShapeController(Object businessObject) {
    for(BusinessObjectShapeController controller : shapeControllers) {
        if(controller.canControlShapeFor(businessObject)) {
          return true;
        }
      }
    return false;
  }
  
  /**
   * @param businessObject the business object to update
   * @param pictogramElement optional pictogram-element to refresh after update is performed. When null
   * is provided, no additional update besides the actual model update is done.
   * @return the updater capable of updating the given object. Null, if the object cannot be updated.
   */
  public KickstartProcessModelUpdater<?> getModelUpdaterFor(Object businessObject, PictogramElement pictogramElement) {
    if (businessObject instanceof StepDefinition) {
      return new StepDefinitionModelUpdater((StepDefinition) businessObject, pictogramElement, this);
    }
    return null;
  }

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		return new AddStepDefinitionFeature(this);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return new ICreateFeature[] {
		        new CreateHumanStepFeature(this)};
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
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		return new UpdateStepDefinitionFeature(this);
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		return new DirectEditStepDefinitionFeature(this);
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
    return new MoveStepDefinitionFeature(this);
  }

  public KickstartProcessIndependenceSolver getPojoIndependenceSolver() {
    return independenceResolver;
  }
  
  public KickstartProcessLayouter getProcessLayouter() {
    return processLayouter;
  }
}
