package org.activiti.designer.property;

import org.activiti.bpmn.model.SubProcess;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyDataPropertyFilter extends ActivitiPropertyFilter {

	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (pe instanceof Diagram) {
			return true;
		} else if (bo instanceof SubProcess) {
			return true; 
		}
		return false;
	}
}
