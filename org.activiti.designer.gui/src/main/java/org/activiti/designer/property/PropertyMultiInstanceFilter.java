package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyMultiInstanceFilter extends ActivitiPropertyFilter {

	public boolean accept(PictogramElement pe) {
	  Object bo = getBusinessObject(pe);
    if (bo instanceof Activity) {
			return true;
		}
		return false;
	}

}
