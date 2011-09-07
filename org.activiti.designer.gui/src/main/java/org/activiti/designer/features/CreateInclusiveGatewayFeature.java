package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.SubProcess;
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
		InclusiveGateway inclusiveGateway = Bpmn2Factory.eINSTANCE.createInclusiveGateway();
		inclusiveGateway.setId(getNextId());
		inclusiveGateway.setName("Inclusive Gateway");
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(inclusiveGateway);
    } else {
      getDiagram().eResource().getContents().add(inclusiveGateway);
    }

    addGraphicalContent(inclusiveGateway, context);
		
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

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createInclusiveGateway().getClass();
	}

}
