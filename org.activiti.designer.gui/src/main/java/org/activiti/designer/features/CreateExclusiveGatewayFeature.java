package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
		exclusiveGateway.setId(getNextId());
		exclusiveGateway.setName("Exclusive Gateway");

		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(exclusiveGateway);
    } else {
    	ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).addFlowElement(exclusiveGateway);
    }

    addGraphicalContent(exclusiveGateway, context);
		
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
		return new ExclusiveGateway().getClass();
	}

}
