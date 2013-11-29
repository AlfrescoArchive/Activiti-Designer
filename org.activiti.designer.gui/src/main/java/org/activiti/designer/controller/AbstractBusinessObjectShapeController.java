package org.activiti.designer.controller;

import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Text;

/**
 * Base class to use when creating {@link BusinessObjectShapeController}, exposes some
 * utility methods for locating child shapes. Also exposes {@link #LABEL_DATA_KEY} as
 * shape data, returning the first {@link MultiText} value found in the child hierarchy and
 *  {@link #DEFAULT_VALUE_DATA_KEY}, returning first {@link Text} value implementation.
 * 
 * @author Frederik Heremans
 */
public abstract class AbstractBusinessObjectShapeController implements BusinessObjectShapeController {

  protected ActivitiBPMNFeatureProvider featureProvider;

  public AbstractBusinessObjectShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    this.featureProvider = featureProvider;
  }

  protected ActivitiBPMNFeatureProvider getFeatureProvider() {
    return featureProvider;
  }
}
