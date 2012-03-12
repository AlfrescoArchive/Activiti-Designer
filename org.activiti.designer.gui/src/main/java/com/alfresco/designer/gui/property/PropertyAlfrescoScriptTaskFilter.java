package com.alfresco.designer.gui.property;

import org.activiti.designer.bpmn2.model.alfresco.AlfrescoScriptTask;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyAlfrescoScriptTaskFilter extends AbstractPropertySectionFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
		if (bo instanceof AlfrescoScriptTask) {
			return true;
		}
		return false;
	}

}
