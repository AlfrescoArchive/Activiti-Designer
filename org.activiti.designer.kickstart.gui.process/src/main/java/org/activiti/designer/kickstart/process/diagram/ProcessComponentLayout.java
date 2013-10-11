package org.activiti.designer.kickstart.process.diagram;

import org.activiti.designer.kickstart.process.layout.KickstartProcessLayouter;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Interface representing a class capable of layouting process components.
 * 
 * @author Frederik Heremans
 * @author Tijs Rademakers
 */
public interface ProcessComponentLayout {

  /**
   * Shape was requested to move to the given location in the target container, and should be positioned
   * using this layout implementation.
   */
  void moveShape(KickstartProcessLayouter layouter, ContainerShape targetContainer, ContainerShape sourceContainer, Shape shape, int x, int y);
  
  /**
   * Request to re-layout all components in this container.
   */
  void relayout(KickstartProcessLayouter layouter, ContainerShape targetContainer);
}
