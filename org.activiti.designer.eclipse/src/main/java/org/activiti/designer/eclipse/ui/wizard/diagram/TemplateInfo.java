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
package org.activiti.designer.eclipse.ui.wizard.diagram;


/**
 * @author Tijs Rademakers
 */
public class TemplateInfo {

  public static String[] templateFilenames = new String[] {"adhoc.bpmn20.xml",
    "parallel-review-group.bpmn20.xml",
    "parallel-review.bpmn20.xml",
    "review-pooled.bpmn20.xml",
    "review.bpmn20.xml"};
  
  public static String[] templateDescriptions = new String[] {"Adhoc Activiti Process",
    "Parallel Group Review And Approve Activiti Process",
    "Parallel Review And Approve Activiti Process",
    "Pooled Review And Approve Activiti Process",
    "Review And Approve Activiti Process"};
}
