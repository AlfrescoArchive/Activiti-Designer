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
package org.activiti.designer.eclipse.navigator.cloudrepo;

import com.fasterxml.jackson.databind.JsonNode;


public class ActivitiCloudEditorException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
  protected JsonNode exceptionNode;
  
  public ActivitiCloudEditorException(JsonNode exceptionNode) {
    this.exceptionNode = exceptionNode;
  }
  
  public ActivitiCloudEditorException(String exceptionMessage) {
    super(exceptionMessage);
  }

  public JsonNode getExceptionNode() {
    return exceptionNode;
  }
}
