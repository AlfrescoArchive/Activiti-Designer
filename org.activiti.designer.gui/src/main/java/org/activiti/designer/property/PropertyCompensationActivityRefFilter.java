package org.activiti.designer.property;

import org.activiti.bpmn.model.CompensateEventDefinition;
import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.ThrowEvent;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyCompensationActivityRefFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof ThrowEvent) {
		  Event event = (Event) bo;
			if (event.getEventDefinitions().size() > 0 && event.getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
				return true;
			}
		}
		return false;
	}

}
