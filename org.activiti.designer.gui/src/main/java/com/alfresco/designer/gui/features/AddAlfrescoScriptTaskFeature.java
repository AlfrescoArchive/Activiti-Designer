package com.alfresco.designer.gui.features;

import org.eclipse.graphiti.features.IFeatureProvider;

public class AddAlfrescoScriptTaskFeature extends AddAlfrescoTaskFeature {

	public AddAlfrescoScriptTaskFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected String getIcon(Object bo) {
		return "org.activiti.designer.scripttask";
	}
}
