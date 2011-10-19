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

import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.emf.ecore.EObject;


/**
 * @author Tijs Rademakers
 */
public class ScriptTaskExport implements ActivitiNamespaceConstants {

  public static void createScriptTask(EObject object, XMLStreamWriter xtw) throws Exception {
    ScriptTask scriptTask = (ScriptTask) object;
    // start ScriptTask element
    xtw.writeStartElement("scriptTask");
    xtw.writeAttribute("id", scriptTask.getId());
    if (scriptTask.getName() != null) {
      xtw.writeAttribute("name", scriptTask.getName());
    }
    DefaultFlowExport.createDefaultFlow(object, xtw);
    AsyncActivityExport.createDefaultFlow(object, xtw);
    xtw.writeAttribute("scriptFormat", scriptTask.getScriptFormat());

    ExtensionListenerExport.createExtensionListenerXML(scriptTask.getActivitiListeners(), true, EXECUTION_LISTENER, xtw);

    xtw.writeStartElement("script");
    xtw.writeCData(scriptTask.getScript());
    xtw.writeEndElement();
    
    MultiInstanceExport.createMultiInstance(object, xtw);

    // end ScriptTask element
    xtw.writeEndElement();
  }
}
