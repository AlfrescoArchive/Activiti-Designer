package com.alfresco.designer.gui.property;

import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.alfresco.AlfrescoScriptTask;
import org.activiti.designer.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyAlfrescoScriptTaskFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof ServiceTask) {
		  ServiceTask serviceTask = (ServiceTask) bo;
		  if (AlfrescoScriptTask.ALFRESCO_SCRIPT_DELEGATE.equalsIgnoreCase(serviceTask.getImplementation()) &&
              isAlfrescoMailScriptTask((ServiceTask) bo) == false) {
        
        return true;
      }
		}
		return false;
	}
}
