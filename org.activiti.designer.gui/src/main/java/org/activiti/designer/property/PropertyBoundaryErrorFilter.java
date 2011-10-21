package org.activiti.designer.property;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyBoundaryErrorFilter extends AbstractPropertySectionFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
		if (bo instanceof BoundaryEvent) {
		  if(((BoundaryEvent) bo).getEventDefinitions() != null) {
		    for(EventDefinition eventDefinition : ((BoundaryEvent) bo).getEventDefinitions()) {
		      if(eventDefinition instanceof ErrorEventDefinition) {
		        return true;
		      }
		    }
		  }
		} else if(bo instanceof EndEvent) {
		  if(((EndEvent) bo).getEventDefinitions() != null) {
        for(EventDefinition eventDefinition : ((EndEvent) bo).getEventDefinitions()) {
          if(eventDefinition instanceof ErrorEventDefinition) {
            return true;
          }
        }
      }
		}
		return false;
	}

}
