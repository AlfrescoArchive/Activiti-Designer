package org.activiti.designer.kickstart.process.diagram;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.kickstart.process.command.KickstartProcessModelUpdater;
import org.activiti.designer.kickstart.process.command.StepDefinitionModelUpdater;
import org.activiti.designer.kickstart.process.command.WorkflowDefinitionModelUpdater;
import org.activiti.designer.kickstart.process.diagram.shape.BusinessObjectShapeController;
import org.activiti.designer.kickstart.process.diagram.shape.HumanStepShapeController;
import org.activiti.designer.kickstart.process.features.AddStepDefinitionFeature;
import org.activiti.designer.kickstart.process.features.CreateHumanStepFeature;
import org.activiti.designer.kickstart.process.features.DirectEditStepDefinitionFeature;
import org.activiti.designer.kickstart.process.features.MoveStepDefinitionFeature;
import org.activiti.designer.kickstart.process.features.UpdateStepDefinitionFeature;
import org.activiti.designer.kickstart.process.layout.KickstartProcessLayouter;
import org.activiti.designer.util.editor.KickstartProcessIndependenceSolver;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
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
   * @param businessObject
   *          object to get a {@link BusinessObjectShapeController} for
   * @return a {@link BusinessObjectShapeController} capable of creating/updating shapes of for the given
   *         businessObject.
   * @throws IllegalArgumentException
   *           When no controller can be found for the given object.
   */
  public BusinessObjectShapeController getShapeController(Object businessObject) {
    for (BusinessObjectShapeController controller : shapeControllers) {
      if (controller.canControlShapeFor(businessObject)) {
        return controller;
      }
    }
    throw new IllegalArgumentException("No controller can be found for object: " + businessObject);
  }

  /**
   * @return true, if a {@link BusinessObjectShapeController} is available for the given business object.
   */
  public boolean hasShapeController(Object businessObject) {
    for (BusinessObjectShapeController controller : shapeControllers) {
      if (controller.canControlShapeFor(businessObject)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param businessObject
   *          the business object to update
   * @param pictogramElement
   *          optional pictogram-element to refresh after update is performed. When null is provided, no additional
   *          update besides the actual model update is done.
   * @return the updater capable of updating the given object. Null, if the object cannot be updated.
   */
  public KickstartProcessModelUpdater<?> getModelUpdaterFor(Object businessObject, PictogramElement pictogramElement) {
    if (businessObject instanceof StepDefinition) {
      return new StepDefinitionModelUpdater((StepDefinition) businessObject, pictogramElement, this);
    } if(businessObject instanceof WorkflowDefinition) {
      return new WorkflowDefinitionModelUpdater((WorkflowDefinition) businessObject, this);
    }
    return null;
  }

  @Override
  public IAddFeature getAddFeature(IAddContext context) {
    return new AddStepDefinitionFeature(this);
  }

  @Override
  public ICreateFeature[] getCreateFeatures() {
    return new ICreateFeature[] { new CreateHumanStepFeature(this) };
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
  public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
    return new MoveStepDefinitionFeature(this);
  }
  
  @Override
  public Object getBusinessObjectForPictogramElement(PictogramElement pictogramElement) {
    if(pictogramElement instanceof Diagram) {
      KickstartProcessMemoryModel model = ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(pictogramElement));
      if(model != null && model.isInitialized()) {
        return model.getWorkflowDefinition();
      }
    }
    return super.getBusinessObjectForPictogramElement(pictogramElement);
  }

  public KickstartProcessIndependenceSolver getPojoIndependenceSolver() {
    return independenceResolver;
  }

  public KickstartProcessLayouter getProcessLayouter() {
    return processLayouter;
  }
}
