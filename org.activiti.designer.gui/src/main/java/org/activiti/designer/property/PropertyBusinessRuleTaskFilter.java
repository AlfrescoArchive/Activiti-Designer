package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.BusinessRuleTask;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyBusinessRuleTaskFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof BusinessRuleTask) {
			return true;
		}
		return false;
	}

}
