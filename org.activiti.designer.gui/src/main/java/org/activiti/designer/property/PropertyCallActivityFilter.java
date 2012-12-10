package org.activiti.designer.property;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyCallActivityFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof CallActivity) {
			return true;
		}
		return false;
	}

}
