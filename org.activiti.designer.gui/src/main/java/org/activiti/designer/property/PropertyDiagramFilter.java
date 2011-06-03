package org.activiti.designer.property;

import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyDiagramFilter extends AbstractPropertySectionFilter {

	public boolean accept(PictogramElement pe) {
		if(pe instanceof Diagram) {
			return true;
		}
		return false;
	}

}
