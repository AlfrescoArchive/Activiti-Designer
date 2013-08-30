package org.activiti.designer.kickstart.form.diagram;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

public class SingleColumnGroupFormLayout implements FormComponentLayout {

  protected static final int MIN_BOX_HEIGHT = 60;
  
  private int initialverticalSpacing = 40;
  private int verticalSpacing = 10;
  private int leftPadding = 5;

  @Override
  public void moveShape(KickstartFormLayouter layouter, ContainerShape targetContainer, ContainerShape sourceContainer,
      Shape shape, int x, int y) {
    boolean inSameContainer = targetContainer.equals(sourceContainer);

    // Skip the first child-shape, this is the title
    if (targetContainer.getChildren().size() == 1) {
      // First element in the container
      Graphiti.getGaService().setLocation(shape.getGraphicsAlgorithm(), leftPadding, initialverticalSpacing);
      targetContainer.getChildren().add(shape);
    } else if (inSameContainer && targetContainer.getChildren().size() == 2) {
      // Already added to container, re-set the initial location
      Graphiti.getGaService().setLocation(shape.getGraphicsAlgorithm(), leftPadding, initialverticalSpacing);
    } else {

      // Only move when the shape is not already present as the only one in the container
      int targetShapeIndex = -1;
      Shape child = null;
      
      // Loop over all children to find the appropriate position to insert the shape, skipping the first one
      for (int shapeIndex = 1; shapeIndex < targetContainer.getChildren().size(); shapeIndex++) {
        child = targetContainer.getChildren().get(shapeIndex);
        if (child.getGraphicsAlgorithm().getY() > y) {
          targetShapeIndex = shapeIndex;
          break;
        }
      }

      // No index specified, add at end of the container
      if (targetShapeIndex < 0) {
        targetShapeIndex = targetContainer.getChildren().size() - 1;
      }

      if (inSameContainer) {
        targetContainer.getChildren().move(targetShapeIndex, shape);
      } else {
        sourceContainer.getChildren().remove(shape);
        targetContainer.getChildren().add(targetShapeIndex, shape);
      }
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
    
    List<FormPropertyDefinition> definitionsInNewOrder = new ArrayList<FormPropertyDefinition>();
    FormPropertyGroup group = (FormPropertyGroup) model.getFeatureProvider().getBusinessObjectForPictogramElement(targetContainer);
    
    int yPosition = initialverticalSpacing;
    int xPosition = leftPadding;
    
    FormPropertyDefinition definition = null;
    Shape child = null;
    for (int i=1; i<targetContainer.getChildren().size(); i++) {
      child =  targetContainer.getChildren().get(i);
       definition = (FormPropertyDefinition) model.getFeatureProvider().getBusinessObjectForPictogramElement(child);
       if(definition != null) {
         definitionsInNewOrder.add(definition);
       }
      Graphiti.getGaService().setLocation(child.getGraphicsAlgorithm(), xPosition, yPosition);
      yPosition = yPosition + child.getGraphicsAlgorithm().getHeight() + verticalSpacing;
    }
    
    // Update this container shape's height
    Graphiti.getGaService().setSize(targetContainer.getGraphicsAlgorithm(), 
        targetContainer.getGraphicsAlgorithm().getWidth(), Math.max(yPosition, MIN_BOX_HEIGHT));
    
    if(model.isInitialized()) {
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
