/**
 * 
 */
package org.activiti.designer.validation.bpmn20.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.extension.validation.AbstractProcessValidator;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.validation.bpmn20.bundle.PluginConstants;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorkerInfo;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorkerMarker;
import org.activiti.designer.validation.bpmn20.validation.worker.impl.ScriptTaskValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.impl.SequenceFlowValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.impl.ServiceTaskValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.impl.SubProcessValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.impl.UserTaskValidationWorker;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 2
 * 
 */
public class BPMN20ProcessValidator extends AbstractProcessValidator {

  private boolean overallResult;

  /**
	 * 
	 */
  public BPMN20ProcessValidator() {
  }

  @Override
  public String getValidatorId() {
    return ActivitiBPMNDiagramConstants.BPMN_VALIDATOR_ID;
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

    this.overallResult = true;

    monitor.beginTask("", PluginConstants.WORK_TOTAL);

    // Clear problems for this diagram first
    clearMarkers(getResource(diagram.eResource().getURI()));

    final Map<String, List<Object>> processNodes = extractProcessConstructs(ModelHandler.getModel(EcoreUtil.getURI(diagram)).getProcess().getFlowElements(), 
    		new SubProgressMonitor(monitor, PluginConstants.WORK_EXTRACT_CONSTRUCTS));

    final List<ProcessValidationWorkerInfo> workers = getWorkers();

    for (final ProcessValidationWorkerInfo worker : workers) {

      Collection<ProcessValidationWorkerMarker> result = worker.getProcessValidationWorker().validate(diagram, processNodes);
      if (result.size() > 0) {
        for (final ProcessValidationWorkerMarker marker : result) {
          final String markerMessage = String.format(PluginConstants.MARKER_MESSAGE_PATTERN, marker.getCode().getDisplayName(), marker.getMessage());
          switch (marker.getSeverity()) {
          case IMarker.SEVERITY_ERROR:
            addProblemToDiagram(diagram, markerMessage, marker.getNodeId());
            break;
          case IMarker.SEVERITY_WARNING:
            addWarningToDiagram(diagram, markerMessage, marker.getNodeId());
            break;
          case IMarker.SEVERITY_INFO:
            addInfoToDiagram(diagram, markerMessage, marker.getNodeId());
            break;
          }
        }
      }
      monitor.worked(worker.getWork());
    }

    monitor.done();
    return overallResult;
  }

  private List<ProcessValidationWorkerInfo> getWorkers() {

    List<ProcessValidationWorkerInfo> result = new ArrayList<ProcessValidationWorkerInfo>();

    result.add(new ProcessValidationWorkerInfo(new UserTaskValidationWorker(), PluginConstants.WORK_USER_TASK));
    result.add(new ProcessValidationWorkerInfo(new ScriptTaskValidationWorker(), PluginConstants.WORK_SCRIPT_TASK));
    result.add(new ProcessValidationWorkerInfo(new ServiceTaskValidationWorker(), PluginConstants.WORK_SERVICE_TASK));
    result.add(new ProcessValidationWorkerInfo(new SequenceFlowValidationWorker(), PluginConstants.WORK_SEQUENCE_FLOW));
    result.add(new ProcessValidationWorkerInfo(new SubProcessValidationWorker(), PluginConstants.WORK_SUB_PROCESS));

    return result;

  }

  @Override
  protected void addProblemToDiagram(Diagram diagram, String message, String nodeId) {
    super.addProblemToDiagram(diagram, message, nodeId);
    overallResult = false;
  }

}
