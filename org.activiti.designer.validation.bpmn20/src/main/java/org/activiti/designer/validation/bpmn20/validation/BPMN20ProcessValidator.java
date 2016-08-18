/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package org.activiti.designer.validation.bpmn20.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.designer.eclipse.extension.validation.AbstractProcessValidator;
import org.activiti.designer.util.ActivitiConstants;
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
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 2
 * 
 */
public class BPMN20ProcessValidator extends AbstractProcessValidator {

  private static final int EXTRACTION_WORK_UNIT = 10;

  private boolean overallResult;

  /**
	 * 
	 */
  public BPMN20ProcessValidator() {
  }

  @Override
  public String getValidatorId() {
    return ActivitiConstants.BPMN_VALIDATOR_ID;
  }

  @Override
  public String getValidatorName() {
    return ActivitiConstants.BPMN_VALIDATOR_NAME;
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
    clearMarkers(getDiagramResource());

    final Map<String, List<Object>> processNodes = extractProcessConstructs(new SubProgressMonitor(monitor, PluginConstants.WORK_EXTRACT_CONSTRUCTS));

    final List<ProcessValidationWorkerInfo> workers = getWorkers();

    for (final ProcessValidationWorkerInfo worker : workers) {

      Collection<ProcessValidationWorkerMarker> result = worker.getProcessValidationWorker().validate(diagram, processNodes);
      if (result.size() > 0) {
        for (final ProcessValidationWorkerMarker marker : result) {
          final String markerMessage = String.format(PluginConstants.MARKER_MESSAGE_PATTERN, marker.getCode().getDisplayName(), marker.getMessage());
          switch (marker.getSeverity()) {
          case IMarker.SEVERITY_ERROR:
            addProblemToDiagram(markerMessage, marker.getNodeId());
            break;
          case IMarker.SEVERITY_WARNING:
            addWarningToDiagram(markerMessage, marker.getNodeId());
            break;
          case IMarker.SEVERITY_INFO:
            addInfoToDiagram(markerMessage, marker.getNodeId());
            break;
          }
        }
      }
      monitor.worked(worker.getWork());
    }

    monitor.done();
    return overallResult;
  }

  private Map<String, List<Object>> extractProcessConstructs(final IProgressMonitor monitor) {

    Collection<FlowElement> flowElements = getDiagramWorkerContext().getBpmnModel().getBpmnModel().getMainProcess().getFlowElements();
    monitor.beginTask("Analyzing process constructs", flowElements.size() * EXTRACTION_WORK_UNIT);

    final Map<String, List<Object>> result = new HashMap<String, List<Object>>();

    for (final FlowElement object : flowElements) {

      String nodeType = null;

      nodeType = object.getClass().getCanonicalName();

      if (nodeType != null) {
        if (!result.containsKey(nodeType)) {
          result.put(nodeType, new ArrayList<Object>());
        }
        result.get(nodeType).add(object);
      }

      monitor.worked(EXTRACTION_WORK_UNIT);
    }

    monitor.done();

    return result;
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
  protected void addProblemToDiagram(String message, String nodeId) {
    super.addProblemToDiagram(message, nodeId);
    overallResult = false;
  }

}
