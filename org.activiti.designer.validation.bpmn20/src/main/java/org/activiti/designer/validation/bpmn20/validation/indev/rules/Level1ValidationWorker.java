package org.activiti.designer.validation.bpmn20.validation.indev.rules;

import static org.activiti.designer.eclipse.extension.validation.ValidationResults.TYPE_ERROR;

import java.util.List;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.designer.validation.bpmn20.validation.indev.AbstractAdvancedValidatorWorker;

/**
 * BPMN 2.0 Level 1 Palete Verification Rules
 * 
 * @author Jurosh
 * 
 */
public class Level1ValidationWorker extends AbstractAdvancedValidatorWorker {

	@Override
	public void validate() {

		/**
		 * [O1-1] A sequence flow may not cross a pool (process) boundary.
		 */
		// TODO

		/**
		 * [O1-2] A sequence flow may not cross a subprocess boundary.
		 */
		// TODO

		/**
		 * [O1-3] A Message flow may not connect nodes in the same pool
		 * 
		 * priority: MEDIUM
		 */
		for (Process process : getModel().getProcesses()) {
			for (FlowElement elem : process.getFlowElements()) {
				if (elem instanceof BoundaryEvent) {
					// System.out.println(elem.getId());
					// TODO detect message boundary and check ref element, if is
					// on same pool or not
				}
			}
		}

		/**
		 * [O1-4] A sequence flow may only be connect to an activity, gateway,
		 * or event, and both ends must be properly connected
		 * 
		 * priority: HIGH
		 */
		for (Process process : getModel().getProcesses()) {
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

		/**
		 * [O1-5] A message flow may only connect to an activity, Message (or
		 * Multiple) event, or black-box pool, and both ends must be properly
		 * connected.
		 */
		// TODO

	}

}
