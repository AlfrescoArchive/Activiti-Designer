package org.activiti.designer.validation.bpmn20.validation.indev;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.designer.eclipse.extension.validation.ValidationResults.ValidationResult;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.validation.bpmn20.validation.worker.AbstractValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorkerMarker;
import org.activiti.designer.validation.bpmn20.validation.worker.impl.ValidationCode;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * 
 * @author Jurosh
 * 
 */
public abstract class AbstractAdvancedValidatorWorker extends AbstractValidationWorker {

	/**
	 * Format name for output
	 * 
	 * @param element
	 * @return
	 */
	protected static String formatName(BaseElement element) {
		return element.getClass().getSimpleName() + " [" + element.getId() + "]";
	}

	/**
	 * 
	 * @param type
	 * @param elem
	 * @param msg
	 * @return
	 */
	protected void createErr(int severity, String msg, BaseElement elem) {
		System.out.println("[ValidationError]" + msg);

		// new (but temporary) result object
		ValidationResult validationResult = new ValidationResult(severity, msg, elem);

		// original result object
		ProcessValidationWorkerMarker processValidationWorkerMarker = new ProcessValidationWorkerMarker(validationResult, ValidationCode.VAL_100);
		
		results.add(processValidationWorkerMarker);
	}
	
	/**
	 * Try to do stuff without this method, but probably will be needed TODO:
	 * correct way of getting model, not it should probably take not always
	 * selected
	 * 
	 * @return
	 */
	protected BpmnModel getModel() {
		URI uri = EcoreUtil.getURI(diagram);
		BpmnMemoryModel model = ModelHandler.getModel(uri);
		return model.getBpmnModel();
	}

}
