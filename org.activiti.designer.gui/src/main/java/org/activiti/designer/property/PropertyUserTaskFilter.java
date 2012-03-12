package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoUserTask;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyUserTaskFilter extends AbstractPropertySectionFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
		if (bo instanceof UserTask && bo instanceof AlfrescoUserTask == false) {
			return true;
		}
		return false;
	}

}
