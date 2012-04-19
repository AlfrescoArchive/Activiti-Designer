package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.SignalEventDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateBoundarySignalFeature extends AbstractCreateBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "boundarysignal";

	public CreateBoundarySignalFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "SignalBoundaryEvent", "Add signal boundary event");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof Activity == true) {
      
      return true;
    }
    return false;
	}

	public Object[] create(ICreateContext context) {
	  BoundaryEvent boundaryEvent = new BoundaryEvent();
		SignalEventDefinition signalEvent = new SignalEventDefinition();
		boundaryEvent.getEventDefinitions().add(signalEvent);
		
		boundaryEvent.setId(getNextId(boundaryEvent));
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    ((Activity) parentObject).getBoundaryEvents().add(boundaryEvent);
    boundaryEvent.setAttachedToRef((Activity) parentObject);
    
		// do the add
		addGraphicalRepresentation(context, boundaryEvent);
		
		// return newly created business object(s)
		return new Object[] { boundaryEvent };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_BOUNDARY_SIGNAL;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
