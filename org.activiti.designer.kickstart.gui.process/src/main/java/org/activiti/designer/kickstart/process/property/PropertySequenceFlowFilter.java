package org.activiti.designer.kickstart.process.property;

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertySequenceFlowFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof SequenceFlow) {
			return true;
		}
		return false;
	}

}
