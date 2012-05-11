package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.InclusiveGateway;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateInclusiveGatewayFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "inclusivegateway";

  public CreateInclusiveGatewayFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "InclusiveGateway", "Add inclusive gateway");
  }

  public Object[] create(ICreateContext context) {
    InclusiveGateway inclusiveGateway = new InclusiveGateway();
    addObjectToContainer(context, inclusiveGateway, "Inclusive Gateway");

    return new Object[] { inclusiveGateway };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_GATEWAY_INCLUSIVE.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
