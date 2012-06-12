package com.alfresco.designer.gui.features;

import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddAlfrescoUserTaskFeature extends AddAlfrescoTaskFeature {

	public AddAlfrescoUserTaskFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected String getIcon(Object bo) {
		return PluginImage.IMG_USERTASK.getImageKey();
	}
}
