package org.activiti.designer.property;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.SequenceFlow;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyExecutionListenerFilter extends ActivitiPropertyFilter {

	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof Activity) {
			return true;
		} else if (bo instanceof SequenceFlow || pe instanceof Diagram) {
		  return true;
		} else if (bo instanceof Pool) {
		  return true;
		}
		return false;
	}

}
