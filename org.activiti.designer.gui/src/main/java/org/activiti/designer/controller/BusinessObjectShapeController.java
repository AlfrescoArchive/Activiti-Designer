package org.activiti.designer.controller;


import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Interface describing a controller capable of creating and updating
 * shapes that are related to a particular business-object. 
 * 
 * @author Frederik Heremans
 */
public interface BusinessObjectShapeController {

  /**
   * @return true, if this controller can create/update a shape for the
   * given business-object.
   */
  boolean canControlShapeFor(Object businessObject);
  
  /**
   * @param businessObject object to create shape for
   * @param layoutParent parent to add new shape to
   * @param width the preferred width, can be ignored by implementations.
   * @param height the preferred height, can be ignored by implementations.
   * @return new shape, representing the given business-object.
   */
  PictogramElement createShape(Object businessObject, ContainerShape layoutParent, int width, int height, IAddContext context);
}
