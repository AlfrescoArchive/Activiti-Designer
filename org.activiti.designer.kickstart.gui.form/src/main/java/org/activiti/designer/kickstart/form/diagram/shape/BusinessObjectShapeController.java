package org.activiti.designer.kickstart.form.diagram.shape;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;

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
   * @return true, if the values in the given businessObject differ from the corresponding
   * values found in the given shape.
   */
  boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject);
  
  /**
   * @param businessObject object to create shape for
   * @param layoutParent parent to add new shape to
   * @param width the preferred width, can be ignored by implementations.
   * @param height the preferred height, can be ignored by implementations.
   * @return new shape, representing the given business-object.
   */
  ContainerShape createShape(Object businessObject, ContainerShape layoutParent, int width, int height);
  
  /**
   * @param businessObject object to update shape for
   * @param shape shape representing the business object
   * @param width the preferred width, can be ignored by implementations.
   * @param height the preferred height, can be ignored by implementations.
   */
  void updateShape(ContainerShape shape, Object businessObject, int width, int height);
  
  /**
   * Extract data from the given shape, hiding away any complexity related to child-shape iteration
   * and type-checking. Provided a clean way of exposing data that is present in a shape-tree, which is
   * specific to the implementation.
   * 
   * @param key data key, defined by the implementation.
   * @param shape shape to get the data from
   * @return the shape data value that can be extracted from the given shape for the given key. Returns
   * null if the data cannot be extracted or if the data is null.
   */
  Object extractShapeData(String key, ContainerShape shape);
}
