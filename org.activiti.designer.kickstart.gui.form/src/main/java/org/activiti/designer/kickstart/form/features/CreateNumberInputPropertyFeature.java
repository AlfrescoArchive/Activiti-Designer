package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.NumberPropertyDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateNumberInputPropertyFeature extends AbstractCreateFormPropertyFeature {

  public static final String FEATURE_ID_KEY = "number-input";

  public CreateNumberInputPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp, "Number input", "Add a number input field");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  @Override
  protected FormPropertyDefinition createFormPropertyDefinition(ICreateContext context) {
    NumberPropertyDefinition definition = new NumberPropertyDefinition();
    definition.setName("Number input");
    definition.setWritable(true);
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartFormPluginImage.NEW_NUMBER_INPUT.getImageKey();
  }
}
