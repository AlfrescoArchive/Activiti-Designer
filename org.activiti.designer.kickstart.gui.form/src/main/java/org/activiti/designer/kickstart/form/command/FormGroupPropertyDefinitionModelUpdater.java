package org.activiti.designer.kickstart.form.command;

import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class FormGroupPropertyDefinitionModelUpdater extends KickstartModelUpdater<FormPropertyGroup> {

  public FormGroupPropertyDefinitionModelUpdater(FormPropertyGroup businessObject, PictogramElement pictogramElement,
      IFeatureProvider featureProvider) {
    super(businessObject, pictogramElement, featureProvider);
  }

  @Override
  protected FormPropertyGroup cloneBusinessObject(FormPropertyGroup businessObject) {
    // We use the same list of form-properties instead of cloning, as this 
    // updater doesn't impact children
    FormPropertyGroup newGroup = new FormPropertyGroup();
    newGroup.setFormPropertyDefinitions(businessObject.getFormPropertyDefinitions());
    newGroup.setId(businessObject.getId());
    newGroup.setTitle(businessObject.getTitle());
    newGroup.setType(businessObject.getType());
    return newGroup;
  }

  @Override
  protected void performUpdates(FormPropertyGroup valueObject, FormPropertyGroup targetObject) {
    targetObject.setId(valueObject.getId());
    targetObject.setTitle(valueObject.getTitle());
    targetObject.setType(valueObject.getType());
  }


}
