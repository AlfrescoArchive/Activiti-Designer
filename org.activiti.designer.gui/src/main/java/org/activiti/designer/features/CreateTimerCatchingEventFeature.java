package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.IntermediateCatchEvent;
import org.activiti.designer.bpmn2.model.TimerEventDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateTimerCatchingEventFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "timerintermediatecatchevent";

	public CreateTimerCatchingEventFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "TimerCatchingEvent", "Add timer intermediate catching event");
	}

	public Object[] create(ICreateContext context) {
		IntermediateCatchEvent catchEvent = new IntermediateCatchEvent();
		TimerEventDefinition eventDef = new TimerEventDefinition();
		catchEvent.getEventDefinitions().add(eventDef);
		addObjectToContainer(context, catchEvent, "TimerCatchEvent");
		
		// return newly created business object(s)
		return new Object[] { catchEvent };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_BOUNDARY_TIMER;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
