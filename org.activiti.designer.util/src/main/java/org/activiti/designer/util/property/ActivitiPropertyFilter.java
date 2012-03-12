package org.activiti.designer.util.property;

import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class ActivitiPropertyFilter extends AbstractPropertySectionFilter {

	protected Object getBusinessObject(PictogramElement element) {
		Bpmn2MemoryModel model = (ModelHandler.getModel(EcoreUtil.getURI(element)));
  	if(model == null) {
  		model = (ModelHandler.getModel(EcoreUtil.getURI(element.eContainer())));
  	}
  	if(model != null) {
  		return model.getFeatureProvider().getBusinessObjectForPictogramElement(element);
  	}
  	return null;
	}

	@Override
  protected boolean accept(PictogramElement element) {
	  return false;
  }
}
