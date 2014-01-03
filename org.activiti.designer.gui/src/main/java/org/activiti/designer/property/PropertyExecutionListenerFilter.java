package org.activiti.designer.property;

import org.activiti.bpmn.model.HasExecutionListeners;
import org.activiti.bpmn.model.Pool;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyExecutionListenerFilter extends ActivitiPropertyFilter {

	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof HasExecutionListeners) {
			return true;
		} else if (pe instanceof Diagram) {
		  return true;
		} else if (bo instanceof Pool) {
		  return true;
		}
		return false;
	}

}
