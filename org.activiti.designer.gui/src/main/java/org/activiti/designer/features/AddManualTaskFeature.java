package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddManualTaskFeature extends AddTaskFeature {

	public AddManualTaskFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected String getIcon(Object bo) {
		return ActivitiImageProvider.IMG_MANUALTASK;
	}
}
