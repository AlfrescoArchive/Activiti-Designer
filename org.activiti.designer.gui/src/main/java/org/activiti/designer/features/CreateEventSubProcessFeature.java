package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.EventSubProcess;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateEventSubProcessFeature extends AbstractCreateBPMNFeature {

	public static final String FEATURE_ID_KEY = "eventsubprocess";

	public CreateEventSubProcessFeature(IFeatureProvider fp) {
		super(fp, "EventSubProcess", "Add event sub process");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		if(context.getTargetContainer() instanceof Diagram) return false;
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess == true) {
      return true;
    }
    
    return false;
	}

	@Override
	public Object[] create(ICreateContext context) {
		EventSubProcess newSubProcess = new EventSubProcess();
		addObjectToContainer(context, newSubProcess, "Event sub Process");

		// return newly created business object(s)
		return new Object[] { newSubProcess };
	}

	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_EVENT_SUBPROCESS;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
