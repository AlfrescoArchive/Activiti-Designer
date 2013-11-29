package org.activiti.designer.command;

import org.activiti.bpmn.model.TextAnnotation;
import org.eclipse.graphiti.features.IFeatureProvider;

public class TextAnnotationModelUpdater extends BpmnProcessModelUpdater {

  public TextAnnotationModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof TextAnnotation) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected TextAnnotation cloneBusinessObject(Object businessObject) {
    return ((TextAnnotation) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((TextAnnotation) targetObject).setValues(((TextAnnotation) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new TextAnnotationModelUpdater(featureProvider);
  }
}
