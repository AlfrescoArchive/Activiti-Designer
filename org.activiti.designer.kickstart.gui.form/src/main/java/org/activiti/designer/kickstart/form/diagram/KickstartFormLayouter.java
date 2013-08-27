package org.activiti.designer.kickstart.form.diagram;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Provides {@link FormComponentLayout} implementations for {@link ContainerShape}s.
 * @author Frederik Heremans
 */
public class KickstartFormLayouter {

  private SingleColumnFormLayout defaultLayout;
  
  public KickstartFormLayouter() {
    defaultLayout = new SingleColumnFormLayout();
  }
  
  /**
   * @return the appropriate layout to use for the given container. Null, if no layouter
   * is available.
   */
  public FormComponentLayout getLayoutForContainer(ContainerShape container) {
    if(container instanceof Diagram) {
      return defaultLayout;
    }
    return null;
  }
  
  /**
   * @return the {@link ContainerShape} that can be used to add children to. An appropriate container
   * is found in the parent hierarchy, if the given {@link ContainerShape} is not suited.
   */
  public ContainerShape getLayoutContainerShape(ContainerShape containerShape) {
    if(containerShape instanceof Diagram) {
      return containerShape;
    } else if(containerShape.getContainer() != null) {
      // Go one level up the hierarchy to find a container that is able to do layout
      return getLayoutContainerShape(containerShape.getContainer());
    }
    return null;
  }
  
}
