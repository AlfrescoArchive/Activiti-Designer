package org.activiti.designer.kickstart.process.features;

import java.util.UUID;

import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.workflow.simple.definition.ParallelStepsDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateParallelStepFeature extends AbstractCreateStepDefinitionFeature {

  public static final String FEATURE_ID_KEY = "parallel-step";

  public CreateParallelStepFeature(KickstartProcessFeatureProvider fp) {
    super(fp, "Parallel step", "Add a parallel step");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected StepDefinition createStepDefinition(ICreateContext context) {
    ParallelStepsDefinition definition = new ParallelStepsDefinition();
    definition.setId(UUID.randomUUID().toString());
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartProcessPluginImage.PARALLEL_STEP_ICON.getImageKey();
  }
}