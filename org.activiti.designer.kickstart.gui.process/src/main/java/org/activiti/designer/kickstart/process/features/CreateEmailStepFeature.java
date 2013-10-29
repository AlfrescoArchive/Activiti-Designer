package org.activiti.designer.kickstart.process.features;

import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.workflow.simple.alfresco.step.AlfrescoEmailStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateEmailStepFeature extends AbstractCreateStepDefinitionFeature {

  public static final String FEATURE_ID_KEY = "email-step";

  public CreateEmailStepFeature(KickstartProcessFeatureProvider fp) {
    super(fp, "Email step", "Add an email step");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected StepDefinition createStepDefinition(ICreateContext context) {
    AlfrescoEmailStepDefinition definition = new AlfrescoEmailStepDefinition();
    definition.setName("Email step");
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartProcessPluginImage.EMAIL_STEP_ICON.getImageKey();
  }
}