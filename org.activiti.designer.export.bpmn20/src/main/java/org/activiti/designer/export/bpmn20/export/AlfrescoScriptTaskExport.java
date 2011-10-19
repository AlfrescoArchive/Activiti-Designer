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

import org.eclipse.bpmn2.AlfrescoScriptTask;
import org.eclipse.emf.ecore.EObject;


/**
 * @author Tijs Rademakers
 */
public class AlfrescoScriptTaskExport implements ActivitiNamespaceConstants {

  public static void createScriptTask(EObject object, XMLStreamWriter xtw) throws Exception {
    AlfrescoScriptTask scriptTask = (AlfrescoScriptTask) object;
    // start AlfrescoScriptTask element
    xtw.writeStartElement("serviceTask");
    xtw.writeAttribute("id", scriptTask.getId());
    if (scriptTask.getName() != null) {
      xtw.writeAttribute("name", scriptTask.getName());
    }
    xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE,
            "class", "org.alfresco.repo.workflow.activiti.script.AlfrescoScriptDelegate");
    DefaultFlowExport.createDefaultFlow(object, xtw);
    AsyncActivityExport.createDefaultFlow(object, xtw);
    
    xtw.writeStartElement("extensionElements");
    
    ExtensionListenerExport.createExtensionListenerXML(scriptTask.getActivitiListeners(), false, EXECUTION_LISTENER, xtw);
    
    xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
    xtw.writeAttribute("name", "script");
    xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "string", ACTIVITI_EXTENSIONS_NAMESPACE);
    xtw.writeCharacters(scriptTask.getScript());
    xtw.writeEndElement();
    xtw.writeEndElement();
    
    if(scriptTask.getRunAs() != null && scriptTask.getRunAs().length() > 0) {
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeAttribute("name", "runAs");
      xtw.writeAttribute("stringValue", scriptTask.getRunAs());
      xtw.writeEndElement();
    }
    
    if(scriptTask.getScriptProcessor() != null && scriptTask.getScriptProcessor().length() > 0) {
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeAttribute("name", "scriptProcessor");
      xtw.writeAttribute("stringValue", scriptTask.getScriptProcessor());
      xtw.writeEndElement();
    }
    
    xtw.writeEndElement();
    
    MultiInstanceExport.createMultiInstance(object, xtw);

    // end AlfrescoScriptTask element
    xtw.writeEndElement();
  }
}
