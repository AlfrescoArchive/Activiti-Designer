package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.SubProcess;
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
		ManualTask newManualTask = Bpmn2Factory.eINSTANCE.createManualTask();
		newManualTask.setId(getNextId());
		setName("Manual Task", newManualTask, context);
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(newManualTask);
    } else {
      getDiagram().eResource().getContents().add(newManualTask);
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
		return Bpmn2Factory.eINSTANCE.createManualTask().getClass();
	}

}
