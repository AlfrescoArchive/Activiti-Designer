package org.activiti.designer.property;

import org.activiti.bpmn.model.UserTask;
import org.activiti.bpmn.model.alfresco.AlfrescoUserTask;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyUserTaskFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof UserTask && bo instanceof AlfrescoUserTask == false) {
			return true;
		}
		return false;
	}

}
