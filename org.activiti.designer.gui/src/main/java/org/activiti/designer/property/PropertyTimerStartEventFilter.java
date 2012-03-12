package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoStartEvent;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyTimerStartEventFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof StartEvent && bo instanceof AlfrescoStartEvent == false) {
			if (((StartEvent) bo).getEventDefinitions().size() > 0) {
				return true;
			}
		}
		return false;
	}

}
