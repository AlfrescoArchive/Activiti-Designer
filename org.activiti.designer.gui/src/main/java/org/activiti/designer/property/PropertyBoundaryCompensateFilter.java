package org.activiti.designer.property;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.CompensateEventDefinition;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyBoundaryCompensateFilter extends ActivitiPropertyFilter {

	  @Override
	  protected boolean accept(PictogramElement pe) {
	    Object bo = getBusinessObject(pe);
	    if (bo instanceof BoundaryEvent) {
	      if (((BoundaryEvent) bo).getEventDefinitions().size() > 0) {
	        if (((BoundaryEvent) bo).getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
	          return true;
	        }
	      }
	    }
	    return false;
	  }
}
