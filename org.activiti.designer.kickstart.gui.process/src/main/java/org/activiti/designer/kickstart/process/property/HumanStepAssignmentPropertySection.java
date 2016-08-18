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
package org.activiti.designer.kickstart.process.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.activiti.designer.kickstart.util.widget.ToggleContainerViewer;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.HumanStepAssignment.HumanStepAssignmentType;
import org.activiti.workflow.simple.definition.HumanStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section containing all properties in the main {@link StepDefinition}.
 * 
 * @author Frederik Heremans
 */
public class HumanStepAssignmentPropertySection extends AbstractKickstartProcessPropertySection {

  protected Button initiatorOption;
  protected Button userOption;
  protected Button groupOption;
  protected Button usersOption;
  protected Composite assigneeOptions;
  protected Label separator;
  
  protected ToggleContainerViewer assignmentDetailsViewer;
  protected StringItemSelectionViewer selectedUsersViewer;
  protected StringItemSelectionViewer selectedUserViewer;
  protected StringItemSelectionViewer selectedGroupsViewer;
  
  protected PropertyItemBrowser userBrowser;
  protected PropertyItemBrowser groupBrowser;
  protected PropertyItemBrowser usersBrowser;
  
  protected boolean clearingSelection = false;
  
  @Override
  protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    assigneeOptions = getWidgetFactory().createFlatFormComposite(formComposite);
    assigneeOptions.setLayout(new RowLayout(SWT.VERTICAL));
    
    initiatorOption = new Button(assigneeOptions, SWT.RADIO);
    initiatorOption.setText("Assign to workflow initiator");
    
    userOption = new Button(assigneeOptions, SWT.RADIO);
    userOption.setText("Assign to single user");
    
    usersOption = new Button(assigneeOptions, SWT.RADIO);
    usersOption.setText("Candidate user(s)");
    
    groupOption = new Button(assigneeOptions, SWT.RADIO);
    groupOption.setText("Candidate group(s)");
    
    registerControl(assigneeOptions);
    addOptionListeners();
    
    separator = createSeparator();
    
    assignmentDetailsViewer = new ToggleContainerViewer(formComposite);
    FormData formData = new FormData();
    assignmentDetailsViewer.getComposite().setLayoutData(formData);
    formData.left = new FormAttachment(0, 0);
    formData.right = new FormAttachment(100, 0);
    formData.top = createTopFormAttachment();
    
    userBrowser = new PropertyItemBrowser();
    userBrowser.setItemfilter(new UserPropertyItemFilter());
    
    usersBrowser = new PropertyItemBrowser();
    usersBrowser.setItemfilter(new UserPropertyItemFilter());
    
    groupBrowser = new PropertyItemBrowser();
    groupBrowser.setItemfilter(new GroupPropertyItemFilter());
    
