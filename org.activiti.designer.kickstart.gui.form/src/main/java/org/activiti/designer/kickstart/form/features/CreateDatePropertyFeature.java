package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateDatePropertyFeature extends AbstractCreateFormPropertyFeature {

  public static final String FEATURE_ID_KEY = "date";

  public CreateDatePropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp, "Date", "Add a date selection field");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected FormPropertyDefinition createFormPropertyDefinition(ICreateContext context) {
    DatePropertyDefinition definition = new DatePropertyDefinition();
    definition.setShowTime(true);
    definition.setName("Date field");
    definition.setWritable(true);
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartFormPluginImage.NEW_DATE_INPUT.getImageKey();
  }
}
