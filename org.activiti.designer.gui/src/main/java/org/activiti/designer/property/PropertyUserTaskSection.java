package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyUserTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Text assigneeText;
  private Text candidateUsersText;
  private Text candidateGroupsText;
  private Text formKeyText;
  private Text dueDateText;
  private Text priorityText;
  private Text documentationText;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    Composite composite = factory.createFlatFormComposite(parent);
    FormData data;

    assigneeText = createText(composite, factory, null);
    createLabel("Assignee:", composite, factory, assigneeText);
    
    candidateUsersText = createText(composite, factory, assigneeText);
    createLabel("Candidate users (comma separated):", composite, factory, candidateUsersText);
    
    candidateGroupsText = createText(composite, factory, candidateUsersText);
    createLabel("Candidate groups (comma separated:", composite, factory, candidateGroupsText);

    formKeyText = createText(composite, factory, candidateGroupsText);
    createLabel("Form key:", composite, factory, formKeyText);
    
    dueDateText = createText(composite, factory, formKeyText);
    createLabel("Due date (variable):", composite, factory, dueDateText);
    
    priorityText = createText(composite, factory, dueDateText);
    createLabel("Priority:", composite, factory, priorityText);

    documentationText = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
    data = new FormData(SWT.DEFAULT, 100);
    data.left = new FormAttachment(0, 250);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(priorityText, VSPACE);
    documentationText.setLayoutData(data);
    documentationText.addFocusListener(listener);

    createLabel("Documentation:", composite, factory, documentationText);
  }

  @Override
  public void refresh() {
    assigneeText.removeFocusListener(listener);
    candidateUsersText.removeFocusListener(listener);
    candidateGroupsText.removeFocusListener(listener);
    formKeyText.removeFocusListener(listener);
    dueDateText.removeFocusListener(listener);
    priorityText.removeFocusListener(listener);
    documentationText.removeFocusListener(listener);
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = getBusinessObject(pe);
      if (bo == null)
        return;

      UserTask userTask = (UserTask) bo;
      
      assigneeText.setText("");
      if (StringUtils.isNotEmpty(userTask.getAssignee())) {
      	assigneeText.setText(userTask.getAssignee());
      }
      
      candidateUsersText.setText("");
      if (userTask.getCandidateUsers().size() > 0) {
        StringBuffer expressionBuffer = new StringBuffer();
        for (String user : userTask.getCandidateUsers()) {
          if (expressionBuffer.length() > 0) {
            expressionBuffer.append(",");
          }
          expressionBuffer.append(user.trim());
        }
        candidateUsersText.setText(expressionBuffer.toString());
      } 
      
      candidateGroupsText.setText("");
      if (userTask.getCandidateGroups().size() > 0) {
        StringBuffer expressionBuffer = new StringBuffer();
        for (String group : userTask.getCandidateGroups()) {
          if (expressionBuffer.length() > 0) {
            expressionBuffer.append(",");
          }
          expressionBuffer.append(group.trim());
        }
        candidateGroupsText.setText(expressionBuffer.toString());
      }
      
      formKeyText.setText("");
      if(formKeyText != null) {
        if (StringUtils.isNotEmpty(userTask.getFormKey())) {
          formKeyText.setText(userTask.getFormKey());
        }
      }
      
      dueDateText.setText("");
      if(StringUtils.isNotEmpty(userTask.getDueDate())) {
      	dueDateText.setText(userTask.getDueDate().toString());
      }
      
      priorityText.setText("");
      if(userTask.getPriority() != null) {
        priorityText.setText(userTask.getPriority().toString());
      }

      documentationText.setText("");
      if (StringUtils.isNotEmpty(userTask.getDocumentation())) {
        documentationText.setText(userTask.getDocumentation());
      }

      assigneeText.addFocusListener(listener);
      candidateUsersText.addFocusListener(listener);
      candidateGroupsText.addFocusListener(listener);
      formKeyText.addFocusListener(listener);
      dueDateText.addFocusListener(listener);
      priorityText.addFocusListener(listener);
      documentationText.addFocusListener(listener);
    }
  }

  private FocusListener listener = new FocusListener() {

    public void focusGained(final FocusEvent e) {
    }

    public void focusLost(final FocusEvent e) {
      
      PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        final Object bo = getBusinessObject(pe);
        if (bo instanceof UserTask) {
          DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
          TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
          ActivitiUiUtil.runModelChange(new Runnable() {

            public void run() {
              UserTask userTask = (UserTask) bo;
              
              String assignee = assigneeText.getText();
              userTask.setAssignee(assignee);
              
              userTask.getCandidateUsers().clear();
              if (StringUtils.isNotEmpty(candidateUsersText.getText())) {
                String[] expressionList = null;
                if (candidateUsersText.getText().contains(",")) {
                  expressionList = candidateUsersText.getText().split(",");
                } else {
                  expressionList = new String[] { candidateUsersText.getText() };
                }
                
                for (String user : expressionList) {
                  userTask.getCandidateUsers().add(user.trim());
                }
              }
              
              userTask.getCandidateGroups().clear();
              if (StringUtils.isNotEmpty(candidateGroupsText.getText())) {
                String[] expressionList = null;
                if (candidateGroupsText.getText().contains(",")) {
                  expressionList = candidateGroupsText.getText().split(",");
                } else {
                  expressionList = new String[] { candidateGroupsText.getText() };
                }
                
                for (String group : expressionList) {
                  userTask.getCandidateGroups().add(group.trim());
                }
              }
              
              String formKey = formKeyText.getText();
              if (formKey != null) {
                userTask.setFormKey(formKey);
              } else {
                userTask.setFormKey("");
              }
              
              String dueDate = dueDateText.getText();
              if (StringUtils.isNotEmpty(dueDate)) {
                userTask.setDueDate(dueDate);
                
              } else {
                userTask.setDueDate(null);
              }
              
              if(StringUtils.isNotEmpty(priorityText.getText())) {
                Integer priorityValue = null;
                try {
                  priorityValue = Integer.valueOf(priorityText.getText());
                } catch(Exception e) {}
                userTask.setPriority(priorityValue);
              }

              String documentation = documentationText.getText();
              if (documentation != null) {
                userTask.setDocumentation(documentation);
              }
            }
          }, editingDomain, "Model Update");
        }

      }
    }
  };
  
  private Text createText(Composite parent, TabbedPropertySheetWidgetFactory factory, Control top) {
    Text text = factory.createText(parent, ""); //$NON-NLS-1$
    FormData data = new FormData();
    data.left = new FormAttachment(0, 250);
    data.right = new FormAttachment(100, -HSPACE);
    if(top == null) {
      data.top = new FormAttachment(0, VSPACE);
    } else {
      data.top = new FormAttachment(top, VSPACE);
    }
    text.setLayoutData(data);
    text.addFocusListener(listener);
    return text;
  }
  
  private CLabel createLabel(String text, Composite parent, TabbedPropertySheetWidgetFactory factory, Control control) {
    CLabel label = factory.createCLabel(parent, text);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(control, -HSPACE);
    data.top = new FormAttachment(control, 0, SWT.CENTER);
    label.setLayoutData(data);
    return label;
  }
}
