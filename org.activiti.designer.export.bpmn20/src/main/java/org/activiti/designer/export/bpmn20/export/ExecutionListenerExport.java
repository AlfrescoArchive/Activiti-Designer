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

import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.bpmn2.model.ActivitiListener;

/**
 * @author Tijs Rademakers
 */
public class ExecutionListenerExport implements ActivitiNamespaceConstants {

  public static void createExecutionListenerXML(List<ActivitiListener> listenerList, boolean writeExtensionsElement, XMLStreamWriter xtw) throws Exception {

    if (listenerList == null || listenerList.size() == 0)
      return;

    if (writeExtensionsElement)
      xtw.writeStartElement("extensionElements");

    for (ActivitiListener listener : listenerList) {
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, EXECUTION_LISTENER, ACTIVITI_EXTENSIONS_NAMESPACE);
      if (listener.getEvent() != null) {
        xtw.writeAttribute("event", listener.getEvent());
      }
      ImplementationValueExport.writeImplementationValue(xtw, EXECUTION_LISTENER, listener.getImplementationType(), listener.getImplementation(), false);
      FieldExtensionsExport.writeFieldExtensions(xtw, listener.getFieldExtensions(), false);
      xtw.writeEndElement();
    }

    if (writeExtensionsElement)
      xtw.writeEndElement();
  }

}
