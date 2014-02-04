package org.activiti.designer.validation.bpmn20.validation.indev;

import static org.activiti.designer.eclipse.extension.validation.ValidationResults.TYPE_ERROR;
import static org.activiti.designer.eclipse.extension.validation.ValidationResults.TYPE_INFO;

import java.util.List;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.designer.eclipse.extension.validation.ValidationResults;
import org.activiti.designer.eclipse.extension.validation.ValidationResults.ValidationResult;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Business process validator
 * 
 * @author Juraj Husar (jurosh@jurosh.com)
 *
 */
public class BPVerificator {

	/**
	 * Run verification process
	 * @return
	 */
	public ValidationResults validate(Diagram diagram) {
		
		// get model
		URI uri = EcoreUtil.getURI(diagram);
		BpmnMemoryModel model = ModelHandler.getModel(uri);
		BpmnModel bmodel = model.getBpmnModel();
		
		// simple validations
		ValidationResults results = new ValidationResults();
		
		// [1] RULE: have all pools NAMES ?
		// INFO LOW
		
		for(Pool pool : bmodel.getPools()) {
			if(pool.getName() == null || pool.getName().trim().isEmpty()) {
				createErr(TYPE_INFO, pool, formatName(pool)+" should have name.");
			}
		}
		
		// [O-22] Official BPMN 2.0 - 22 RULE
		// RULE: Sequence flow may not cross a subprocess boundary
		// ERROR HIGH 
		
		//TODO how to get subprocess
		
		// [O-23] Official BPMN 2.0 - 23 RULE
		// RULE: A message flow may not connect nodes in the same pool
		// ERROR MEDIUM
		
		for(Process process : bmodel.getProcesses()) {
			for(FlowElement elem : process.getFlowElements()) {
				if(elem instanceof BoundaryEvent) {
//					System.out.println(elem.getId());
//					TODO detect message boundary and check ref element, if is on same pool or not
				}
			}
		}
		
		// [O-24] Official BPMN 2.0 - 24 RULE
		// RULE: A sequence flow may only be connected to an activity, gateway, or event, and both ends must be properly connected
		// ADD: remove cycle connections
		// ERROR HIGH
		
		for(Process process : bmodel.getProcesses()) {
			for(FlowElement elem : process.getFlowElements()) {
				if(elem instanceof FlowNode) {
					List<SequenceFlow> outgoingFlows = ((FlowNode) elem).getOutgoingFlows();
					List<SequenceFlow> incomingFlows = ((FlowNode) elem).getIncomingFlows();

					// not allow cycle connections
					if(outgoingFlows.contains(elem) || incomingFlows.contains(elem)) {
						createErr(TYPE_ERROR, elem, formatName(elem) +" is connected to itself.");
						break;
					}
					
					// just end element should not have outgoing flow
					boolean flowOutError = outgoingFlows.size() == 0 && !(elem instanceof EndEvent);
					
					// just start element should not have incoming flow 
					boolean flowInError = incomingFlows.size() == 0 && !(elem instanceof StartEvent);
					
					if(flowInError && flowOutError) {
						createErr(TYPE_ERROR, elem, formatName(elem) +" have no incomming and outgoing sequence flow.");
						
					} else if(flowInError) {
						createErr(TYPE_ERROR, elem, formatName(elem) +" have no incomming sequence flow.");
						
					} else if(flowOutError){
						createErr(TYPE_ERROR, elem, formatName(elem) +" don't have next element connection.");
					}
					
				}
				
			}
		}
		
		// NEXT..
		
		
		return results;
	}
	
	private static ValidationResult createErr(String type, BaseElement elem , String msg) {
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
						System.out
								.println("validation ERROR, start not conected");
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
	
}
