package org.activiti.designer.validation.bpmn20.validation.indev;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.Pool;
import org.activiti.designer.validation.bpmn20.validation.worker.AbstractValidationWorker;

/**
 * Official BPMN rules validation
 * 
 * @author Jurosh
 * 
 */
public class OfficialRulesValidationWorker extends AbstractValidationWorker {

	@Override
	public void validate() {

		// TODO Auto-generated method stub
		// [1] RULE: have all pools NAMES ?
		// INFO LOW

		for (Pool pool : getNodes(Pool.class)) {
			if (pool.getName() == null || pool.getName().trim().isEmpty()) {
				// createErr(TYPE_INFO, pool, formatName(pool) +
				// " should have name.");
				System.out.println(formatName(pool) + " should have name.");
			}
		}

	}

	/**
	 * Format name for output
	 * @param element
	 * @return
	 */
	private static String formatName(BaseElement element) {
		return element.getClass().getSimpleName() + " [" + element.getId() + "]";
	}

	
	/*
	private void testValidations() {
		
		// [1] RULE: have all pools NAMES ?
		// INFO LOW

		for (Pool pool : bmodel.getPools()) {
			if (pool.getName() == null || pool.getName().trim().isEmpty()) {
				createErr(TYPE_INFO, pool, formatName(pool) + " should have name.");
			}
		}

		// [O-22] Official BPMN 2.0 - 22 RULE
		// RULE: Sequence flow may not cross a subprocess boundary
		// ERROR HIGH

		// TODO how to get subprocess

		// [O-23] Official BPMN 2.0 - 23 RULE
		// RULE: A message flow may not connect nodes in the same pool
		// ERROR MEDIUM

		for (Process process : bmodel.getProcesses()) {
			for (FlowElement elem : process.getFlowElements()) {
				if (elem instanceof BoundaryEvent) {
					// System.out.println(elem.getId());
					// TODO detect message boundary and check ref element, if is
					// on same pool or not
				}
			}
		}

		// [O-24] Official BPMN 2.0 - 24 RULE
		// RULE: A sequence flow may only be connected to an activity, gateway,
		// or event, and both ends must be properly connected
		// ADD: remove cycle connections
		// ERROR HIGH

		for (Process process : bmodel.getProcesses()) {
			for (FlowElement elem : process.getFlowElements()) {
				if (elem instanceof FlowNode) {
					List<SequenceFlow> outgoingFlows = ((FlowNode) elem).getOutgoingFlows();
					List<SequenceFlow> incomingFlows = ((FlowNode) elem).getIncomingFlows();

					// not allow cycle connections
					if (outgoingFlows.contains(elem) || incomingFlows.contains(elem)) {
						createErr(TYPE_ERROR, elem, formatName(elem) + " is connected to itself.");
						break;
					}

					// just end element should not have outgoing flow
					boolean flowOutError = outgoingFlows.size() == 0 && !(elem instanceof EndEvent);

					// just start element should not have incoming flow
					boolean flowInError = incomingFlows.size() == 0 && !(elem instanceof StartEvent);

					if (flowInError && flowOutError) {
						createErr(TYPE_ERROR, elem, formatName(elem) + " have no incomming and outgoing sequence flow.");

					} else if (flowInError) {
						createErr(TYPE_ERROR, elem, formatName(elem) + " have no incomming sequence flow.");

					} else if (flowOutError) {
						createErr(TYPE_ERROR, elem, formatName(elem) + " don't have next element connection.");
					}

				}

			}
		}

		// NEXT..
	}
	*/

}
