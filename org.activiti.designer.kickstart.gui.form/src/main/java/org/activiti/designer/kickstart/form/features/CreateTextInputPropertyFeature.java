package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.TextPropertyDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateTextInputPropertyFeature extends AbstractCreateFormPropertyFeature {

  public static final String FEATURE_ID_KEY = "text-input";

  public CreateTextInputPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp, "Text input", "Add a text input field");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  @Override
  protected FormPropertyDefinition createFormPropertyDefinition(ICreateContext context) {
    TextPropertyDefinition definition = new TextPropertyDefinition();
    definition.setName("Text input");
    definition.setWritable(true);
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartFormPluginImage.NEW_TEXT_INPUT.getImageKey();
  }
}
