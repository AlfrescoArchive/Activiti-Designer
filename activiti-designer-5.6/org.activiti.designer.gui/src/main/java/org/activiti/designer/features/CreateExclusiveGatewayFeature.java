package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.util.features.AbstractCreateBPMNFeature;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateExclusiveGatewayFeature extends AbstractCreateBPMNFeature {

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
		ExclusiveGateway exclusiveGateway = Bpmn2Factory.eINSTANCE.createExclusiveGateway();
		exclusiveGateway.setId(getNextId());
		exclusiveGateway.setName("Exclusive Gateway");

		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(exclusiveGateway);
    } else {
      getDiagram().eResource().getContents().add(exclusiveGateway);
    }

		addGraphicalRepresentation(context, exclusiveGateway);
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

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createExclusiveGateway().getClass();
	}

}
