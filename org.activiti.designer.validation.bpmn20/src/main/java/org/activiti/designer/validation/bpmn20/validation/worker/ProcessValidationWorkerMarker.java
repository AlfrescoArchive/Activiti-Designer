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
package org.activiti.designer.validation.bpmn20.validation.worker;

import org.activiti.designer.validation.bpmn20.validation.worker.impl.ValidationCode;

/**
 * Container object for markers created by {@link ProcessValidationWorker}s.
 * 
 * @author Tiese Barrell
 * @since 5.6
 * @version 1
 */
public class ProcessValidationWorkerMarker {

  private int severity;
  private String message;
  private String nodeId;
  private ValidationCode code;

  public ProcessValidationWorkerMarker(final int severity, final String message, final String nodeId, final ValidationCode code) {
    super();
    this.severity = severity;
    this.message = message;
    this.nodeId = nodeId;
    this.code = code;
  }

  public int getSeverity() {
    return severity;
  }

  public String getMessage() {
    return message;
  }

  public String getNodeId() {
    return nodeId;
  }

  public ValidationCode getCode() {
    return code;
  }

}
