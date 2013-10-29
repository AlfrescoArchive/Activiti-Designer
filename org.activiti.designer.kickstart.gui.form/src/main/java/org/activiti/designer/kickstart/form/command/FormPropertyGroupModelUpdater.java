package org.activiti.designer.kickstart.form.command;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class FormPropertyGroupModelUpdater extends KickstartModelUpdater<FormPropertyGroup> {

  public FormPropertyGroupModelUpdater(FormPropertyGroup businessObject, PictogramElement pictogramElement,
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
    
    boolean typeChanged = !StringUtils.equals(valueObject.getType(), targetObject.getType());
    targetObject.setType(valueObject.getType());
    
    if(typeChanged) {
      // Force relayout of the updated group
      PictogramElement element = ((KickstartFormFeatureProvider)featureProvider).getPictogramElementForBusinessObject(targetObject);
      if(element != null) {
        ((KickstartFormFeatureProvider)featureProvider).getFormLayouter().relayout((ContainerShape) element);
      }
    }
  }


}
