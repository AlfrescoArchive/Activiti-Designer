package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.features.AbstractCreateBPMNFeature;
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
		return context.getTargetContainer() instanceof Diagram;
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

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return new SubProcess().getClass();
	}

}
