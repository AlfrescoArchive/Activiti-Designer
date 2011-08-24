package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateTimerCatchingEventFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "timerintermediatecatchevent";

	public CreateTimerCatchingEventFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "TimerCatchingEvent", "Add timer intermediate catching event");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	public Object[] create(ICreateContext context) {
		IntermediateCatchEvent catchEvent = Bpmn2Factory.eINSTANCE.createIntermediateCatchEvent();
		TimerEventDefinition eventDef = Bpmn2Factory.eINSTANCE.createTimerEventDefinition();
		catchEvent.getEventDefinitions().add(eventDef);
		
		catchEvent.setId(getNextId());
		catchEvent.setName("TimerCatchEvent");
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
		if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(catchEvent);
    } else {
      getDiagram().eResource().getContents().add(catchEvent);
    }

    addGraphicalContent(catchEvent, context);
		
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

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createIntermediateCatchEvent().getClass();
	}

}
