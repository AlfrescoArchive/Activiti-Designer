package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.ScriptTask;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
		ScriptTask newScriptTask = new ScriptTask();
		newScriptTask.setId(getNextId());
		setName("Script Task", newScriptTask, context);
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(newScriptTask);
    } else {
    	ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).addFlowElement(newScriptTask);
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
		return new ScriptTask().getClass();
	}

}
