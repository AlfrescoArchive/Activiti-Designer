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

import org.eclipse.bpmn2.ManualTask;
import org.eclipse.emf.ecore.EObject;


/**
 * @author Tijs Rademakers
 */
public class ManualTaskExport implements ActivitiNamespaceConstants {

  public static void createManualTask(EObject object, XMLStreamWriter xtw) throws Exception {
    ManualTask manualTask = (ManualTask) object;
    // start ManualTask element
    xtw.writeStartElement("manualTask");
    xtw.writeAttribute("id", manualTask.getId());
    if (manualTask.getName() != null) {
      xtw.writeAttribute("name", manualTask.getName());
    }
    DefaultFlowExport.createDefaultFlow(object, xtw);
    ExtensionListenerExport.createExtensionListenerXML(manualTask.getActivitiListeners(), true, EXECUTION_LISTENER, xtw);
    
    MultiInstanceExport.createMultiInstance(object, xtw);

    // end ManualTask element
    xtw.writeEndElement();
  }
}
