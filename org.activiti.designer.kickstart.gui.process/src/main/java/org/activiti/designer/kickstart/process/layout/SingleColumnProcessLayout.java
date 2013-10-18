package org.activiti.designer.kickstart.process.layout;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.kickstart.process.diagram.ProcessComponentLayout;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
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
public class SingleColumnProcessLayout implements ProcessComponentLayout {

  private int leftPadding = 20;
  private int verticalSpacing = 10;
  
  public void relayout(KickstartProcessLayouter layouter, ContainerShape targetContainer) {
    int yPosition = verticalSpacing;
    int xPosition = leftPadding;
    
    TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(targetContainer);
    
    boolean updateGraphicsAllowed = editingDomain != null && editingDomain instanceof InternalTransactionalEditingDomain 
        && ((InternalTransactionalEditingDomain) editingDomain).getActiveTransaction() != null;
    
    Diagram diagram = layouter.getDiagram(targetContainer);
    KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(diagram)));
    
    List<StepDefinition> definitionsInNewOrder = new ArrayList<StepDefinition>();
    
    for (Shape child : targetContainer.getChildren()) {
      Object businessObject = model.getFeatureProvider().getBusinessObjectForPictogramElement(child);
      if (businessObject instanceof StepDefinition) {
        definitionsInNewOrder.add((StepDefinition) businessObject);
        xPosition = leftPadding; 
      }
      if(updateGraphicsAllowed) {
        Graphiti.getGaService().setLocation(child.getGraphicsAlgorithm(), xPosition, yPosition);
      }
      yPosition = yPosition + child.getGraphicsAlgorithm().getHeight() + verticalSpacing;
    }
    
    if (model.isInitialized()) {
      // Set the properties list in the new order after re-layouting
      model.getWorkflowDefinition().getSteps().clear();
      model.getWorkflowDefinition().getSteps().addAll(definitionsInNewOrder);
    }
  }
  

  /**
   * Moves the given shape to the right location in the given container, based on the position the shape should be moved
   * to. Other shapes in the container may be moved as well.
   */
  public void moveShape(KickstartProcessLayouter layouter, ContainerShape targetContainer, ContainerShape sourceContainer, Shape shape, int x, int y) {
    boolean inSameContainer = targetContainer.equals(sourceContainer);
    
    int xPosition = leftPadding;
    
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
      int targetShapeIndex = Math.max(shapeIndex - 1, 0);
      shapeIndex++;
      
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
      
      // Finally, re-position all shapes according to their order in the container
      relayout(layouter, targetContainer);
    }
      
    // Request the other container to be re-layouted, since an element has
    // been moved
    if(!inSameContainer) {
      layouter.relayout(sourceContainer);
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
    this.leftPadding = leftPadding;
  }
}
