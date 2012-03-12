package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.TimerEventDefinition;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.features.AbstractCreateBPMNFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateTimerStartEventFeature extends AbstractCreateBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "timerstartevent";

	public CreateTimerStartEventFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "TimerStartEvent", "Add timer start event");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	public Object[] create(ICreateContext context) {
		StartEvent startEvent = new StartEvent();
		TimerEventDefinition timerEvent = new TimerEventDefinition();
		startEvent.getEventDefinitions().add(timerEvent);
		
		startEvent.setId(getNextId());
		startEvent.setName("Timer start");
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(startEvent);
    } else {
    	ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).addFlowElement(startEvent);
    }
    
		addGraphicalRepresentation(context, startEvent);
		
		// return newly created business object(s)
		return new Object[] { startEvent };
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
		return new StartEvent().getClass();
	}

}
