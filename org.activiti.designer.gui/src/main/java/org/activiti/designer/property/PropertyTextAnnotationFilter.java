package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.TextAnnotation;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyTextAnnotationFilter extends ActivitiPropertyFilter {

	@Override
	protected boolean accept(PictogramElement element) {
		return getBusinessObject(element) instanceof TextAnnotation;
	}
}
