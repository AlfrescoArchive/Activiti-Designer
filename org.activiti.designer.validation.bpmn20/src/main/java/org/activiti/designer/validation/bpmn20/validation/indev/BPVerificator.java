package org.activiti.designer.validation.bpmn20.validation.indev;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.Process;
import org.activiti.designer.util.editor.BpmnMemoryModel;

/**
 * 
 * @author Juraj Husar (jurosh@jurosh.com)
 *
 */
public class BPVerificator {

	public void run() {
		
	}
	
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
