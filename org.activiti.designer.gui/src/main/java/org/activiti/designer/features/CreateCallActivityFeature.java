package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.CallActivity;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
		callActivity.setId(getNextId());
		setName("Call activity", callActivity, context);

		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(callActivity);
    } else {
    	ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).addFlowElement(callActivity);
    }

		// do the add
    addGraphicalContent(callActivity, context);

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

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return new CallActivity().getClass();
	}

}
