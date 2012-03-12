package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.ManualTask;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateManualTaskFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "manualtask";

	public CreateManualTaskFeature(IFeatureProvider fp) {
		super(fp, "ManualTask", "Add manual task");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	@Override
	public Object[] create(ICreateContext context) {
		ManualTask newManualTask = new ManualTask();
		newManualTask.setId(getNextId());
		setName("Manual Task", newManualTask, context);
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(newManualTask);
    } else {
    	ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).addFlowElement(newManualTask);
    }
		
    addGraphicalContent(newManualTask, context);
		return new Object[] { newManualTask };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_MANUALTASK;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return new ManualTask().getClass();
	}

}
