package com.alfresco.designer.gui.property;

import org.activiti.designer.bpmn2.model.alfresco.AlfrescoStartEvent;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyAlfrescoStartEventFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof AlfrescoStartEvent) {
			return true;
		}
		return false;
	}

}
