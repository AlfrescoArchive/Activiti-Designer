package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.CallActivity;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateCallActivityFeature extends AbstractCreateFastBPMNFeature {

	public static final String FEATURE_ID_KEY = "callactivity";

	public CreateCallActivityFeature(IFeatureProvider fp) {
		super(fp, "CallActivity", "Add call activity");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	@Override
	public Object[] create(ICreateContext context) {
		CallActivity callActivity = new CallActivity();
		addObjectToContainer(context, callActivity, "Call activity");

		// return newly created business object(s)
		return new Object[] { callActivity };

	}

	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_CALLACTIVITY;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@Override
	protected Class<? extends FlowElement> getFeatureClass() {
		return new CallActivity().getClass();
	}

}
