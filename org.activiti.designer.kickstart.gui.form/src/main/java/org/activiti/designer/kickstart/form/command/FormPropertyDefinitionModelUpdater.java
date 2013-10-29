package org.activiti.designer.kickstart.form.command;

import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class FormPropertyDefinitionModelUpdater extends KickstartModelUpdater<FormPropertyDefinition> {

  public FormPropertyDefinitionModelUpdater(FormPropertyDefinition businessObject, PictogramElement pictogramElement,
      IFeatureProvider featureProvider) {
    super(businessObject, pictogramElement, featureProvider);
  }

  @Override
  protected FormPropertyDefinition cloneBusinessObject(FormPropertyDefinition businessObject) {
    return businessObject.clone();
  }

  @Override
  protected void performUpdates(FormPropertyDefinition valueObject, FormPropertyDefinition targetObject) {
    targetObject.setValues(valueObject);
  }


}
