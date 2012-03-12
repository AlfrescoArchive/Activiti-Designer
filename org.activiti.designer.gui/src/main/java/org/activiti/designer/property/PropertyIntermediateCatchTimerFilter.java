package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.EventDefinition;
import org.activiti.designer.bpmn2.model.IntermediateCatchEvent;
import org.activiti.designer.bpmn2.model.TimerEventDefinition;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyIntermediateCatchTimerFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
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
