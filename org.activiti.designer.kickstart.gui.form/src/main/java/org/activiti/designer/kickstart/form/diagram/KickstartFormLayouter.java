package org.activiti.designer.kickstart.form.diagram;

import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
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
  private SingleColumnGroupFormLayout defaultGroupLayout;
  
  public KickstartFormLayouter() {
    defaultLayout = new SingleColumnFormLayout();
    defaultGroupLayout = new SingleColumnGroupFormLayout();
  }
  
  /**
   * @return the {@link ContainerShape} that can be used to add children to. An appropriate container
   * is found in the parent hierarchy, if the given {@link ContainerShape} is not suited.
   */
  protected ContainerShape getValidLayoutContainerShape(ContainerShape containerShape) {
    return this.getValidLayoutContainerShape(containerShape, ModelHandler.getKickstartFormMemoryModel(
        EcoreUtil.getURI(getDiagram(containerShape))));
  }
  
  /**
   * @return the {@link ContainerShape} that can be used to add children to. An appropriate container
   * is found in the parent hierarchy, if the given {@link ContainerShape} is not suited.
   */
  protected ContainerShape getValidLayoutContainerShape(ContainerShape containerShape, KickstartFormMemoryModel model) {
    if(containerShape instanceof Diagram) {
      return containerShape;
    } else {
      Object businessObject = model.getFeatureProvider().getBusinessObjectForPictogramElement(containerShape);
      if(businessObject instanceof FormPropertyGroup) {
        return containerShape;
      } else {
        if(containerShape.getContainer() != null) {
          // Go one level up the hierarchy to find a container that is able to do layout
          return getValidLayoutContainerShape(containerShape.getContainer(), model);
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
    ContainerShape actualTargetContainer = getValidLayoutContainerShape(targetContainer);
    
    if(actualTargetContainer != targetContainer) {
      // X and Y need to be recalculated using coordinates of container shapes
      // in the hierarchy between target and the actual target
      ContainerShape offsetContainer = targetContainer;
      while(offsetContainer != null && offsetContainer != actualTargetContainer) {
        x += offsetContainer.getGraphicsAlgorithm().getX();
        y += offsetContainer.getGraphicsAlgorithm().getX();
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
    ContainerShape actualTargetContainer = getValidLayoutContainerShape(targetContainer);
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
    // TODO: support of other layouts for FormGroups
    if(container instanceof Diagram) {
      return defaultLayout;
    } else {
      Object businessObject = ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(getDiagram(container)))
          .getFeatureProvider().getBusinessObjectForPictogramElement(container);
      if(businessObject instanceof FormPropertyGroup) {
        return defaultGroupLayout;
      }
      return null;
    }
  }
}
