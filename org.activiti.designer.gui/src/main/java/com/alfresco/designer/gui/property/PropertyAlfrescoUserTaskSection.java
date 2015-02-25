package com.alfresco.designer.gui.property;

import java.util.List;

import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.property.ActivitiPropertySection;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyAlfrescoUserTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Combo performerTypeCombo;
  private String[] performerTypes = new String[] {"Assignee", "Candidate users", "Candidate groups"};
  private String currentType = "Assignee";
  private Text expressionText;
  private Combo formTypeCombo;
  private Text priorityText;
  private Text documentationText;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    performerTypeCombo = createCombobox(performerTypes, 0);
    createLabel("Performer type", performerTypeCombo);
    
    expressionText = createTextControl(false);
    createLabel("Expression", expressionText);

    List<String> userTaskFormTypes = PreferencesUtil.getStringArray(Preferences.ALFRESCO_FORMTYPES_USERTASK, ActivitiPlugin.getDefault());
    formTypeCombo = createCombobox(userTaskFormTypes.toArray(new String[userTaskFormTypes.size()]), 0);
    createLabel("Form key", formTypeCombo);
    
    priorityText = createTextControl(false);
    createLabel("Priority", priorityText);

    documentationText = createTextControl(true);
    createLabel("Documentation", documentationText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    UserTask userTask = (UserTask) businessObject;
    if (control == performerTypeCombo) {
      if (userTask.getCandidateUsers().size() > 0) {
        return "Candidate users";
      } else if (userTask.getCandidateGroups().size() > 0) {
        return "Candidate groups";
      } else {
        return "Assignee";
      }
    
    } else if (control == expressionText) {
      if (userTask.getCandidateUsers().size() > 0) {
        return getCommaSeperatedString(userTask.getCandidateUsers());
      } else if (userTask.getCandidateGroups().size() > 0) {
        return getCommaSeperatedString(userTask.getCandidateGroups());
      } else {
        return userTask.getAssignee();
      }
    
    } else if (control == formTypeCombo) {
      return userTask.getFormKey();
    
    } else if (control == priorityText) {
      return userTask.getPriority();
    
    } else if (control == documentationText) {
      return userTask.getDocumentation();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    UserTask userTask = (UserTask) businessObject;
    if (control == performerTypeCombo) {
      if (performerTypeCombo.getSelectionIndex() == 0) {
        userTask.setAssignee(expressionText.getText());
        removeCandidateUsers(userTask);
        removeCandidateGroups(userTask);
      
      } else if (performerTypeCombo.getSelectionIndex() == 1) {
        userTask.setAssignee(null);
        userTask.setCandidateUsers(commaSeperatedStringToList(expressionText.getText()));
        removeCandidateGroups(userTask);
      
      } else {
        userTask.setAssignee(null);
        removeCandidateUsers(userTask);
        userTask.setCandidateGroups(commaSeperatedStringToList(expressionText.getText()));
      }
    
    } else if (control == expressionText) {
      if (performerTypeCombo.getSelectionIndex() == 0) {
        userTask.setAssignee(expressionText.getText());
        removeCandidateUsers(userTask);
        removeCandidateGroups(userTask);
      
      } else if (performerTypeCombo.getSelectionIndex() == 1) {
        userTask.setAssignee(null);
        userTask.setCandidateUsers(commaSeperatedStringToList(expressionText.getText()));
        removeCandidateGroups(userTask);
      
      } else {
        userTask.setAssignee(null);
        removeCandidateUsers(userTask);
        userTask.setCandidateGroups(commaSeperatedStringToList(expressionText.getText()));
      }
    
    } else if (control == formTypeCombo) {
      userTask.setFormKey(formTypeCombo.getText());
    
    } else if (control == priorityText) {
      userTask.setPriority(priorityText.getText());
    
    } else if (control == documentationText) {
      userTask.setDocumentation(documentationText.getText());
    }
  }
  
  private void removeCandidateUsers(UserTask userTask) {
    if (userTask.getCandidateUsers() == null)
      return;
    userTask.getCandidateUsers().clear();
  }

  private void removeCandidateGroups(UserTask userTask) {
    if (userTask.getCandidateGroups() == null)
      return;
    userTask.getCandidateGroups().clear();
  }
}
