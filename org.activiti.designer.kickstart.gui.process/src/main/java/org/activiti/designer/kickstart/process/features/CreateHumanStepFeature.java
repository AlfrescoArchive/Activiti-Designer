package org.activiti.designer.kickstart.process.features;

import org.activiti.designer.kickstart.process.PluginImage;
import org.activiti.workflow.simple.definition.HumanStepDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateHumanStepFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "humanstep";

  public CreateHumanStepFeature(IFeatureProvider fp) {
    super(fp, "Human step", "Add human step");
  }

  public CreateHumanStepFeature(IFeatureProvider fp, String name, String description) {
    super(fp, name, description);
  }

  @Override
  public Object[] create(ICreateContext context) {
    HumanStepDefinition newHumanStep = new HumanStepDefinition();
    newHumanStep.setName("Human step");
    
    addObjectToContainer(context, newHumanStep, newHumanStep.getName());

    return new Object[] { newHumanStep };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_SERVICETASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}