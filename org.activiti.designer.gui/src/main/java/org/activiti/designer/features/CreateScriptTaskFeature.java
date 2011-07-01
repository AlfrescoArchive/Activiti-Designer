package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateScriptTaskFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "scripttask";

	public CreateScriptTaskFeature(IFeatureProvider fp) {
		super(fp, "ScriptTask", "Add script task");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	@Override
	public Object[] create(ICreateContext context) {
		ScriptTask newScriptTask = Bpmn2Factory.eINSTANCE.createScriptTask();
		newScriptTask.setId(getNextId());
		newScriptTask.setName("Script Task");
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(newScriptTask);
    } else {
      getDiagram().eResource().getContents().add(newScriptTask);
    }
		
    addGraphicalContent(newScriptTask, context);
		
		return new Object[] { newScriptTask };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_SCRIPTTASK;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createScriptTask().getClass();
	}

}
