package com.alfresco.designer.gui.features;

import org.activiti.designer.features.AbstractCreateFastBPMNFeature;
import org.eclipse.bpmn2.AlfrescoUserTask;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateAlfrescoUserTaskFeature extends AbstractCreateFastBPMNFeature {

	public static final String FEATURE_ID_KEY = "alfrescoUsertask";

	public CreateAlfrescoUserTaskFeature(IFeatureProvider fp) {
		super(fp, "AlfrescoUserTask", "Add Alfresco user task");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	@Override
	public Object[] create(ICreateContext context) {
		AlfrescoUserTask newUserTask = Bpmn2Factory.eINSTANCE.createAlfrescoUserTask();

		newUserTask.setId(getNextId());
		newUserTask.setName("Alfresco User Task");

		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(newUserTask);
    } else {
      getDiagram().eResource().getContents().add(newUserTask);
    }

    addGraphicalContent(newUserTask, context);

		// activate direct editing after object creation
		getFeatureProvider().getDirectEditingInfo().setActive(true);

		return new Object[] { newUserTask };
	}

	@Override
	public String getCreateImageId() {
		return "org.activiti.designer.usertask";
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createAlfrescoUserTask().getClass();
	}

}
