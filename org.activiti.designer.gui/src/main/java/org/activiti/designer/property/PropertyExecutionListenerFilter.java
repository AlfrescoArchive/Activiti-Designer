package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.property.extension.util.ExtensionUtil;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyExecutionListenerFilter extends ActivitiPropertyFilter {

	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof Activity && ExtensionUtil.isCustomServiceTask(bo) == false && (bo instanceof UserTask == false)) {
			return true;
		} else if (bo instanceof SequenceFlow || pe instanceof Diagram) {
		  return true;
		}
		return false;
	}

}
