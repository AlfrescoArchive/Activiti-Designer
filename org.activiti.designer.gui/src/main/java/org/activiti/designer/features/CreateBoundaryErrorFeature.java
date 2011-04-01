package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateBoundaryErrorFeature extends AbstractCreateBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "boundaryerror";

	public CreateBoundaryErrorFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "ErrorBoundaryEvent", "Add error boundary event");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess == true) {
      return true;
    }
    return false;
	}

	public Object[] create(ICreateContext context) {
	  BoundaryEvent boundaryEvent = Bpmn2Factory.eINSTANCE.createBoundaryEvent();
		ErrorEventDefinition errorEvent = Bpmn2Factory.eINSTANCE.createErrorEventDefinition();
		boundaryEvent.getEventDefinitions().add(errorEvent);
		
		boundaryEvent.setId(getNextId());
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      getDiagram().eResource().getContents().add(boundaryEvent);
    }
    
    ((Activity) parentObject).getBoundaryEventRefs().add(boundaryEvent);
    boundaryEvent.setAttachedToRef((Activity) parentObject);

		// do the add
		addGraphicalRepresentation(context, boundaryEvent);
		
		// return newly created business object(s)
		return new Object[] { boundaryEvent };
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
		return Bpmn2Factory.eINSTANCE.createErrorEventDefinition().getClass();
	}

}
