package org.activiti.designer.property;

import org.eclipse.bpmn2.Activity;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyMultiInstanceFilter extends AbstractPropertySectionFilter {

	public boolean accept(PictogramElement pe) {
	  EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
    if (bo instanceof Activity) {
			return true;
		}
		return false;
	}

}
