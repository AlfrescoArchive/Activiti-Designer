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

import org.eclipse.bpmn2.CandidateGroup;
import org.eclipse.bpmn2.CandidateUser;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.emf.ecore.EObject;


/**
 * @author Tijs Rademakers
 */
public class UserTaskExport implements ActivitiNamespaceConstants {

  public static void createUserTask(EObject object, XMLStreamWriter xtw) throws Exception {
    UserTask userTask = (UserTask) object;
    if ((userTask.getAssignee() != null && userTask.getAssignee().length() > 0)
            || (userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0)
            || (userTask.getCandidateGroups() != null && userTask.getCandidateGroups().size() > 0)) {

      // start UserTask element
      xtw.writeStartElement("userTask");
      xtw.writeAttribute("id", userTask.getId());
      if (userTask.getName() != null) {
        xtw.writeAttribute("name", userTask.getName());
      }
      
      if (userTask.getDueDate() != null && userTask.getDueDate().length() > 0) {
        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "dueDate", userTask.getDueDate());
      }
      
      DefaultFlowExport.createDefaultFlow(object, xtw);

      // TODO revisit once the designer supports mixing these
      // configurations as they are now exclusive
      if (userTask.getAssignee() != null && userTask.getAssignee().length() > 0) {
        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "assignee", userTask.getAssignee());
      } else if (userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0) {
        Iterator<CandidateUser> candidateUserIterator = userTask.getCandidateUsers().iterator();
        String candidateUsers = candidateUserIterator.next().getUser();
        while (candidateUserIterator.hasNext()) {
          candidateUsers += ", " + candidateUserIterator.next().getUser();
        }
        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "candidateUsers", candidateUsers);
      } else {
        Iterator<CandidateGroup> candidateGroupIterator = userTask.getCandidateGroups().iterator();
        String candidateGroups = candidateGroupIterator.next().getGroup();
        while (candidateGroupIterator.hasNext()) {
          candidateGroups += ", " + candidateGroupIterator.next().getGroup();
        }
        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "candidateGroups", candidateGroups);
      }

      if (userTask.getFormKey() != null && userTask.getFormKey().length() > 0) {
        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "formKey", userTask.getFormKey());
      }
      
      if (userTask.getPriority() != null) {
        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "priority", userTask.getPriority().toString());
      }
      
      if (userTask.getDocumentation() != null && userTask.getDocumentation().size() > 0) {

        final Documentation documentation = userTask.getDocumentation().get(0);

        if (documentation.getText() != null && !"".equals(documentation.getText())) {

          // start documentation element
          xtw.writeStartElement("documentation");

          if (documentation.getId() != null) {
            xtw.writeAttribute("id", documentation.getId());
          }
          xtw.writeCharacters(documentation.getText());

          // end documentation element
          xtw.writeEndElement();
        }

      }
      
      boolean extensionsElement = true;
      if(userTask.getFormProperties().size() > 0) {
        extensionsElement = false;
        xtw.writeStartElement("extensionElements");
      }
      
      FormPropertiesExport.createFormPropertiesXML(userTask.getFormProperties(), xtw);
      ExtensionListenerExport.createExtensionListenerXML(userTask.getActivitiListeners(), extensionsElement, TASK_LISTENER, xtw);
      
      if(extensionsElement == false) {
        xtw.writeEndElement();
      }
      
      MultiInstanceExport.createMultiInstance(object, xtw);

      // end UserTask element
      xtw.writeEndElement();
    }
  }
}
