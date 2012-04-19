package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.MailTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateMailTaskFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "mailtask";

	public CreateMailTaskFeature(IFeatureProvider fp) {
		super(fp, "MailTask", "Add mail task");
	}

	@Override
	public Object[] create(ICreateContext context) {
		MailTask newMailTask = new MailTask();
		addObjectToContainer(context, newMailTask, "Mail Task");
		
		return new Object[] { newMailTask };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_MAILTASK;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
