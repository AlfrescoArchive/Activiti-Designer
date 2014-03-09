package org.activiti.designer.validation.bpmn20.validation.indev;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.designer.eclipse.extension.validation.ValidationResults;
import org.activiti.designer.eclipse.extension.validation.ValidationResults.ValidationResult;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorker;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Business process verificator - DO NOT USE THIS, will be removed
 * 
 * @author Juraj Husar (jurosh@jurosh.com)
 *
 */
@Deprecated
public class BPVerificator {

	
	/**
	 * Run verification process
	 * 
	 * this method is used just to quick testing
	 * 
	 * @return
	 */
	public ValidationResults validate(Diagram diagram) {

		// get model
		URI uri = EcoreUtil.getURI(diagram);
		BpmnMemoryModel model = ModelHandler.getModel(uri);
		BpmnModel bmodel = model.getBpmnModel();

		// create map
		Map<String, List<Object>> processNodes = extractProcessConstructs(bmodel);
		
		// create and run workers
		ProcessValidationWorker officialRulesValidationWorker = new OfficialRulesValidationWorker();
		officialRulesValidationWorker.validate(diagram, processNodes);
		
		ProcessValidationWorker styleRulesValidationWorker = new StyleRulesValidationWoker();
		styleRulesValidationWorker.validate(diagram, processNodes);
		
		// results
		ValidationResults results = new ValidationResults();
		
		return results;
	}
	
//	// TODO Auto-generated method stub
//	// [1] RULE: have all pools NAMES ?
//	// INFO LOW
//
//	for (Pool pool : getNodes(Pool.class)) {
//		if (pool.getName() == null || pool.getName().trim().isEmpty()) {
//			// createErr(TYPE_INFO, pool, formatName(pool) +
//			// " should have name.");
//			System.out.println(formatName(pool) + " should have name.");
//		}
//	}
	
	private static ValidationResult createErr(String type, BaseElement elem , String msg) {
		System.out.println("[ValidationError]" + msg);
		return new ValidationResult(type, msg, elem);
	}
	
	private static String formatName(BaseElement element) {
		return element.getClass().getSimpleName() + " [" + element.getId() + "]";
	}
	
	
	@Deprecated
	private void validateTest() {
		BpmnMemoryModel model = null;

		// label.setText("Validation initialized");

		boolean hasErrors = false;

		// getStart element
		for (Process process : model.getBpmnModel().getProcesses()) {
			for (FlowElement element : process.getFlowElements()) {
				if (element instanceof StartEvent) {
					// just test validation: is every start event connected to
					// some element
					if (((StartEvent) element).getOutgoingFlows().size() == 0) {
						// Something wrong there, start element
						// (id=" + element.getId() + ") is not conected..");
						System.out								.println("validation ERROR, start not conected");
						hasErrors = true;
					}
				}
			}
		}

		// do validation
		for (Pool pool : model.getBpmnModel().getPools()) {
			System.out.println(pool.getName());
		}

		if (hasErrors) {
			System.out.println("Validation done, with errrors!!!");
		} else {
			System.out.println("Validation success, no errors...");
			// label.setText("NBo errros found..");
		}

		System.out.print("VALIDATE TEST RUN!");
	}
	
	private Map<String, List<Object>> extractProcessConstructs(final BpmnModel model) {

	    Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();

	    final Map<String, List<Object>> result = new HashMap<String, List<Object>>();
	    
	    // new code
	    List<? extends Object> pools = model.getPools();
	    result.put(Pool.class.getCanonicalName(), (List<Object>) pools);
	    
	    // old code

	    for (final FlowElement object : flowElements) {

	      String nodeType = null;

	      nodeType = object.getClass().getCanonicalName();

	      if (nodeType != null) {
	        if (!result.containsKey(nodeType)) {
	          result.put(nodeType, new ArrayList<Object>());
	        }
	        result.get(nodeType).add(object);
	      }

	    }


	    return result;
	  }
}
