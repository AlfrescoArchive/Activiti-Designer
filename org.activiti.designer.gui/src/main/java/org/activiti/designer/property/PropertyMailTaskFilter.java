package org.activiti.designer.property;

import org.activiti.bpmn.model.ServiceTask;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyMailTaskFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof ServiceTask && ServiceTask.MAIL_TASK.equalsIgnoreCase(((ServiceTask) bo).getType())) {
			return true;
		}
		return false;
	}

}
