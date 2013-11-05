package org.activiti.designer.controller;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Base class to use when creating {@link BusinessObjectShapeController}, exposes some
 * utility methods for locating child shapes. Also exposes {@link #LABEL_DATA_KEY} as
 * shape data, returning the first {@link MultiText} value found in the child hierarchy and
 *  {@link #DEFAULT_VALUE_DATA_KEY}, returning first {@link Text} value implementation.
 * 
 * @author Frederik Heremans
 */
public abstract class AbstractBusinessObjectShapeController implements BusinessObjectShapeController {

  public static final String LABEL_DATA_KEY = "label";
  public static final String DEFAULT_VALUE_DATA_KEY = "default";

  protected ActivitiBPMNFeatureProvider featureProvider;

  public AbstractBusinessObjectShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    this.featureProvider = featureProvider;
  }

  /**
   * {@inheritDoc}
   * 
   * <p>Supported keys: {@value #LABEL_DATA_KEY}, {@value #DEFAULT_VALUE_DATA_KEY}</p>
   */
  @Override
  public Object extractShapeData(String key, PictogramElement element) {
    Object data = null;
    
    if(LABEL_DATA_KEY.equals(key)) {
      // Extract the label-value from the Multi-text field
      MultiText labeltext = findNameMultiText(element);
      if(labeltext != null) {
        data = labeltext.getValue();
      }
    } else if(DEFAULT_VALUE_DATA_KEY.equals(key)) {
      Text defaultText = findFieldText(element);
      if(defaultText != null) {
        data = defaultText.getValue();
      }
    }
    return data;
  }
  
  protected ActivitiBPMNFeatureProvider getFeatureProvider() {
    return featureProvider;
  }
  
  /**
   * @return the first {@link MultiText} that is encountered in the child-hierarchy
   * of the given shape. Returns null, if not found.
   */
  protected MultiText findNameMultiText(PictogramElement element) {
    if (element.getGraphicsAlgorithm() instanceof MultiText) {
      return (MultiText) element.getGraphicsAlgorithm();
    }
    
    MultiText foundtext = null;
    if(element instanceof ContainerShape) {
      for(Shape child : ((ContainerShape) element).getChildren()) {
        foundtext = findNameMultiText(child);
        if(foundtext != null) {
          return foundtext;
        }
      }
    }
    return null;
  }
  
  protected Text findFieldText(PictogramElement element) {
    if (element.getGraphicsAlgorithm() instanceof Text) {
      return (Text) element.getGraphicsAlgorithm();
    }
    
    Text foundText = null;
    if (element instanceof ContainerShape) {
      for (Shape child : ((ContainerShape) element).getChildren()) {
        foundText = findFieldText(child);
        if(foundText != null) {
          return foundText;
        }
      }
    }
    return null;
  }
  
  protected String getLabelTextValue(FlowElement node) {
    String value =  node.getName() != null ?  node.getName() : "";
    return value;
  }
  
  protected String getLabelTextValue(Pool node) {
    String value =  node.getName() != null ?  node.getName() : "";
    return value;
  }
  
  protected String getLabelTextValue(Lane node) {
    String value =  node.getName() != null ?  node.getName() : "";
    return value;
  }
}
