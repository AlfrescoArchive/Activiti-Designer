package org.activiti.designer.kickstart.form.diagram.layout;

import org.activiti.designer.kickstart.form.diagram.FormComponentLayout;
import org.activiti.designer.kickstart.form.property.FormPropertyGroupPropertySection;
import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Main entry point for layouting components in containers.  
 * 
 * @author Frederik Heremans
 */
public class KickstartFormLayouter {

  private SingleColumnFormLayout defaultLayout;
  private GroupFormLayout oneColumnLayout;
  private GroupFormLayout twoColumnLayout;
  private GroupFormLayout threeColumnLayout;
  
  public KickstartFormLayouter() {
    defaultLayout = new SingleColumnFormLayout();
    oneColumnLayout = new GroupFormLayout(1);
    twoColumnLayout = new GroupFormLayout(2);
    threeColumnLayout = new GroupFormLayout(3);
  }
  
  /**
   * @param shapeToLayout 
   * @return the {@link ContainerShape} that can be used to add children to. An appropriate container
   * is found in the parent hierarchy, if the given {@link ContainerShape} is not suited.
   */
  protected ContainerShape getValidLayoutContainerShape(ContainerShape containerShape, Shape shapeToLayout) {
    return this.getValidLayoutContainerShape(containerShape, ModelHandler.getKickstartFormMemoryModel(
        EcoreUtil.getURI(getDiagram(containerShape))), shapeToLayout);
  }
  
  /**
   * @return the {@link ContainerShape} that can be used to add children to. An appropriate container
   * is found in the parent hierarchy, if the given {@link ContainerShape} is not suited.
   */
  protected ContainerShape getValidLayoutContainerShape(ContainerShape containerShape, KickstartFormMemoryModel model, Shape shapeToLayout) {
    if (containerShape instanceof Diagram) {
      if(shapeToLayout == null) {
        // When the shape to layout doens't matter, the diagram is a valid layout container
        return containerShape;
      }
      Object businessObject = model.getFeatureProvider().getBusinessObjectForPictogramElement(shapeToLayout);
      // On the diagram, the only valid objects are group. All form-components are layed out on the "info" group
      if (businessObject instanceof FormPropertyGroup) {
        return containerShape;
      } else if (businessObject instanceof FormPropertyDefinition) {
        for (Shape child : containerShape.getChildren()) {
          Object boForShape = model.getFeatureProvider().getBusinessObjectForPictogramElement(child);
          if (boForShape instanceof FormPropertyGroup
              && KickstartFormMemoryModel.INFO_GROUP_ID.equals(((FormPropertyGroup) boForShape).getId())) {
            return (ContainerShape) child;
          }
        }
        return null;
      }
      } else {
        Object containerObject = model.getFeatureProvider().getBusinessObjectForPictogramElement(containerShape);
        // Not the diagram, check if shape represents a group
        if (containerObject instanceof FormPropertyGroup) {
          return containerShape;
        } else {
          if (containerShape.getContainer() != null) {
            // Go one level up the hierarchy to find a container that is able to do layout
            return getValidLayoutContainerShape(containerShape.getContainer(), model, shapeToLayout);
          }
        }
      }
    return null;
  }

  /**
   * Move a shape to the target container, positioning it according to the layout applicable for that container.
   * @param targetContainer container to move shape to. In case the container is not a valid target, the first
   * valid container in the parent hierarchy is selected.
   * @param sourceContainer container where the shape was in before
   * @param shape the shape to move
   * @param x x-coordinate relative to the provided targetContainer the shape is suggested to move to
   * @param y y-coordinate relative to the provided targetContainer the shape is suggested to move to
   * @return the container the shape was moved to, can differ from the provided targetContainer.
   */
  public ContainerShape moveShape(ContainerShape targetContainer, ContainerShape sourceContainer, Shape shape, int x, int y) {
    ContainerShape actualTargetContainer = getValidLayoutContainerShape(targetContainer, shape);
    
    if(actualTargetContainer != targetContainer) {
      // X and Y need to be recalculated using coordinates of container shapes
      // in the hierarchy between target and the actual target
      ContainerShape offsetContainer = targetContainer;
      while(offsetContainer != null && offsetContainer != actualTargetContainer) {
        x += offsetContainer.getGraphicsAlgorithm().getX();
        y += offsetContainer.getGraphicsAlgorithm().getY();
        offsetContainer = offsetContainer.getContainer();
      }
    }
    
    getLayoutForContainer(actualTargetContainer).moveShape(this, actualTargetContainer, sourceContainer, shape, x, y);
    return actualTargetContainer;
  }

  /**
   * Re-layout the target container. If the given container is not a container that can be layout,
   * the first valid container in the parent hierarchy is re-layout.
   * @param targetContainer container to re-layout
   */
  public void relayout(ContainerShape targetContainer) {
    ContainerShape actualTargetContainer = getValidLayoutContainerShape(targetContainer, null);
    getLayoutForContainer(actualTargetContainer).relayout(this, actualTargetContainer);
  }
  
  /**
   * @return the diagram the given container is used in.
   */
  public Diagram getDiagram(ContainerShape container) {
    if(container instanceof Diagram) {
      return (Diagram) container;
    }
    if(container.getContainer() != null) {
      return getDiagram(container.getContainer());
    }
    return null;
  }
  
  protected FormComponentLayout getLayoutForContainer(ContainerShape container) {
    FormComponentLayout layout = null;
    
    if(container instanceof Diagram) {
      layout = defaultLayout;
    } else {
      Object businessObject = ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(getDiagram(container)))
          .getFeatureProvider().getBusinessObjectForPictogramElement(container);
      if(businessObject instanceof FormPropertyGroup) {
        FormPropertyGroup group = (FormPropertyGroup) businessObject;
        
        if(FormPropertyGroupPropertySection.TWO_COLUMN_VALUE.equals(group.getType())) {
          layout = twoColumnLayout;
        } else if(FormPropertyGroupPropertySection.THREE_COLUMN_VALUE.equals(group.getType())) {
          layout = threeColumnLayout;
        } else {
          layout = oneColumnLayout;
        }
      }
    }
    return layout;
  }
}
