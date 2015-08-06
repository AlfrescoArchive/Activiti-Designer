package org.activiti.designer.property;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.ErrorEventDefinition;
import org.activiti.bpmn.model.EventDefinition;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyBoundaryErrorFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof BoundaryEvent) {
		  if (((BoundaryEvent) bo).getEventDefinitions() != null) {
		    for (EventDefinition eventDefinition : ((BoundaryEvent) bo).getEventDefinitions()) {
		      if (eventDefinition instanceof ErrorEventDefinition) {
		        return true;
		      }
		    }
		  }
		}
		return false;
	}

}
