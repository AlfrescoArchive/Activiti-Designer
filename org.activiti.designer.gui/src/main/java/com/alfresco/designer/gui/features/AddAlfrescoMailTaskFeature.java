package com.alfresco.designer.gui.features;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddAlfrescoMailTaskFeature extends AddAlfrescoTaskFeature {

	public AddAlfrescoMailTaskFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected String getIcon(EObject bo) {
		return "org.activiti.designer.mail";
	}
}
