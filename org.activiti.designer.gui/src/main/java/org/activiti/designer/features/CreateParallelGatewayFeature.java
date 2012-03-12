package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.ParallelGateway;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateParallelGatewayFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "parallelgateway";

	public CreateParallelGatewayFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "ParallelGateway", "Add parallel gateway");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	public Object[] create(ICreateContext context) {
		ParallelGateway parallelGateway = new ParallelGateway();
		parallelGateway.setId(getNextId());
		parallelGateway.setName("Parallel Gateway");
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(parallelGateway);
    } else {
    	ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).addFlowElement(parallelGateway);
    }
		
    addGraphicalContent(parallelGateway, context);
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

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return new ParallelGateway().getClass();
	}
	
}
