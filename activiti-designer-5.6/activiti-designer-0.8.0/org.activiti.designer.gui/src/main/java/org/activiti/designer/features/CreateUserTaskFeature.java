package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateUserTaskFeature extends AbstractCreateBPMNFeature {

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
		UserTask newUserTask = Bpmn2Factory.eINSTANCE.createUserTask();

		newUserTask.setId(getNextId());
		newUserTask.setName("User Task");

		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(newUserTask);
    } else {
      getDiagram().eResource().getContents().add(newUserTask);
    }

		addGraphicalRepresentation(context, newUserTask);

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
		return Bpmn2Factory.eINSTANCE.createUserTask().getClass();
	}

}
