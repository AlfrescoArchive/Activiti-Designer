package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddBusinessRuleTaskFeature extends AddTaskFeature {

	public AddBusinessRuleTaskFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected String getIcon(Object bo) {
		return ActivitiImageProvider.IMG_BUSINESSRULETASK;
	}
}
