package org.activiti.designer.kickstart.process.layout;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.designer.kickstart.process.diagram.ProcessComponentLayout;
import org.activiti.designer.kickstart.process.util.StepDefinitionStyles;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.AbstractStepDefinitionContainer;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

/**
 * Class capable of layouting elements in one column.
 * 
 * @author Tijs Rademakers
 */
public class StepDefinitionVerticalLayout implements ProcessComponentLayout {

  private int horizontalPadding = 20;
  private int verticalSpacing = 10;
  private int topPadding = 10;
  private boolean skipFirstShape = false;
  
  public void relayout(KickstartProcessLayouter layouter, ContainerShape targetContainer) {
    int yPosition = topPadding + verticalSpacing;
    int xPosition = horizontalPadding;
    int width = targetContainer.getGraphicsAlgorithm().getWidth() - horizontalPadding * 2;
    int height = yPosition;
    
    TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(targetContainer);
    
    boolean updateGraphicsAllowed = editingDomain != null && editingDomain instanceof InternalTransactionalEditingDomain 
        && ((InternalTransactionalEditingDomain) editingDomain).getActiveTransaction() != null;
    
    Diagram diagram = layouter.getDiagram(targetContainer);
    KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(diagram)));
    KickstartProcessFeatureProvider featureProvider = (KickstartProcessFeatureProvider) model.getFeatureProvider();
    
    List<StepDefinition> definitionsInNewOrder = new ArrayList<StepDefinition>();
    
    for (Shape child : targetContainer.getChildren()) {
      Object businessObject = model.getFeatureProvider().getBusinessObjectForPictogramElement(child);
      if (businessObject instanceof StepDefinition) {
        StepDefinition definition = (StepDefinition) businessObject;
        definitionsInNewOrder.add(definition);
        xPosition = horizontalPadding;
        
        if(updateGraphicsAllowed) {
          Graphiti.getGaService().setLocation(child.getGraphicsAlgorithm(), xPosition, yPosition);
          
          // Also, request an update of the shape itself, adapting it to the available width
          featureProvider.getShapeController(definition).updateShape((ContainerShape) child, definition,
              width, -1);
          
          featureProvider.getProcessLayouter().relayoutIfNeeded((ContainerShape) child, featureProvider);
          height += child.getGraphicsAlgorithm().getHeight() + verticalSpacing;
          yPosition = yPosition + child.getGraphicsAlgorithm().getHeight() + verticalSpacing;
        }
        
      }
    }
    
    if(updateGraphicsAllowed) {
      height = Math.max(height, StepDefinitionStyles.DEFAULT_COMPONENT_BOX_HEIGHT);
      if(targetContainer.getGraphicsAlgorithm().getHeight() != height) {
        // Also, request an update of the shape itself, adapting it to the available height
        Object bo = featureProvider.getBusinessObjectForPictogramElement(targetContainer);
        featureProvider.getShapeController(bo).updateShape(targetContainer, bo,
            -1, height);
      }
    }
    
    if (model.isInitialized()) {
      Object businessObject = featureProvider.getBusinessObjectForPictogramElement(targetContainer);
      AbstractStepDefinitionContainer<?> container = null; 
      if(businessObject != null && businessObject instanceof AbstractStepDefinitionContainer<?>) {
        // Set the properties list in the new order after re-layouting
        container = (AbstractStepDefinitionContainer<?>) businessObject;
        container.getSteps().clear();
        container.getSteps().addAll(definitionsInNewOrder);
      }
    }
  }
  

  /**
   * Moves the given shape to the right location in the given container, based on the position the shape should be moved
   * to. Other shapes in the container may be moved as well.
   */
  public void moveShape(KickstartProcessLayouter layouter, ContainerShape targetContainer, ContainerShape sourceContainer, Shape shape, int x, int y) {
    boolean inSameContainer = targetContainer.equals(sourceContainer);
    
    int xPosition = horizontalPadding;
    
    if(targetContainer.getChildren().size() == getEmptyContainerCount()) {
      // First element in the container
      Graphiti.getGaService().setLocation(shape.getGraphicsAlgorithm(), xPosition, topPadding);
      targetContainer.getChildren().add(shape);
    } else if(inSameContainer && targetContainer.getChildren().size() == getEmptyContainerCount() + 1) {
      // Already added to container, re-set the initial location
      Graphiti.getGaService().setLocation(shape.getGraphicsAlgorithm(), xPosition, topPadding);
    } else {
      // Only move when the shape is not already present as the only one in the container
      int shapeIndex = 0;
      int targetShapeIndex = -1;

      // Loop over all children to find the appropriate position to insert the shape
      for (Shape child : targetContainer.getChildren()) {
        if (child.getGraphicsAlgorithm().getY() > y) {
          targetShapeIndex = shapeIndex;
          break;
        }
        shapeIndex++;
      }
      
      // No index specified, add at end of the container
      if(targetShapeIndex < 0) {
        targetShapeIndex = targetContainer.getChildren().size() - 1;
      } else if(targetShapeIndex == 0) {
        targetShapeIndex = getFirstIndex();
      }
      
      if(inSameContainer) {
        targetContainer.getChildren().move(targetShapeIndex, shape);
      } else {
        sourceContainer.getChildren().remove(shape);
        targetContainer.getChildren().add(targetShapeIndex, shape);
      }
    }
  }

  /**
   * @param verticalSpacing
   *          vertical spacing between components.
   */
  public void setVerticalSpacing(int verticalSpacing) {
    this.verticalSpacing = verticalSpacing;
  }

  /**
   * @param leftPadding
   *          padding on the left side of all components. All components are aligned to the left.
   */
  public void setLeftPadding(int leftPadding) {
    this.horizontalPadding = leftPadding;
  }
  
  public void setTopPadding(int topPadding) {
    this.topPadding = topPadding;
  }
  
  public void setSkipFirstShape(boolean skipFirstShape) {
    this.skipFirstShape = skipFirstShape;
  }
  
  protected int getEmptyContainerCount() {
    return skipFirstShape ? 1 : 0;
  }
  
  protected int getFirstIndex() {
    return skipFirstShape ? 1 : 0;
  }
}
