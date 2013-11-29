package org.activiti.designer.property;

import org.activiti.bpmn.model.EventDefinition;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.TimerEventDefinition;
import org.activiti.bpmn.model.UserTask;
import org.activiti.bpmn.model.alfresco.AlfrescoStartEvent;
import org.activiti.bpmn.model.alfresco.AlfrescoUserTask;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyFormPropertyFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof UserTask && bo instanceof AlfrescoUserTask == false) {
		  return true;
		} else if (bo instanceof StartEvent && bo instanceof AlfrescoStartEvent == false) {
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
