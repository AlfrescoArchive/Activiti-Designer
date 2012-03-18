package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.InclusiveGateway;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateInclusiveGatewayFeature extends AbstractCreateFastBPMNFeature {

	public static final String FEATURE_ID_KEY = "inclusivegateway";

	public CreateInclusiveGatewayFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "InclusiveGateway", "Add inclusive gateway");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	public Object[] create(ICreateContext context) {
		InclusiveGateway inclusiveGateway = new InclusiveGateway();
		addObjectToContainer(context, inclusiveGateway, "Inclusive Gateway");
		
		return new Object[] { inclusiveGateway };
	}

	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_GATEWAY_INCLUSIVE;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@Override
	protected Class<? extends FlowElement> getFeatureClass() {
		return new InclusiveGateway().getClass();
	}

}
