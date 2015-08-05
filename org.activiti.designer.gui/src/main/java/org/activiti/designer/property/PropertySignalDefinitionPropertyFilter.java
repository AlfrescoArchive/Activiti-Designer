package org.activiti.designer.property;

import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertySignalDefinitionPropertyFilter extends ActivitiPropertyFilter {

	@Override
	protected boolean accept(PictogramElement pe) {
		if (pe instanceof Diagram) {
			return true;
		}
		return false;
	}
}
