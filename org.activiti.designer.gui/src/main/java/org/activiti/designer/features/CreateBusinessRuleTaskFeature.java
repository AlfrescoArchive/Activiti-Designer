package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.BusinessRuleTask;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateBusinessRuleTaskFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "businessruletask";

	public CreateBusinessRuleTaskFeature(IFeatureProvider fp) {
		super(fp, "BusinessRuleTask", "Add business rule task");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	@Override
	public Object[] create(ICreateContext context) {
		BusinessRuleTask newBusinessRuleTask = new BusinessRuleTask();
		addObjectToContainer(context, newBusinessRuleTask, "Business rule task");
    
		return new Object[] { newBusinessRuleTask };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_BUSINESSRULETASK;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return new BusinessRuleTask().getClass();
	}

}
