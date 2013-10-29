package org.activiti.designer.kickstart.process.features;

import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.workflow.simple.definition.AbstractNamedStepDefinition;
import org.activiti.workflow.simple.definition.HumanStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateHumanStepFeature extends AbstractCreateStepDefinitionFeature {

  public static final String FEATURE_ID_KEY = "human-step";

  public CreateHumanStepFeature(KickstartProcessFeatureProvider fp) {
    super(fp, "Human step", "Add a human step");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected StepDefinition createStepDefinition(ICreateContext context) {
    AbstractNamedStepDefinition definition = new HumanStepDefinition();
    definition.setName("Human step");
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartProcessPluginImage.HUMAN_STEP_ICON.getImageKey();
  }
}