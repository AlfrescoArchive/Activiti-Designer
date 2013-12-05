package org.activiti.designer.property;

import org.activiti.bpmn.model.UserTask;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyUserTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Text assigneeText;
  private Text candidateUsersText;
  private Text candidateGroupsText;
  private Text formKeyText;
  private Text dueDateText;
  private Text priorityText;
  
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
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    UserTask task = (UserTask) businessObject;
    if (control == assigneeText) {
      return task.getAssignee();
    } else if(control == candidateUsersText) {
      return getCommaSeperatedString(task.getCandidateUsers());
    } else if(control == candidateGroupsText) {
      return getCommaSeperatedString(task.getCandidateGroups());
    } else if(control == formKeyText) {
      return task.getFormKey();
    } else if(control == dueDateText) {
      return task.getDueDate();
    } else if(control == priorityText) {
      return task.getPriority();
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
