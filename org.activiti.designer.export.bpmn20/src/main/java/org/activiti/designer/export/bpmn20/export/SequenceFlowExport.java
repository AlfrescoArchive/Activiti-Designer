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

import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.emf.ecore.EObject;


/**
 * @author Tijs Rademakers
 */
public class SequenceFlowExport implements ActivitiNamespaceConstants {

  public static void createSequenceFlow(EObject object, XMLStreamWriter xtw) throws Exception {
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

    ExtensionListenerExport.createExtensionListenerXML(sequenceFlow.getExecutionListeners(), true, EXECUTION_LISTENER, xtw);

    if (sequenceFlow.getConditionExpression() != null && sequenceFlow.getConditionExpression().getBody() != null
            && sequenceFlow.getConditionExpression().getBody().length() > 0) {

      String condition = sequenceFlow.getConditionExpression().getBody();
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
