package org.activiti.designer.util.property;

import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class ActivitiPropertyFilter extends AbstractPropertySectionFilter {

	protected Object getBusinessObject(PictogramElement element) {
		Diagram diagram = getContainer(element);
		Bpmn2MemoryModel model = (ModelHandler.getModel(EcoreUtil.getURI(diagram)));
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
}
