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
package org.activiti.designer.validation.bpmn20.validation.worker.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorkerMarker;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Validates process according to {@link ValidationCode#VAL_004}.
 * 
 * @author Tiese Barrell
 * @since 5.6
 * @version 1
 */
public class ServiceTaskValidationWorker implements ProcessValidationWorker {

  private static final String NO_CLASS_EXCEPTION_MESSAGE_PATTERN = "ServiceTask '%s' has no class specified";

  @Override
  public Collection<ProcessValidationWorkerMarker> validate(final Diagram diagram, final Map<String, List<Object>> processNodes) {

    final Collection<ProcessValidationWorkerMarker> result = new ArrayList<ProcessValidationWorkerMarker>();

    final List<Object> serviceTasks = processNodes.get(ServiceTask.class.getCanonicalName());

    if (serviceTasks != null && !serviceTasks.isEmpty()) {
      for (final Object object : serviceTasks) {

        
      }
    }
    
    final List<Object> subProcesses = processNodes.get(SubProcess.class.getCanonicalName());

    if (subProcesses != null && !subProcesses.isEmpty()) {
      for (final Object object : subProcesses) {

        final SubProcess subProcess = (SubProcess) object;
        
        final Map<String, List<Object>> subElementsMap = new HashMap<String, List<Object>>();

        for (final FlowElement subElement : subProcess.getFlowElements()) {

          String nodeType = subElement.getClass().getCanonicalName();

          if (nodeType != null) {
            if (!subElementsMap.containsKey(nodeType)) {
            	subElementsMap.put(nodeType, new ArrayList<Object>());
            }
            subElementsMap.get(nodeType).add(subElement);
          }
        }
        
        result.addAll(validate(diagram, subElementsMap));
      }
    }

    return result;
  }
}
