package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.UserTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateUserTaskFeature extends AbstractCreateFastBPMNFeature {

	public static final String FEATURE_ID_KEY = "usertask";

	public CreateUserTaskFeature(IFeatureProvider fp) {
		super(fp, "UserTask", "Add user task");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	@Override
	public Object[] create(ICreateContext context) {
		UserTask newUserTask = new UserTask();
		addObjectToContainer(context, newUserTask, "User Task");

		// activate direct editing after object creation
		getFeatureProvider().getDirectEditingInfo().setActive(true);

		return new Object[] { newUserTask };
	}

	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_USERTASK;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return new UserTask().getClass();
	}

}
