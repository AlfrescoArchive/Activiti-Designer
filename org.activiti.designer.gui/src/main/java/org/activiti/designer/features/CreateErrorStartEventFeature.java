package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.ErrorEventDefinition;
import org.activiti.designer.bpmn2.model.EventSubProcess;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateErrorStartEventFeature extends AbstractCreateBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "errorstartevent";

	public CreateErrorStartEventFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "ErrorStartEvent", "Add error start event");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (parentObject instanceof EventSubProcess);
	}

	public Object[] create(ICreateContext context) {
		StartEvent startEvent = new StartEvent();
		ErrorEventDefinition errorEvent = new ErrorEventDefinition();
		startEvent.getEventDefinitions().add(errorEvent);
		addObjectToContainer(context, startEvent, "Error start");
		
		// return newly created business object(s)
		return new Object[] { startEvent };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_BOUNDARY_ERROR;
	}
	
	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
