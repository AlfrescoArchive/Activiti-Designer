package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.definition.form.BooleanPropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateBooleanPropertyFeature extends AbstractCreateFormPropertyFeature {

  public static final String FEATURE_ID_KEY = "boolean";

  public CreateBooleanPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp, "Checkbox", "Add a checkbox");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected FormPropertyDefinition createFormPropertyDefinition(ICreateContext context) {
    BooleanPropertyDefinition definition = new BooleanPropertyDefinition();
    definition.setName("Checkbox");
    definition.setWritable(true);
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartFormPluginImage.NEW_CHECKBOX.getImageKey();
  }
}
