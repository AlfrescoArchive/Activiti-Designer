package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.MailTask;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateMailTaskFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "mailtask";

	public CreateMailTaskFeature(IFeatureProvider fp) {
		super(fp, "MailTask", "Add mail task");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	@Override
	public Object[] create(ICreateContext context) {
		MailTask newMailTask = Bpmn2Factory.eINSTANCE.createMailTask();
		newMailTask.setId(getNextId());
		setName("Mail Task", newMailTask, context);
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(newMailTask);
    } else {
      getDiagram().eResource().getContents().add(newMailTask);
    }
		
    addGraphicalContent(newMailTask, context);
		return new Object[] { newMailTask };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_MAILTASK;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createMailTask().getClass();
	}

}
