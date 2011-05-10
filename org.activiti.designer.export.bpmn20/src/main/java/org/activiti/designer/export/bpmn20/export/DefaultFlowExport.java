/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.export.bpmn20.export;

import javax.xml.stream.XMLStreamWriter;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.emf.ecore.EObject;


/**
 * @author Tijs Rademakers
 */
public class DefaultFlowExport {

  public static void createDefaultFlow(EObject object, String subProcessId, XMLStreamWriter xtw) throws Exception {
    SequenceFlow defaultFlow = null;
    if(object instanceof Activity) {
      defaultFlow = ((Activity) object).getDefault();
    } else {
      defaultFlow = ((ExclusiveGateway) object).getDefault();
    }
    if(defaultFlow != null) {
      xtw.writeAttribute("default", subProcessId + defaultFlow.getId());
    }
  }
}
