package org.activiti.designer.validation.bpmn20.validation.indev;

import org.activiti.designer.validation.bpmn20.validation.indev.rules.Level1ValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.indev.rules.Level2MessageFlowValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.indev.rules.Level2SequenceFlowValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.indev.rules.Level2StartValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.AbstractValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorker;

/**
 * Official BPMN rules validation
 * 
 * @author Jurosh
 * 
 */
public class OfficialRulesValidationWorker extends AbstractValidationWorker {

	@Override
	public void validate() {

		// run all sub-validator workers
		
		// BPMN 2.0 Level 1   
		ProcessValidationWorker level1ValidationRulesWorker = new Level1ValidationWorker();
		results.addAll(level1ValidationRulesWorker.validate(diagram, nodes));
		
		// BPMN 2.0 Level 2
		ProcessValidationWorker seqFlowWorker = new Level2SequenceFlowValidationWorker();
		results.addAll(seqFlowWorker.validate(diagram, nodes));
		
		ProcessValidationWorker messageFlowWorker = new Level2MessageFlowValidationWorker();
		results.addAll(messageFlowWorker.validate(diagram, nodes));
		
		ProcessValidationWorker startElementWorker = new Level2StartValidationWorker();
		results.addAll(startElementWorker.validate(diagram, nodes));
		// TODO
		
	}


}
