package org.activiti.designer.property;

import org.activiti.bpmn.model.EventDefinition;
import org.activiti.bpmn.model.SignalEventDefinition;
import org.activiti.bpmn.model.ThrowEvent;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyIntermediateThrowSignalFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof ThrowEvent && ((ThrowEvent) bo).getEventDefinitions().size() > 0) {
		  EventDefinition eventDef = ((ThrowEvent) bo).getEventDefinitions().get(0);
		  if (eventDef instanceof SignalEventDefinition) {
		    return true;
		  }
		}
		return false;
	}

}
