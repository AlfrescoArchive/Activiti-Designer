package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.BusinessRuleTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateBusinessRuleTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "businessruletask";

  public CreateBusinessRuleTaskFeature(IFeatureProvider fp) {
    super(fp, "BusinessRuleTask", "Add business rule task");
  }

  @Override
  public Object[] create(ICreateContext context) {
    BusinessRuleTask newBusinessRuleTask = new BusinessRuleTask();
    addObjectToContainer(context, newBusinessRuleTask, "Business rule task");

    return new Object[] { newBusinessRuleTask };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_BUSINESSRULETASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
