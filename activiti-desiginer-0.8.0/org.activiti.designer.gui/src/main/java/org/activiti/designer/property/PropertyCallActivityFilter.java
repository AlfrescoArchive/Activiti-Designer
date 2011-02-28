package org.activiti.designer.property;

import org.eclipse.bpmn2.CallActivity;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyCallActivityFilter extends AbstractPropertySectionFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
		if (bo instanceof CallActivity) {
			return true;
		}
		return false;
	}

}
