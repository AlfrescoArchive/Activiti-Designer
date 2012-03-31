package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.SignalEventDefinition;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.ThrowSignalEvent;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateSignalThrowingEventFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "signalintermediatethrowevent";

	public CreateSignalThrowingEventFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "SignalThrowingEvent", "Add signal intermediate throwing event");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	public Object[] create(ICreateContext context) {
		ThrowSignalEvent throwEvent = new ThrowSignalEvent();
		SignalEventDefinition eventDef = new SignalEventDefinition();
		throwEvent.getEventDefinitions().add(eventDef);
		addObjectToContainer(context, throwEvent, "SignalThrowEvent");
		
		// return newly created business object(s)
		return new Object[] { throwEvent };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_THROW_SIGNAL;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@Override
	protected Class<? extends FlowElement> getFeatureClass() {
		return new ThrowSignalEvent().getClass();
	}

}
