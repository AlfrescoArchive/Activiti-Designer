package org.activiti.designer.property;

import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyIntermediateCatchTimerFilter extends AbstractPropertySectionFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
		if (bo instanceof IntermediateCatchEvent) {
		  if(((IntermediateCatchEvent) bo).getEventDefinitions() != null) {
		    for(EventDefinition eventDefinition : ((IntermediateCatchEvent) bo).getEventDefinitions()) {
		      if(eventDefinition instanceof TimerEventDefinition) {
		        return true;
		      }
		    }
		  }
		}
		return false;
	}

}
