package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.EventSubProcess;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateStartEventFeature extends AbstractCreateBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "startevent";

	public CreateStartEventFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "StartEvent", "Add start event");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
	  if(parentObject instanceof EventSubProcess) return false;
	  return super.canCreate(context);
	}

	public Object[] create(ICreateContext context) {
		StartEvent startEvent = new StartEvent();
		addObjectToContainer(context, startEvent, "Start");
		
		// return newly created business object(s)
		return new Object[] { startEvent };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_STARTEVENT_NONE;
	}
	
	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
