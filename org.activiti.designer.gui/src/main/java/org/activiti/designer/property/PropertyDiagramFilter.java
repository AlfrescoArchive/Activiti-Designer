package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyDiagramFilter extends ActivitiPropertyFilter {

	public boolean accept(PictogramElement pe) {
		if(pe instanceof Diagram) {
			return true;
		}
		
		Object bo = getBusinessObject(pe);
		if(bo instanceof Pool) {
		  return true;
		}
		
		return false;
	}

}
