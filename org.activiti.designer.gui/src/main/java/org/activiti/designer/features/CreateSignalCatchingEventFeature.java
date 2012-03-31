package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.IntermediateCatchEvent;
import org.activiti.designer.bpmn2.model.SignalEventDefinition;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateSignalCatchingEventFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "signalintermediatecatchevent";

	public CreateSignalCatchingEventFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "SignalCatchingEvent", "Add signal intermediate catching event");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	public Object[] create(ICreateContext context) {
		IntermediateCatchEvent catchEvent = new IntermediateCatchEvent();
		SignalEventDefinition eventDef = new SignalEventDefinition();
		catchEvent.getEventDefinitions().add(eventDef);
		addObjectToContainer(context, catchEvent, "SignalCatchEvent");
		
		// return newly created business object(s)
		return new Object[] { catchEvent };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_BOUNDARY_SIGNAL;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@Override
	protected Class<? extends FlowElement> getFeatureClass() {
		return new IntermediateCatchEvent().getClass();
	}

}
