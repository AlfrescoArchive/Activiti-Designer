package org.activiti.designer.kickstart.form.diagram.layout;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.kickstart.form.diagram.FormComponentLayout;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.util.FormComponentStyles;
import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

/**
 * @author Frederik Heremans
 */
public class GroupFormLayout implements FormComponentLayout {
  private int initialverticalSpacing = 40;
  private int verticalSpacing = 5;
  private int leftPadding = 5;
  private int horizontalSpacing = 5;
  
  private int numberOfColumns = 1;
  private int columnWidth;

  public GroupFormLayout(int numberOfColumns) {
    this.numberOfColumns = numberOfColumns;
    
    // Calculate width based on the component styles
    this.columnWidth = (FormComponentStyles.DEFAULT_COMPONENT_WIDTH - (numberOfColumns - 1) * horizontalSpacing) / numberOfColumns;
  }
  
  @Override
  public void moveShape(KickstartFormLayouter layouter, ContainerShape targetContainer, ContainerShape sourceContainer,
      Shape shape, int x, int y) {
    
    boolean inSameContainer = targetContainer.equals(sourceContainer);
    int targetShapeIndex = 1;
    
    if(x < 0 && y < 0) {
      // If both x and y are negative, add the shape at the end
      targetShapeIndex = targetContainer.getChildren().size() - 1;
    } else {
      // Loop over all children to find the appropriate position to insert the shape, skipping the first one
      Shape child = null;
      List<Integer> rows = new ArrayList<Integer>();
      int rowBottom = 0;
      
      for (int shapeIndex = 1; shapeIndex < targetContainer.getChildren().size(); shapeIndex++) {
        child = targetContainer.getChildren().get(shapeIndex);
        rowBottom = Math.max(rowBottom, child.getGraphicsAlgorithm().getY() + child.getGraphicsAlgorithm().getHeight()
            + verticalSpacing);
        
        // Push row info on list
        if (shapeIndex% numberOfColumns == 0) {
          rows.add(rowBottom);
          rowBottom = 0;
        }
      }

      // Potentially add incomplete row
      if(rowBottom > 0) {
        rows.add(rowBottom);
      }
      
      // Check what column the shape is ideally moved to
      int column = Math.min(numberOfColumns, 
          (x + shape.getGraphicsAlgorithm().getWidth() / 2) / (columnWidth + horizontalSpacing));
      
      int row = -1;
      for (int i = 0; i < rows.size(); i++) {
        if (y < rows.get(i)) {
          row = i;
          break;
        }
      }
      
      if (row == -1) {
        // Add as last
        row = rows.size() - 1;
      }
      
      targetShapeIndex = row * numberOfColumns + column + 1;
      targetShapeIndex = Math.min(targetShapeIndex, targetContainer.getChildren().size() - 1);
      targetShapeIndex = Math.max(1, targetShapeIndex);
    }
    

    if (inSameContainer) {
        targetContainer.getChildren().move(targetShapeIndex, shape);
    } else {
      sourceContainer.getChildren().remove(shape);
      targetContainer.getChildren().add(targetShapeIndex, shape);
    }

    // Finally, re-position all shapes according to their order in the container
    relayout(layouter, targetContainer);

    // Request the other container to be re-layouted, since an element has been moved
    if (!inSameContainer) {
      layouter.relayout(sourceContainer);
    }
  }

  @Override
  public void relayout(KickstartFormLayouter layouter, ContainerShape targetContainer) {
    Diagram diagram = layouter.getDiagram(targetContainer);
    KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(diagram)));
    KickstartFormFeatureProvider featureProvider = (KickstartFormFeatureProvider) model.getFeatureProvider();
    TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(targetContainer);

    boolean updateGraphicsAllowed = editingDomain != null
        && editingDomain instanceof InternalTransactionalEditingDomain
        && ((InternalTransactionalEditingDomain) editingDomain).getActiveTransaction() != null;

    List<FormPropertyDefinition> definitionsInNewOrder = new ArrayList<FormPropertyDefinition>();
    FormPropertyGroup group = (FormPropertyGroup) featureProvider.getBusinessObjectForPictogramElement(targetContainer);

    int yPosition = initialverticalSpacing;
    
    // In case the group doesn't have a title, children are layout to the top of the group
    if(group.getTitle() == null || group.getTitle().isEmpty()) {
      yPosition = verticalSpacing;
    }
    int yOffset = 0;
    int xPosition = leftPadding;

    int currentColumn = 1;
    FormPropertyDefinition definition = null;
    Shape child = null;
    for (int i = 1; i < targetContainer.getChildren().size(); i++) {
      child = targetContainer.getChildren().get(i);
      definition = (FormPropertyDefinition) featureProvider.getBusinessObjectForPictogramElement(child);
      if (definition != null) {
        definitionsInNewOrder.add(definition);

        if (updateGraphicsAllowed) {
          Graphiti.getGaService().setLocation(child.getGraphicsAlgorithm(), xPosition, yPosition);

          // Also, request an update of the shape itself, adapting it to the available width
          featureProvider.getShapeController(definition).updateShape((ContainerShape) child, definition,
              columnWidth, -1);
        }
      }
      yOffset = Math.max(yOffset, child.getGraphicsAlgorithm().getHeight() + verticalSpacing);
      currentColumn++;
      if (currentColumn > numberOfColumns) {
        currentColumn = 1;
        yPosition = yPosition + yOffset + verticalSpacing;
        yOffset = 0;
      }
      xPosition = leftPadding + (currentColumn - 1)
          * (columnWidth + horizontalSpacing);
    }

    if (updateGraphicsAllowed) {
      if (yOffset > 0) {
        yPosition += yOffset;
      }
      // Update this container shape's height
      Graphiti.getGaService().setSize(targetContainer.getGraphicsAlgorithm(),
          targetContainer.getGraphicsAlgorithm().getWidth(),
          Math.max(yPosition, FormComponentStyles.DEFAULT_GROUP_HEIGHT));
    }

    if (model.isInitialized()) {
      group.setFormPropertyDefinitions(definitionsInNewOrder);
    }

    // Force relayout of diagram, since the height may be altered
    layouter.relayout(layouter.getDiagram(targetContainer));

  }

  public void setVerticalSpacing(int verticalSpacing) {
    this.initialverticalSpacing = verticalSpacing;
  }

  public int getVerticalSpacing() {
    return initialverticalSpacing;
  }
}
