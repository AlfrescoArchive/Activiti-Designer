package org.activiti.designer.kickstart.process.features;

import java.util.UUID;

import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.workflow.simple.definition.ChoiceStepsDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateChoiceStepFeature extends AbstractCreateStepDefinitionFeature {

  public static final String FEATURE_ID_KEY = "choice-step";

  public CreateChoiceStepFeature(KickstartProcessFeatureProvider fp) {
    super(fp, "Choice step", "Add a choice step");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected StepDefinition createStepDefinition(ICreateContext context) {
    ChoiceStepsDefinition definition = new ChoiceStepsDefinition();
    definition.setId(UUID.randomUUID().toString());
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartProcessPluginImage.CHOICE_STEP_ICON.getImageKey();
  }
}