package org.activiti.designer.kickstart.process.property;

import org.activiti.workflow.simple.definition.HumanStepAssignment.HumanStepAssignmentType;
import org.activiti.workflow.simple.definition.HumanStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
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
  protected Label assignmentTypeLabel;
  protected StringItemSelectionViewer selectedUsersViewer;
  
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
    
    createSeparator();
    
    assignmentTypeLabel = createFullWidthLabel("");
    
    selectedUsersViewer = new StringItemSelectionViewer(formComposite, true, new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        // TODO: implement
      }
    }, new StringItemBrowser() {
      
      @Override
      public Control getBrowserControl(StringItemSelectionViewer viewer, Composite parent) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText("Use form property...");
        return button;
      }
    });
    selectedUsersViewer.setAddItemLabel("Add user");
    selectedUsersViewer.setRemoveItemLabel("Remove user");
    selectedUsersViewer.setDefaultItemValue("user");
    selectedUsersViewer.getComposite().setVisible(false);
    
    FormData formData = new FormData();
    formData.left = new FormAttachment(0, 0);
    formData.right = new FormAttachment(100, 0);
    formData.top = createTopFormAttachment();
    selectedUsersViewer.getComposite().setLayoutData(formData);
    
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
      
      // TODO: remove
      selectedUsersViewer.getComposite().setVisible(false);
      
      // Populate selection box based on assignment type
      HumanStepAssignmentType type = ((HumanStepDefinition) businessObject).getAssignmentType();
      switch (type) {
      case GROUPS:
        groupOption.setSelection(true);
        assignmentTypeLabel.setText(groupOption.getText());
        break;
      case USER:
        userOption.setSelection(true);
        assignmentTypeLabel.setText(userOption.getText());
        break;
      case USERS:
        usersOption.setSelection(true);
        assignmentTypeLabel.setText(usersOption.getText());
        selectedUsersViewer.getComposite().setVisible(true);
        break;
      default:
        initiatorOption.setSelection(true);
        assignmentTypeLabel.setText("");
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
  protected Object getModelValueForControl(Control control, Object businessObject) {
    return null;
  }

  
  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    // Not using standard control
  }
}
