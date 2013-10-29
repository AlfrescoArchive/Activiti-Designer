package org.activiti.designer.property;

import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class ActivitiPropertyFilter extends AbstractPropertySectionFilter {

	protected Object getBusinessObject(PictogramElement element) {
	  if (element == null) return null;
		Diagram diagram = getContainer(element);
		BpmnMemoryModel model = (ModelHandler.getModel(EcoreUtil.getURI(diagram)));
  	if(model != null) {
  		return model.getFeatureProvider().getBusinessObjectForPictogramElement(element);
  	}
  	return null;
	}
	
	private Diagram getContainer(EObject container) {
		if(container instanceof Diagram) {
			return (Diagram) container;
		} else {
			return getContainer(container.eContainer());
		}
	}

	@Override
  protected boolean accept(PictogramElement element) {
	  return false;
  }
	
	protected boolean isAlfrescoMailScriptTask(ServiceTask serviceTask) {
	  boolean isMailTask = false;
	  for (FieldExtension fieldExtension : serviceTask.getFieldExtensions()) {
	    if ("script".equalsIgnoreCase(fieldExtension.getFieldName())) {
	      if (fieldExtension.getStringValue().contains("mail.execute(bpm_package);")) {
	        isMailTask = true;
	      }
	    }
	  }
	  return isMailTask;
	}
}
