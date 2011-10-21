package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddScriptTaskFeature extends AddTaskFeature {

	public AddScriptTaskFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected String getIcon(EObject bo) {
		return ActivitiImageProvider.IMG_SCRIPTTASK;
	}

}
