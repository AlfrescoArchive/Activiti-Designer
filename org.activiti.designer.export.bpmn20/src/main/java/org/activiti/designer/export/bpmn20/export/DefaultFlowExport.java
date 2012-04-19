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

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.Gateway;


/**
 * @author Tijs Rademakers
 */
public class DefaultFlowExport {

  public static void createDefaultFlow(FlowElement object, XMLStreamWriter xtw) throws Exception {
    String defaultFlow = null;
    if(object instanceof Activity) {
      defaultFlow = ((Activity) object).getDefaultFlow();
    } else if(object instanceof Gateway) {
      defaultFlow = ((Gateway) object).getDefaultFlow();
    } else {
        throw new Exception("Invalid element for default flow.  Expected 'Activity', " +
        		"'ExclusiveGateway', 'InclusiveGateway', but got: '" + object.getClass());
    }
    if(defaultFlow != null) {
      xtw.writeAttribute("default", defaultFlow);
    }
  }
}
