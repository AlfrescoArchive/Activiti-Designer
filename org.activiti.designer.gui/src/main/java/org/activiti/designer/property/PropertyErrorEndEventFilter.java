package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.EndEvent;
import org.activiti.designer.bpmn2.model.ErrorEventDefinition;
import org.activiti.designer.bpmn2.model.EventDefinition;
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
