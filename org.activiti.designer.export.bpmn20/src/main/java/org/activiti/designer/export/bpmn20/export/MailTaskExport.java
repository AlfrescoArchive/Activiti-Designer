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

import org.eclipse.bpmn2.MailTask;
import org.eclipse.emf.ecore.EObject;


/**
 * @author Tijs Rademakers
 */
public class MailTaskExport implements ActivitiNamespaceConstants {

  public static void createMailTask(EObject object, String subProcessId, XMLStreamWriter xtw) throws Exception {
    MailTask mailTask = (MailTask) object;
    // start MailTask element
    xtw.writeStartElement("serviceTask");
    xtw.writeAttribute("id", subProcessId + mailTask.getId());
    if (mailTask.getName() != null) {
      xtw.writeAttribute("name", mailTask.getName());
    }
    DefaultFlowExport.createDefaultFlow(object, subProcessId, xtw);
    xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "type", "mail");

    xtw.writeStartElement("extensionElements");
    ExtensionListenerExport.createExtensionListenerXML(mailTask.getActivitiListeners(), false, EXECUTION_LISTENER, xtw);

    if (mailTask.getTo() != null && mailTask.getTo().length() > 0) {
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeAttribute("name", "to");
      xtw.writeAttribute("expression", mailTask.getTo());
      xtw.writeEndElement();
    }
    if (mailTask.getFrom() != null && mailTask.getFrom().length() > 0) {
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeAttribute("name", "from");
      xtw.writeAttribute("expression", mailTask.getFrom());
      xtw.writeEndElement();
    }
    if (mailTask.getSubject() != null && mailTask.getSubject().length() > 0) {
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeAttribute("name", "subject");
      xtw.writeAttribute("expression", mailTask.getSubject());
      xtw.writeEndElement();
    }
    if (mailTask.getCc() != null && mailTask.getCc().length() > 0) {
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeAttribute("name", "cc");
      xtw.writeAttribute("expression", mailTask.getCc());
      xtw.writeEndElement();
    }
    if (mailTask.getBcc() != null && mailTask.getBcc().length() > 0) {
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeAttribute("name", "bcc");
      xtw.writeAttribute("expression", mailTask.getBcc());
      xtw.writeEndElement();
    }
    if (mailTask.getHtml() != null && mailTask.getHtml().length() > 0) {
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeAttribute("name", "html");
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "expression", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeCData(mailTask.getHtml());
      xtw.writeEndElement();
      xtw.writeEndElement();
    }
    if (mailTask.getText() != null && mailTask.getText().length() > 0) {
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeAttribute("name", "text");
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "expression", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeCData(mailTask.getText());
      xtw.writeEndElement();
      xtw.writeEndElement();
    }
    xtw.writeEndElement();
    
    MultiInstanceExport.createMultiInstance(object, subProcessId, xtw);

    // end MailTask element
    xtw.writeEndElement();
  }
}
