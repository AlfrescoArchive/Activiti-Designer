package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.SignalEventDefinition;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyBoundarySignalFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof BoundaryEvent) {
		  if(((BoundaryEvent) bo).getEventDefinitions().size() > 0) {
		    if(((BoundaryEvent) bo).getEventDefinitions().get(0) instanceof SignalEventDefinition) {
		      return true;
		    }
		  }
		}
		return false;
	}

}
