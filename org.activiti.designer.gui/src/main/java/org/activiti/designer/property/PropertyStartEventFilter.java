package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyStartEventFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof StartEvent) {
			if (((StartEvent) bo).getEventDefinitions().size() > 0) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

}
