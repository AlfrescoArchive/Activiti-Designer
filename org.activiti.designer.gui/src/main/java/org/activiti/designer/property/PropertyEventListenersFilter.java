package org.activiti.designer.property;

import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * @author Frederik Heremans
 */
public class PropertyEventListenersFilter extends ActivitiPropertyFilter {

	public boolean accept(PictogramElement pe) {
		return pe instanceof Diagram;
	}

}
