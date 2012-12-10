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
		  if(((EndEvent) bo).getEventDefinitions() != null) {
        for(EventDefinition eventDefinition : ((EndEvent) bo).getEventDefinitions()) {
          if(eventDefinition instanceof ErrorEventDefinition) {
            return true;
          }
        }
      }
		}
		return false;
	}

}
