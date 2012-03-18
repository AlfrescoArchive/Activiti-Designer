package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateExclusiveGatewayFeature extends AbstractCreateFastBPMNFeature {

	public static final String FEATURE_ID_KEY = "exclusivegateway";

	public CreateExclusiveGatewayFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "ExclusiveGateway", "Add exclusive gateway");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
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

	@Override
	protected Class<? extends FlowElement> getFeatureClass() {
		return new ExclusiveGateway().getClass();
	}

}
