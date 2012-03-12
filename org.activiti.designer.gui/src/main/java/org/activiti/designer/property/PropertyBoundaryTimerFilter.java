package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.EventDefinition;
import org.activiti.designer.bpmn2.model.TimerEventDefinition;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyBoundaryTimerFilter extends AbstractPropertySectionFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
		if (bo instanceof BoundaryEvent) {
		  if(((BoundaryEvent) bo).getEventDefinitions() != null) {
		    for(EventDefinition eventDefinition : ((BoundaryEvent) bo).getEventDefinitions()) {
		      if(eventDefinition instanceof TimerEventDefinition) {
		        return true;
		      }
		    }
		  }
		}
		return false;
	}

}
