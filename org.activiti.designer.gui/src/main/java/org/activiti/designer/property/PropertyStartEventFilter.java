package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.StartEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyStartEventFilter extends AbstractPropertySectionFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
		if (bo instanceof StartEvent) {
			if (((StartEvent) bo).getEventDefinitions().size() > 0) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

}
