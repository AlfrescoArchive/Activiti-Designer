package com.alfresco.designer.gui.features;

import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoMailTask;
import org.activiti.designer.features.AbstractCreateFastBPMNFeature;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateAlfrescoMailTaskFeature extends AbstractCreateFastBPMNFeature {

	public static final String FEATURE_ID_KEY = "alfrescoMailtask";

	public CreateAlfrescoMailTaskFeature(IFeatureProvider fp) {
		super(fp, "AlfrescoMailTask", "Add Alfresco mail task");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	@Override
	public Object[] create(ICreateContext context) {
		AlfrescoMailTask newMailTask = new AlfrescoMailTask();

		newMailTask.setId(getNextId(newMailTask));
		newMailTask.setName("Alfresco Mail Task");

		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(newMailTask);
    } else {
    	ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getMainProcess().getFlowElements().add(newMailTask);
    }

    addGraphicalContent(context, newMailTask);

		// activate direct editing after object creation
		getFeatureProvider().getDirectEditingInfo().setActive(true);

		return new Object[] { newMailTask };
	}

	@Override
	public String getCreateImageId() {
		return "org.activiti.designer.mail";
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
