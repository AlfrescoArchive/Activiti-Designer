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

import org.activiti.designer.eclipse.extension.ExtensionConstants;
import org.eclipse.bpmn2.CustomProperty;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.emf.ecore.EObject;


/**
 * @author Tijs Rademakers
 */
public class ServiceTaskExport implements ActivitiNamespaceConstants {

  public static void createServiceTask(EObject object, String subProcessId, XMLStreamWriter xtw) throws Exception {
    ServiceTask serviceTask = (ServiceTask) object;
    // start ServiceTask element
    xtw.writeStartElement("serviceTask");
    xtw.writeAttribute("id", subProcessId + serviceTask.getId());
    if (serviceTask.getName() != null) {
      xtw.writeAttribute("name", serviceTask.getName());
    }

    ImplementationValueExport.writeImplementationValue(xtw, serviceTask.getImplementationType(), serviceTask.getImplementation(), true);

    if (serviceTask.getResultVariableName() != null && serviceTask.getResultVariableName().length() > 0) {
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "resultVariableName", serviceTask.getResultVariableName());
    }

    ExtensionListenerExport.createExtensionListenerXML(serviceTask.getActivitiListeners(), true, EXECUTION_LISTENER, xtw);
    FieldExtensionsExport.writeFieldExtensions(xtw, serviceTask.getFieldExtensions(), true);

    if (serviceTask.getCustomProperties() != null && serviceTask.getCustomProperties().size() > 0) {
      boolean firstCustomProperty = true;
      for (CustomProperty customProperty : serviceTask.getCustomProperties()) {
        if (ExtensionConstants.PROPERTY_ID_CUSTOM_SERVICE_TASK.equals(customProperty.getName())) {
          continue;
        }
        if (firstCustomProperty == true) {
          xtw.writeStartElement("extensionElements");
          firstCustomProperty = false;
        }
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeAttribute("name", customProperty.getName());
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "string", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeCharacters(customProperty.getSimpleValue());
        xtw.writeEndElement();
        xtw.writeEndElement();
      }
      if (firstCustomProperty == false) {
        xtw.writeEndElement();
      }
    }
    // end ServiceTask element
    xtw.writeEndElement();
  }
}
