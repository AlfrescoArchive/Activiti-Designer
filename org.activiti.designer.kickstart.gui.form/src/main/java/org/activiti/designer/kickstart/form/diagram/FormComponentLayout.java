package org.activiti.designer.kickstart.form.diagram;

import org.activiti.designer.kickstart.form.diagram.layout.KickstartFormLayouter;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Interface representing a class capable of layouting form-components.
 * 
 * @author Frederik Heremans
 */
public interface FormComponentLayout {

  /**
   * Shape was requested to move to the given location in the target container, and should be positioned
   * using this layout implementation.
   */
  void moveShape(KickstartFormLayouter layouter, ContainerShape targetContainer, ContainerShape sourceContainer, Shape shape, int x, int y);
  
  /**
   * Request to re-layout all components in this container.
   */
  void relayout(KickstartFormLayouter layouter, ContainerShape targetContainer);
}
