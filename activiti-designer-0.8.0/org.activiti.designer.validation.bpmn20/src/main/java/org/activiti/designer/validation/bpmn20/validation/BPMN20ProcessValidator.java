/**
 * 
 */
package org.activiti.designer.validation.bpmn20.validation;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.extension.validation.AbstractProcessValidator;
import org.activiti.designer.property.extension.util.ExtensionUtil;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 * 
 */
public class BPMN20ProcessValidator extends AbstractProcessValidator {

  private static final String BPMN2_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";
  private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
  private static final String SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
  private static final String XPATH_NAMESPACE = "http://www.w3.org/1999/XPath";
  private static final String PROCESS_NAMESPACE = "http://www.activiti.org/test";
  private static final String ACTIVITI_EXTENSIONS_NAMESPACE = "http://activiti.org/bpmn";
  private static final String ACTIVITI_EXTENSIONS_PREFIX = "activiti";
  private static final String VALIDATION_TITLE = "BPMN 2.0 Validation";

  private IProgressMonitor monitor;
  private Diagram diagram;

  /**
	 * 
	 */
  public BPMN20ProcessValidator() {
  }

  @Override
  public String getValidatorName() {
    return ActivitiBPMNDiagramConstants.BPMN_VALIDATOR_NAME;
  }

  @Override
  public String getFormatName() {
    return "Activiti BPMN 2.0";
  }

  @Override
  public boolean validateDiagram(Diagram diagram, IProgressMonitor monitor) {
    this.monitor = monitor;
    this.diagram = diagram;

    boolean validBpmn = true;
    
    monitor.beginTask("", 100);

    // Clear problems for this diagram first
    clearMarkers(getResource(diagram.eResource().getURI()));

    validateDiagram(getResourceForDiagram(diagram).getContents());

    if (getMarkers(getResource(diagram.eResource().getURI())) != null && 
            getMarkers(getResource(diagram.eResource().getURI())).length > 0) {
      
      validBpmn = false;
    }
    return validBpmn;
  }

  private void validateDiagram(EList<EObject> contents) {

    for (EObject object : contents) {
      if(object instanceof FlowElement) {
        validateElement((FlowElement) object);
      }
    }
  }
  
  private void validateElement(FlowElement flowElement) {
    if (flowElement instanceof UserTask) {
      UserTask userTask = (UserTask) flowElement;
      boolean potentialOwnerIsSet = false;
      if (userTask.getAssignee() != null && userTask.getAssignee().length() > 0) {
        potentialOwnerIsSet = true;
      }
      if (userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0) {
        potentialOwnerIsSet = true;
      }
      if (userTask.getCandidateGroups() != null && userTask.getCandidateGroups().size() > 0) {
        potentialOwnerIsSet = true;
      }
      if (potentialOwnerIsSet == false) {
        createErrorMessage("UserTask " + userTask.getName() + " has no assignee, candidate users, candidate groups set");
      }

    } else if (flowElement instanceof ScriptTask) {
      ScriptTask scriptTask = (ScriptTask) flowElement;
      if (scriptTask.getScriptFormat() == null || scriptTask.getScriptFormat().length() == 0) {
        createErrorMessage("ScriptTask " + scriptTask.getName() + " has no format specified");
      }
      if (scriptTask.getScript() == null || scriptTask.getScript().length() == 0) {
        createErrorMessage("ScriptTask " + scriptTask.getName() + " has no script logic specified");
      }

    } else if (flowElement instanceof ServiceTask && ExtensionUtil.isCustomServiceTask(flowElement) == false) {
      ServiceTask serviceTask = (ServiceTask) flowElement;
      if ((serviceTask.getImplementationType() == null || serviceTask.getImplementationType().length() == 0 || "classType".equalsIgnoreCase(serviceTask
              .getImplementationType())) && serviceTask.getImplementation() == null || serviceTask.getImplementation().length() == 0) {
        createErrorMessage("ServiceTask " + serviceTask.getName() + " has no class specified");
      }
    } else if (flowElement instanceof SequenceFlow) {
      SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
      if (sequenceFlow.getSourceRef() == null || sequenceFlow.getSourceRef().getId() == null || sequenceFlow.getSourceRef().getId().length() == 0) {
        createErrorMessage("SequenceFlow " + sequenceFlow.getName() + " has no source activity");
      }
      if (sequenceFlow.getTargetRef() == null || sequenceFlow.getTargetRef().getId() == null || sequenceFlow.getTargetRef().getId().length() == 0) {
        createErrorMessage("SequenceFlow " + sequenceFlow.getName() + " has no target activity");
      }
    } else if (flowElement instanceof SubProcess) {
      SubProcess subProcess = (SubProcess) flowElement;
      if(subProcess.getFlowElements() != null) {
        for (FlowElement subElement : subProcess.getFlowElements()) {
          validateElement(subElement);
        }
      }
    }
  }

  private void createErrorMessage(String message) {
    addProblemToDiagram(diagram, message, null);
  }

  @Override
  public String getValidatorId() {
    return ActivitiBPMNDiagramConstants.BPMN_VALIDATOR_ID;
  }
}
