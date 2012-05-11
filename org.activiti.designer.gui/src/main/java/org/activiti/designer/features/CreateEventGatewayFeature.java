package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.EventGateway;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateEventGatewayFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "eventgateway";

  public CreateEventGatewayFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "EventGateway", "Add event gateway");
  }

  public Object[] create(ICreateContext context) {
    EventGateway eventGateway = new EventGateway();
    addObjectToContainer(context, eventGateway, "Event Gateway");

    return new Object[] { eventGateway };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_GATEWAY_EVENT.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
