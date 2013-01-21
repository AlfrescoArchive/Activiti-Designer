package org.activiti.designer.property;

import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ErrorEventDefinition;
import org.activiti.bpmn.model.EventDefinition;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyErrorEndEventFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if(bo instanceof EndEvent) {
		  EndEvent endEvent = (EndEvent) bo;
		  if(endEvent.getEventDefinitions() != null) {
        for(EventDefinition eventDefinition : endEvent.getEventDefinitions()) {
          if(eventDefinition instanceof ErrorEventDefinition) {
            return true;
          }
        }
      }
		}
		return false;
	}

}
