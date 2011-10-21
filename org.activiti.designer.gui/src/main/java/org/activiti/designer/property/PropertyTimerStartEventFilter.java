package org.activiti.designer.property;

import org.eclipse.bpmn2.AlfrescoStartEvent;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyTimerStartEventFilter extends AbstractPropertySectionFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
		if (bo instanceof StartEvent && bo instanceof AlfrescoStartEvent == false) {
			if (((StartEvent) bo).getEventDefinitions().size() > 0) {
				return true;
			}
		}
		return false;
	}

}
