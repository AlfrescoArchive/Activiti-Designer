package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.MailTask;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoMailTask;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyMailTaskFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof MailTask && bo instanceof AlfrescoMailTask == false) {
			return true;
		}
		return false;
	}

}
