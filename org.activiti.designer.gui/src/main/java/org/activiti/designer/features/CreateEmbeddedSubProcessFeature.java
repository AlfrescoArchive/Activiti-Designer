package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateEmbeddedSubProcessFeature extends AbstractCreateBPMNFeature {

	public static final String FEATURE_ID_KEY = "subprocess";

	public CreateEmbeddedSubProcessFeature(IFeatureProvider fp) {
		super(fp, "SubProcess", "Add sub process");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		if(context.getTargetContainer() instanceof Diagram) return true;
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess == true) {
      return true;
    }
    
    return false;
	}

	@Override
	public Object[] create(ICreateContext context) {
		SubProcess newSubProcess = new SubProcess();
		addObjectToContainer(context, newSubProcess, "Sub Process");

		// return newly created business object(s)
		return new Object[] { newSubProcess };
	}

	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_SUBPROCESS_COLLAPSED;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@Override
	protected Class<? extends FlowElement> getFeatureClass() {
		return new SubProcess().getClass();
	}

}
