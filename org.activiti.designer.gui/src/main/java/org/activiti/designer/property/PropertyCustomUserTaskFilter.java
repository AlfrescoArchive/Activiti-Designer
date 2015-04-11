package org.activiti.designer.property;

import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyCustomUserTaskFilter extends ActivitiPropertyFilter {

	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof UserTask && ExtensionUtil.isCustomUserTask(bo)) {
			return true;
		}
		return false;
	}

}
