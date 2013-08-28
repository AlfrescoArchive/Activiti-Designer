package org.activiti.designer.kickstart.form.features;

import java.util.Random;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.TextPropertyDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateTextAreaPropertyFeature extends AbstractCreateFormPropertyFeature {

  public static final String FEATURE_ID_KEY = "text-area";

  public CreateTextAreaPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp, "Text area", "Add a text area field");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected FormPropertyDefinition createFormPropertyDefinition(ICreateContext context) {
    TextPropertyDefinition definition = new TextPropertyDefinition();
    // Text-area is a multi-lined TextPropertyDefinition
    definition.setMultiline(true);
    definition.setName("Textarea " + new Random().nextInt(100));
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartFormPluginImage.NEW_ICON.getImageKey();
  }
}
