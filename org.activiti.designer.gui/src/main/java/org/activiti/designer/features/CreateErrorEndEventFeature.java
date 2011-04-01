package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateErrorEndEventFeature extends AbstractCreateBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "endevent";

	public CreateErrorEndEventFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "ErrorEndEvent", "Add error end event");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (parentObject instanceof SubProcess);
	}

	public Object[] create(ICreateContext context) {
		EndEvent endEvent = Bpmn2Factory.eINSTANCE.createEndEvent();
		ErrorEventDefinition eventDef = Bpmn2Factory.eINSTANCE.createErrorEventDefinition();
		endEvent.getEventDefinitions().add(eventDef);
		
		endEvent.setId(getNextId());
		endEvent.setName("ErrorEnd");
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(endEvent);
    }

		// do the add
		addGraphicalRepresentation(context, endEvent);
		
		// return newly created business object(s)
		return new Object[] { endEvent };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_ENDEVENT_ERROR;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createEndEvent().getClass();
	}

}
