package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ListPropertyDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateListPropertyFeature extends AbstractCreateFormPropertyFeature {

  public static final String FEATURE_ID_KEY = "date";

  public CreateListPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp, "Dropdown list", "Add a dropdown with a number of possible values to select");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected FormPropertyDefinition createFormPropertyDefinition(ICreateContext context) {
    ListPropertyDefinition definition = new ListPropertyDefinition();
    definition.setName("Value select");
    definition.setWritable(true);
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartFormPluginImage.NEW_LIST_INPUT.getImageKey();
  }
}