    addUserDetails();
    addUsersDetails();
    addGroupsDetails();
  }
  
  protected void addGroupsDetails() {
    selectedGroupsViewer = new StringItemSelectionViewer(assignmentDetailsViewer.getComposite(), true, new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        HumanStepDefinition step = (HumanStepDefinition) getModelUpdater().getUpdatableBusinessObject();
        step.setCandidateGroups(selectedGroupsViewer.getItems());
        executeModelUpdater();
      }
    }, groupBrowser);
    selectedGroupsViewer.setAddItemLabel("Add group");
    selectedGroupsViewer.setRemoveItemLabel("Remove group");
    selectedGroupsViewer.setDefaultItemValue("group");
    selectedGroupsViewer.getComposite().setVisible(false);
    
    assignmentDetailsViewer.addControl("groups", selectedGroupsViewer.getComposite());
  }

  protected void addUsersDetails() {
    selectedUsersViewer = new StringItemSelectionViewer(assignmentDetailsViewer.getComposite(), true, new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        HumanStepDefinition step = (HumanStepDefinition) getModelUpdater().getUpdatableBusinessObject();
        step.setCandidateUsers(selectedUsersViewer.getItems());
        executeModelUpdater();
      }
    }, usersBrowser);
    selectedUsersViewer.setAddItemLabel("Add user");
    selectedUsersViewer.setRemoveItemLabel("Remove user");
    selectedUsersViewer.setDefaultItemValue("user");
    selectedUsersViewer.getComposite().setVisible(false);
    
    assignmentDetailsViewer.addControl("users", selectedUsersViewer.getComposite());
    
  }

  protected void addUserDetails() {
    selectedUserViewer = new StringItemSelectionViewer(assignmentDetailsViewer.getComposite(), false,new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        HumanStepDefinition step = (HumanStepDefinition) getModelUpdater().getUpdatableBusinessObject();
        step.setAssignee(selectedUserViewer.getItem());
        executeModelUpdater();
      }
    }, userBrowser);
    selectedUserViewer.getComposite().setVisible(false);
    
    assignmentDetailsViewer.addControl("user", selectedUserViewer.getComposite());
    
    Label initiatorDetailsLabel = new Label(assignmentDetailsViewer.getComposite(), SWT.NONE);
    initiatorDetailsLabel.setText("The user who starts the workflow will be the assignee of this task.");
    assignmentDetailsViewer.addControl("initiator", initiatorDetailsLabel);
  }

  protected void addOptionListeners() {
    SelectionListener switchTypeSelectionListener = new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if(!clearingSelection) {
          HumanStepAssignmentType type = null;
          if(e.widget == initiatorOption && initiatorOption.getSelection()) {
            type = HumanStepAssignmentType.INITIATOR;
          } else if(e.widget == userOption && userOption.getSelection()) {
            type = HumanStepAssignmentType.USER;
          } else if(e.widget == groupOption && groupOption.getSelection()) {
            type = HumanStepAssignmentType.GROUPS;
          } else if(e.widget == usersOption && usersOption.getSelection()) {
            type = HumanStepAssignmentType.USERS;
          }
          
          if(type != null) {
            HumanStepDefinition updatableBusinessObject = (HumanStepDefinition) getModelUpdater().getUpdatableBusinessObject();
            if(type != updatableBusinessObject.getAssignmentType()) {
              // Only update in case the selection actually changed
              updatableBusinessObject.getAssignment().setType(type);
              executeModelUpdater();
            }
          }
        }
      }
    };
    initiatorOption.addSelectionListener(switchTypeSelectionListener);
    userOption.addSelectionListener(switchTypeSelectionListener);
    groupOption.addSelectionListener(switchTypeSelectionListener);
    usersOption.addSelectionListener(switchTypeSelectionListener);
  }

  @Override
  protected void populateControl(Control control, Object businessObject) {
    if(control == assigneeOptions) {
      clearRadioButtons();
      
      // Populate selection box based on assignment type
      HumanStepDefinition definition =  ((HumanStepDefinition) businessObject);
      switch (definition.getAssignmentType()) {
      case GROUPS:
        groupOption.setSelection(true);
        selectedGroupsViewer.setItems(definition.getAssignment().getCandidateGroups());
        assignmentDetailsViewer.showChild("groups");
        break;
      case USER:
        userOption.setSelection(true);
        selectedUserViewer.setItem(definition.getAssignment().getAssignee());
        assignmentDetailsViewer.showChild("user");
        break;
      case USERS:
        usersOption.setSelection(true);
        selectedUsersViewer.setItems(definition.getAssignment().getCandidateUsers());
        assignmentDetailsViewer.showChild("users");
        break;
      default:
        initiatorOption.setSelection(true);
        assignmentDetailsViewer.showChild("initiator");
        break;
      }
    } else {
      super.populateControl(control, businessObject);
    }
  }
  
  protected void clearRadioButtons() {
    clearingSelection = true;
    try {
      userOption.setSelection(false);
      usersOption.setSelection(false);
      initiatorOption.setSelection(false);
      groupOption.setSelection(false);
    } finally {
      clearingSelection = false;
    }
  }
  
  @Override
  public void refresh() {
    super.refresh();
    if(getSelectedPictogramElement() != null && getDiagram() != null && userBrowser != null && groupBrowser != null) {
      KickstartProcessMemoryModel model = ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram()));
      if(model != null && model.getModelFile() != null) {
        userBrowser.setWorkflowDefinition(model.getWorkflowDefinition());
        groupBrowser.setWorkflowDefinition(model.getWorkflowDefinition());
        usersBrowser.setWorkflowDefinition(model.getWorkflowDefinition());
        
        userBrowser.setProject(model.getModelFile().getProject());
        groupBrowser.setProject(model.getModelFile().getProject());
        usersBrowser.setProject(model.getModelFile().getProject());
      }
    }
  }
  
  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    return null;
  }

  
  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    // Not using standard control storage
  }
}
