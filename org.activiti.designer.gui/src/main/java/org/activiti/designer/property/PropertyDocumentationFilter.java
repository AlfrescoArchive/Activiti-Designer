package org.activiti.designer.property;

import org.activiti.bpmn.model.FlowElement;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyDocumentationFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof FlowElement) {
			return true;
		}
		return false;
	}

}
