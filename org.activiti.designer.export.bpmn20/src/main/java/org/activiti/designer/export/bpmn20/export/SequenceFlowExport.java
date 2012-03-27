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

import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.apache.commons.lang.StringUtils;


/**
 * @author Tijs Rademakers
 */
public class SequenceFlowExport implements ActivitiNamespaceConstants {

  public static void createSequenceFlow(Object object, XMLStreamWriter xtw) throws Exception {
    SequenceFlow sequenceFlow = (SequenceFlow) object;
    // start SequenceFlow element
    xtw.writeStartElement("sequenceFlow");
    xtw.writeAttribute("id", sequenceFlow.getId());
    if (sequenceFlow.getName() == null) {
      xtw.writeAttribute("name", "");
    } else {
      xtw.writeAttribute("name", sequenceFlow.getName());
    }
    xtw.writeAttribute("sourceRef", sequenceFlow.getSourceRef().getId());
    xtw.writeAttribute("targetRef", sequenceFlow.getTargetRef().getId());

    ExecutionListenerExport.createExecutionListenerXML(sequenceFlow.getExecutionListeners(), true, xtw);

    if (StringUtils.isNotEmpty(sequenceFlow.getConditionExpression())) {

      String condition = sequenceFlow.getConditionExpression();
      // start conditionExpression element
      xtw.writeStartElement("conditionExpression");
      xtw.writeAttribute("xsi", XSI_NAMESPACE, "type", "tFormalExpression");
      xtw.writeCData(condition);

      // end conditionExpression element
      xtw.writeEndElement();
    }

    // end SequenceFlow element
    xtw.writeEndElement();
  }
}
