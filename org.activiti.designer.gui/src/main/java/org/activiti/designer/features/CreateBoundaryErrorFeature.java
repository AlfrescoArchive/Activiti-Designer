package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.CallActivity;
import org.activiti.designer.bpmn2.model.ErrorEventDefinition;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.features.AbstractCreateBPMNFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateBoundaryErrorFeature extends AbstractCreateBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "boundaryerror";

	public CreateBoundaryErrorFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "ErrorBoundaryEvent", "Add error boundary event");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess == true ||
        parentObject instanceof CallActivity == true) {
      return true;
    }
    return false;
	}

	public Object[] create(ICreateContext context) {
	  BoundaryEvent boundaryEvent = new BoundaryEvent();
		ErrorEventDefinition errorEvent = new ErrorEventDefinition();
		boundaryEvent.getEventDefinitions().add(errorEvent);
		
		boundaryEvent.setId(getNextId());
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
		if (parentObject instanceof SubProcess) {
      ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).addFlowElement(boundaryEvent);
    } else if(context.getTargetContainer().getContainer() != null && 
            context.getTargetContainer().getContainer() instanceof Diagram == false) {
      
      Object containerObject = getBusinessObjectForPictogramElement(context.getTargetContainer().getContainer());
      if (containerObject instanceof SubProcess) {
        ((SubProcess) containerObject).getFlowElements().add(boundaryEvent);
      }
      
    } else {
    	ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).addFlowElement(boundaryEvent);
    }
    
    ((Activity) parentObject).getBoundaryEvents().add(boundaryEvent);
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
		return new ErrorEventDefinition().getClass();
	}

}
