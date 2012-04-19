package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.ReceiveTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateReceiveTaskFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "receivetask";

	public CreateReceiveTaskFeature(IFeatureProvider fp) {
		super(fp, "ReceiveTask", "Add receive task");
	}

	@Override
	public Object[] create(ICreateContext context) {
		ReceiveTask newReceiveTask = new ReceiveTask();
		addObjectToContainer(context, newReceiveTask, "Receive Task");
		
		return new Object[] { newReceiveTask };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_RECEIVETASK;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
