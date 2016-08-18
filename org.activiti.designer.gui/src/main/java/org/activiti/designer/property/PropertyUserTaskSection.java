/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.property;

import org.activiti.bpmn.model.UserTask;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyUserTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  protected Text assigneeText;
  protected Text candidateUsersText;
  protected Text candidateGroupsText;
  protected Text formKeyText;
  protected Text dueDateText;
  protected Text priorityText;
  protected Text categoryText;
  protected Text skipExpressionText;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    assigneeText = createTextControl(false);
    createLabel("Assignee", assigneeText);
    candidateUsersText = createTextControl(false);
    createLabel("Candidate users (comma separated)", candidateUsersText);
    candidateGroupsText = createTextControl(false);
    createLabel("Candidate groups (comma separated)", candidateGroupsText);
    formKeyText = createTextControl(false);
    createLabel("Form key", formKeyText);
    dueDateText = createTextControl(false);
    createLabel("Due date (variable)", dueDateText);
    priorityText = createTextControl(false);
    createLabel("Priority", priorityText);
    categoryText = createTextControl(false);
    createLabel("Category", categoryText);
    skipExpressionText = createTextControl(false);
    createLabel("Skip expression", skipExpressionText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    UserTask task = (UserTask) businessObject;
    if (control == assigneeText) {
      return task.getAssignee();
    } else if (control == candidateUsersText) {
      return getCommaSeperatedString(task.getCandidateUsers());
    } else if (control == candidateGroupsText) {
      return getCommaSeperatedString(task.getCandidateGroups());
    } else if (control == formKeyText) {
      return task.getFormKey();
    } else if (control == dueDateText) {
      return task.getDueDate();
    } else if (control == priorityText) {
      return task.getPriority();
    } else if (control == categoryText) {
      return task.getCategory();
    } else if (control == skipExpressionText) {
      return task.getSkipExpression();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    UserTask task = (UserTask) businessObject;
    if (control == assigneeText) {
      task.setAssignee(assigneeText.getText());
    } else if (control == candidateUsersText) {
      updateCandidates(task, candidateUsersText);
    } else if (control == candidateGroupsText) {
      updateCandidates(task, candidateGroupsText);
    } else if (control == formKeyText) {
      task.setFormKey(formKeyText.getText());
    } else if (control == dueDateText) {
      task.setDueDate(dueDateText.getText());
    } else if (control == priorityText) {
      task.setPriority(priorityText.getText());
    } else if (control == categoryText) {
      task.setCategory(categoryText.getText());
    } else if (control == skipExpressionText) {
      task.setSkipExpression(skipExpressionText.getText());
    }
  }
  
  protected void updateCandidates(UserTask userTask, Object source) {
    String candidates = ((Text) source).getText();
    
    if (source == candidateUsersText) {
      userTask.getCandidateUsers().clear();
    } else {
      userTask.getCandidateGroups().clear();
    }
    
    if (StringUtils.isNotEmpty(candidates)) {
      String[] expressionList = null;
      if (candidates.contains(",")) {
        expressionList = candidates.split(",");
      } else {
        expressionList = new String[] { candidates };
      }
      
      for (String user : expressionList) {
        if (source == candidateUsersText) {
          userTask.getCandidateUsers().add(user.trim());
        } else {
          userTask.getCandidateGroups().add(user.trim());
        }
      }
    }
  }
}
