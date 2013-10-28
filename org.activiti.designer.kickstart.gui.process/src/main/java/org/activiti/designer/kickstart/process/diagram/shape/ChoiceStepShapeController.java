package org.activiti.designer.kickstart.process.diagram.shape;

import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.designer.kickstart.process.util.StepDefinitionStyles;
import org.activiti.workflow.simple.definition.ChoiceStepsDefinition;
import org.activiti.workflow.simple.definition.ListConditionStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for
 * {@link ChoiceStepsDefinition} objects.
 *  
 * @author Frederik Heremans
 */
public class ChoiceStepShapeController extends AbstractBusinessObjectShapeController implements WrappingChildShapeController {
  
  public ChoiceStepShapeController(KickstartProcessFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    return businessObject instanceof ChoiceStepsDefinition;
  }

  @Override
  public ContainerShape createShape(Object businessObject, ContainerShape layoutParent, int width, int height) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(layoutParent, true);
    final IGaService gaService = Graphiti.getGaService();
    
    Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
    
    ChoiceStepsDefinition definition = (ChoiceStepsDefinition) businessObject;

    // If no size has been supplied, revert to the default sizes
    if(width < 0) {
      width = StepDefinitionStyles.DEFAULT_COMPONENT_WIDTH;
    }
    
    height = StepDefinitionStyles.DEFAULT_LABEL_HEIGHT + StepDefinitionStyles.DEFAULT_PARALLEL_BOX_HEIGHT;

    RoundedRectangle rectangle; 
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, 0, 0, width, height);

    // Create and set visible rectangle inside invisible rectangle
    rectangle = gaService.createRoundedRectangle(invisibleRectangle, 2, 2);
    rectangle.setStyle(StepDefinitionStyles.getStepDefinitionStyle(diagram));
    rectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    gaService.setLocationAndSize(rectangle, 0, 0, width, height);
    
    getFeatureProvider().link(containerShape, new Object[] {definition});
    
    // Check if the shape has any children. If so, create shapes for them as well
    if(definition.getStepList() != null && !definition.getStepList().isEmpty()) {
      AddContext addContext = null;
      AreaContext areaContext = new AreaContext();
      areaContext.setX(-1);
      areaContext.setY(-1);
      
      StepDefinition child = null;
      for(int i = definition.getStepList().size() -1; i>=0; i--) {
        child = definition.getStepList().get(i);
        addContext = new AddContext(areaContext, child);
        addContext.setTargetContainer(containerShape);
        featureProvider.getAddFeature(addContext).add(addContext);
      }
    }
    return containerShape;
  }


  @Override
  public void updateShape(ContainerShape shape, Object businessObject, int width, int height) {
    boolean updateWidth = (width > 0 && width != shape.getGraphicsAlgorithm().getWidth());
    boolean updateHeight = (height > 0 && height != shape.getGraphicsAlgorithm().getHeight());
    // Check if width needs to be altered
    if(updateHeight || updateWidth) {
      IGaService gaService = Graphiti.getGaService();
      
      // Resize main shape rectangle and box shape
      Rectangle invisibleRectangle = (Rectangle) shape.getGraphicsAlgorithm();
      GraphicsAlgorithm box = (GraphicsAlgorithm) invisibleRectangle.eContents().get(0);
      
      if(updateWidth) {
        gaService.setWidth(invisibleRectangle, width);
        gaService.setWidth(box, width);
      }
      
      if(updateHeight) {
        gaService.setHeight(invisibleRectangle, height);
        gaService.setHeight(box, height);
      }
      
    }
  }
  
  @Override
  public GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(ContainerShape container) {
    // No direct edit supported
    return null;
  }
  
  @Override
  public boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject) {
    // Model changes never trigger a shape-update, only layout changes
    return false;
  }

  @Override
  public boolean shouldWrapChild(StepDefinition definition) {
    return ! (definition instanceof ListConditionStepDefinition<?>);
  }

  @Override
  public StepDefinition wrapChild(StepDefinition definition) {
    ListConditionStepDefinition<ChoiceStepsDefinition> wrapper = new ListConditionStepDefinition<ChoiceStepsDefinition>();
    wrapper.addStep(definition);
    return wrapper;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean shouldDeleteWrapper(StepDefinition businessObjectForSource) {
    if(businessObjectForSource instanceof ListConditionStepDefinition<?>) {
      ListConditionStepDefinition<ChoiceStepsDefinition> wrapper = (ListConditionStepDefinition<ChoiceStepsDefinition>) businessObjectForSource;
      return wrapper.getSteps() == null || wrapper.getSteps().isEmpty();
    }
    return false;
  }
}
