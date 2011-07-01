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

/**
 * @author Tijs Rademakers
 */
public class ImplementationValueExport implements ActivitiNamespaceConstants {

  public static void writeImplementationValue(XMLStreamWriter xtw, String listenerType, String implementationType, 
          String implementation, boolean namespace) throws Exception {
    if (implementationType == null || implementationType.length() == 0 || CLASS_TYPE.equals(implementationType)) {
      writeImplementationValueAndType(xtw, "class", implementation, namespace);
    } else if (implementationType.equals(DELEGATE_EXPRESSION_TYPE)) {
      writeImplementationValueAndType(xtw, "delegateExpression", implementation, namespace);
    } else if (implementationType.equals(ALFRESCO_TYPE)) {
      String className = null;
      if(EXECUTION_LISTENER.equals(listenerType)) {
        className = "org.alfresco.repo.workflow.activiti.listener.ScriptExecutionListener";
      } else {
        className = "org.alfresco.repo.workflow.activiti.tasklistener.ScriptTaskListener";
      }
      if(namespace) {
      	xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "class", className);
      } else {
      	xtw.writeAttribute("class", className);
      }
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeAttribute("name", "script");
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "string", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeCharacters(implementation);
      xtw.writeEndElement();
      xtw.writeEndElement();
      
    } else {
      writeImplementationValueAndType(xtw, "expression", implementation, namespace);
    }
  }
  
  private static void writeImplementationValueAndType(XMLStreamWriter xtw, String implementationType, String implementation, boolean namespace) throws Exception {
    if (implementation != null && implementation.length() > 0) {
      if(namespace)
        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, implementationType, implementation);
      else
        xtw.writeAttribute(implementationType, implementation);
    }
  }
}
