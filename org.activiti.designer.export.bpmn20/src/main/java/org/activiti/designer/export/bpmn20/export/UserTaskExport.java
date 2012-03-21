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

import java.util.Iterator;

import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.bpmn2.model.UserTask;

/**
 * @author Tijs Rademakers
 */
public class UserTaskExport implements ActivitiNamespaceConstants {

  public static void createUserTask(Object object, XMLStreamWriter xtw) throws Exception {
    UserTask userTask = (UserTask) object;

    // start UserTask element
    xtw.writeStartElement("userTask");
    xtw.writeAttribute("id", userTask.getId());
    if (userTask.getName() != null) {
      xtw.writeAttribute("name", userTask.getName());
    }
    
    if (userTask.getDueDate() != null) {
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "dueDate", userTask.getDueDate().toString());
    }
    
    DefaultFlowExport.createDefaultFlow(userTask, xtw);
    AsyncActivityExport.createAsyncAttribute(userTask, xtw);

    // TODO revisit once the designer supports mixing these
    // configurations as they are now exclusive
    if (userTask.getAssignee() != null && userTask.getAssignee().length() > 0) {
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "assignee", userTask.getAssignee());
    } else if (userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0) {
      Iterator<String> candidateUserIterator = userTask.getCandidateUsers().iterator();
      String candidateUsers = candidateUserIterator.next();
      while (candidateUserIterator.hasNext()) {
        candidateUsers += ", " + candidateUserIterator.next();
      }
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "candidateUsers", candidateUsers);
    } else if (userTask.getCandidateGroups() != null && userTask.getCandidateGroups().size() > 0) {
      Iterator<String> candidateGroupIterator = userTask.getCandidateGroups().iterator();
      String candidateGroups = candidateGroupIterator.next();
      while (candidateGroupIterator.hasNext()) {
        candidateGroups += ", " + candidateGroupIterator.next();
      }
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "candidateGroups", candidateGroups);
    }

    if (userTask.getFormKey() != null && userTask.getFormKey().length() > 0) {
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "formKey", userTask.getFormKey());
    }
    
    if (userTask.getPriority() != null) {
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "priority", userTask.getPriority().toString());
    }
    
    if (userTask.getDocumentation() != null && userTask.getDocumentation().length() > 0) {

      xtw.writeStartElement("documentation");
      xtw.writeCharacters(userTask.getDocumentation());
      // end documentation element
      xtw.writeEndElement();
    }
    
    boolean extensionsElement = true;
    if(userTask.getFormProperties().size() > 0) {
      extensionsElement = false;
      xtw.writeStartElement("extensionElements");
    }
    
    FormPropertiesExport.createFormPropertiesXML(userTask.getFormProperties(), xtw);
    ExecutionListenerExport.createExecutionListenerXML(userTask.getExecutionListeners(), extensionsElement, xtw);
    
    if(extensionsElement == false) {
      xtw.writeEndElement();
    }
    
    MultiInstanceExport.createMultiInstance(object, xtw);

    // end UserTask element
    xtw.writeEndElement();
  }
}
