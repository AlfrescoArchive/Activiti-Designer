package org.activiti.designer.property;

import org.activiti.bpmn.model.EventDefinition;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.TimerEventDefinition;
import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyFormPropertyFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof UserTask) {
		  return true;
		} else if (bo instanceof StartEvent) {
		  StartEvent startEvent = (StartEvent) bo;
      for (EventDefinition event : startEvent.getEventDefinitions()) {
        if (event instanceof TimerEventDefinition == false) {
          return false;
        }
      }
      return true; 
		}
		return false;
	}

}
