package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddMailTaskFeature extends AddTaskFeature {

	public AddMailTaskFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected String getIcon(EObject bo) {
		return ActivitiImageProvider.IMG_MAILTASK;
	}
}
