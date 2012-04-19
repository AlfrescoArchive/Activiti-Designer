package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateExclusiveGatewayFeature extends AbstractCreateFastBPMNFeature {

	public static final String FEATURE_ID_KEY = "exclusivegateway";

	public CreateExclusiveGatewayFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "ExclusiveGateway", "Add exclusive gateway");
	}

	public Object[] create(ICreateContext context) {
		ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
		addObjectToContainer(context, exclusiveGateway, "Exclusive Gateway");
		
		return new Object[] { exclusiveGateway };
	}

	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_GATEWAY_EXCLUSIVE;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
