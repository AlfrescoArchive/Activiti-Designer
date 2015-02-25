package org.activiti.designer.features;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DeleteMessageFlowFeature extends DefaultDeleteFeature {

	public DeleteMessageFlowFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected void deleteBusinessObject(Object bo) {
		removeElement((BaseElement) bo);
	}
	
	private void removeElement(BaseElement element) {
  	BpmnModel bpmnModel = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel();
  	bpmnModel.getMessageFlows().remove(element.getId());
	}
}
