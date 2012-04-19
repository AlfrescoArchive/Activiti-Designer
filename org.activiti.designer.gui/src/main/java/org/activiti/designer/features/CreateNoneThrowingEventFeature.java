package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.ThrowEvent;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateNoneThrowingEventFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "noneintermediatethrowevent";

	public CreateNoneThrowingEventFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "NoneThrowingEvent", "Add none intermediate throwing event");
	}

	public Object[] create(ICreateContext context) {
		ThrowEvent throwEvent = new ThrowEvent();
		addObjectToContainer(context, throwEvent, "NoneThrowEvent");
		
		// return newly created business object(s)
		return new Object[] { throwEvent };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_THROW_NONE;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
