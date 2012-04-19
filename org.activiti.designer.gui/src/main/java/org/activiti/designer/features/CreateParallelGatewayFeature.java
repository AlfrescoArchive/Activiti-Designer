package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.ParallelGateway;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateParallelGatewayFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "parallelgateway";

	public CreateParallelGatewayFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "ParallelGateway", "Add parallel gateway");
	}

	public Object[] create(ICreateContext context) {
		ParallelGateway parallelGateway = new ParallelGateway();
		addObjectToContainer(context, parallelGateway, "Parallel Gateway");
		
		return new Object[] { parallelGateway };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_GATEWAY_PARALLEL;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
