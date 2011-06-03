package com.alfresco.designer.gui.features;

import org.activiti.designer.util.features.AbstractCreateBPMNFeature;
import org.eclipse.bpmn2.AlfrescoScriptTask;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateAlfrescoScriptTaskFeature extends AbstractCreateBPMNFeature {

	public static final String FEATURE_ID_KEY = "alfrescoScripttask";

	public CreateAlfrescoScriptTaskFeature(IFeatureProvider fp) {
		super(fp, "AlfrescoScriptTask", "Add Alfresco script task");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	@Override
	public Object[] create(ICreateContext context) {
		AlfrescoScriptTask newScriptTask = Bpmn2Factory.eINSTANCE.createAlfrescoScriptTask();

		newScriptTask.setId(getNextId());
		newScriptTask.setName("Alfresco Script Task");

		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(newScriptTask);
    } else {
      getDiagram().eResource().getContents().add(newScriptTask);
    }

		addGraphicalRepresentation(context, newScriptTask);

		// activate direct editing after object creation
		getFeatureProvider().getDirectEditingInfo().setActive(true);

		return new Object[] { newScriptTask };
	}

	@Override
	public String getCreateImageId() {
		return "org.activiti.designer.scripttask";
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createAlfrescoScriptTask().getClass();
	}

}
