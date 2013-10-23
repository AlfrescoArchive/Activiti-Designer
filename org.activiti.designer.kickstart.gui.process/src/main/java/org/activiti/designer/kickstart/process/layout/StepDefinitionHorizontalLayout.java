package org.activiti.designer.kickstart.process.layout;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.designer.kickstart.process.diagram.ProcessComponentLayout;
import org.activiti.designer.kickstart.process.util.StepDefinitionStyles;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.AbstractStepDefinitionContainer;
import org.activiti.workflow.simple.definition.ParallelStepsDefinition;
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
 * Class capable of layouting elements in a {@link AbstractStepDefinitionContainer} in one row.
 * 
 * @author Frederik Heremans
 */
public class StepDefinitionHorizontalLayout implements ProcessComponentLayout {

  private int horizontalSpacing = 15;
  private int leftPadding = horizontalSpacing;
  private int verticalSpacing = 10;
  
  public void relayout(KickstartProcessLayouter layouter, ContainerShape targetContainer) {
    int yPosition = verticalSpacing;
    int xPosition = leftPadding;
    int maxheight = StepDefinitionStyles.DEFAULT_PARALLEL_BOX_HEIGHT;
    
    TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(targetContainer);
    
    boolean updateGraphicsAllowed = editingDomain != null && editingDomain instanceof InternalTransactionalEditingDomain 
        && ((InternalTransactionalEditingDomain) editingDomain).getActiveTransaction() != null;
    
    Diagram diagram = layouter.getDiagram(targetContainer);
    KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(diagram)));
    KickstartProcessFeatureProvider featureProvider = (KickstartProcessFeatureProvider) model.getFeatureProvider();
    
    List<StepDefinition> definitionsInNewOrder = new ArrayList<StepDefinition>();
    int count = targetContainer.getChildren().size();
    int columnWidth = 0;
    
    if(count > 0) {
      columnWidth = (targetContainer.getGraphicsAlgorithm().getWidth() - leftPadding) / count - horizontalSpacing;
    } else {
      columnWidth = (targetContainer.getGraphicsAlgorithm().getWidth() - horizontalSpacing * 2 - leftPadding);
    }
    
    List<Shape> children = new ArrayList<Shape>(targetContainer.getChildren());
    
    for (Shape child : children) {
      Object businessObject = model.getFeatureProvider().getBusinessObjectForPictogramElement(child);
      if (businessObject instanceof StepDefinition) {
        StepDefinition definition = (StepDefinition) businessObject;
        definitionsInNewOrder.add(definition);
      
        if(updateGraphicsAllowed) {
          Graphiti.getGaService().setLocation(child.getGraphicsAlgorithm(), xPosition, yPosition);
          
          // Also, request an update of the shape itself, adapting it to the available width
          featureProvider.getShapeController(definition).updateShape((ContainerShape) child, definition,
              columnWidth, -1);
          
          featureProvider.getProcessLayouter().relayoutIfNeeded((ContainerShape) child, featureProvider);
        }
        
        if(child.getGraphicsAlgorithm() != null && child.getGraphicsAlgorithm().getHeight() + verticalSpacing * 2 > maxheight) {
          maxheight = child.getGraphicsAlgorithm().getHeight() + verticalSpacing * 2;
        }
      }
      xPosition = xPosition + horizontalSpacing + columnWidth;
    }
    
    if(updateGraphicsAllowed) {
      
      if(targetContainer.getGraphicsAlgorithm().getHeight() != maxheight) {
        // Also, request an update of the shape itself, adapting it to the available height
        ParallelStepsDefinition definition = (ParallelStepsDefinition) featureProvider.getBusinessObjectForPictogramElement(targetContainer);
        featureProvider.getShapeController(definition).updateShape(targetContainer, definition,
            -1, maxheight);
      }
    }
    
    if (model.isInitialized()) {
      // Set the steps list in the new order after re-layouting
      AbstractStepDefinitionContainer<?> container = (AbstractStepDefinitionContainer<?>) model.getFeatureProvider().getBusinessObjectForPictogramElement(targetContainer);
      container.getSteps().clear();
      container.getSteps().addAll(definitionsInNewOrder);
    }
  }
  

  /**
   * Moves the given shape to the right location in the given container, based on the position the shape should be moved
   * to. Other shapes in the container may be moved as well.
   */
  public void moveShape(KickstartProcessLayouter layouter, ContainerShape targetContainer, ContainerShape sourceContainer, Shape shape, int x, int y) {
    boolean inSameContainer = targetContainer.equals(sourceContainer);
    
    int xPosition = horizontalSpacing;
    
    if(targetContainer.getChildren().size() == 0) {
      // First element in the container
      Graphiti.getGaService().setLocation(shape.getGraphicsAlgorithm(), xPosition, verticalSpacing);
      targetContainer.getChildren().add(shape);
    } else if(inSameContainer && targetContainer.getChildren().size() == 1) {
      // Already added to container, re-set the initial location
      Graphiti.getGaService().setLocation(shape.getGraphicsAlgorithm(), xPosition, verticalSpacing);
    } else {
      // Only move when the shape is not already present as the only one in the container
      int shapeIndex = 0;
      int targetShapeIndex = -1;

      // Loop over all children to find the appropriate position to insert the shape
      for (Shape child : targetContainer.getChildren()) {
        if (child.getGraphicsAlgorithm().getX() > x) {
          targetShapeIndex = shapeIndex;
          break;
        }
        shapeIndex++;
      }
      
      // No index specified, add at end of the container
      if(targetShapeIndex < 0) {
        targetShapeIndex = targetContainer.getChildren().size() - 1;
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
  
  public void setHorizontalSpacing(int horizontalSpacing) {
    this.horizontalSpacing = horizontalSpacing;
  }

  /**
   * @param leftPadding
   *          padding on the left side of all components. All components are aligned to the left.
   */
  public void setLeftPadding(int leftPadding) {
    this.leftPadding = leftPadding;
  }
}
