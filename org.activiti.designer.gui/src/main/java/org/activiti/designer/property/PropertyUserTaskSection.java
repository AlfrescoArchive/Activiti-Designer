package org.activiti.designer.property;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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

import com.ibm.icu.text.SimpleDateFormat;

public class PropertyUserTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private CCombo performerTypeCombo;
  private List<String> performerTypes = Arrays.asList("Assignee", "Candidate users", "Candidate groups");
  private String currentType = "Assignee";
  private Text expressionText;
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

    performerTypeCombo = createCCombo(composite, (String[]) performerTypes.toArray(), factory, null);
    createLabel("Performer type:", composite, factory, performerTypeCombo);

    expressionText = createText(composite, factory, performerTypeCombo);
    createLabel("Expression:", composite, factory, expressionText);

    formKeyText = createText(composite, factory, expressionText);
    createLabel("Form key:", composite, factory, formKeyText);
    
    dueDateText = createText(composite, factory, formKeyText);
    createLabel("Due date (variable):", composite, factory, dueDateText);
    
    priorityText = createText(composite, factory, dueDateText);
    createLabel("Priority:", composite, factory, priorityText);

    documentationText = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
    data = new FormData(SWT.DEFAULT, 100);
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(priorityText, VSPACE);
    documentationText.setLayoutData(data);
    documentationText.addFocusListener(listener);

    createLabel("Documentation:", composite, factory, documentationText);
  }

  @Override
  public void refresh() {
    performerTypeCombo.removeFocusListener(listener);
    expressionText.removeFocusListener(listener);
    formKeyText.removeFocusListener(listener);
    dueDateText.removeFocusListener(listener);
    priorityText.removeFocusListener(listener);
    documentationText.removeFocusListener(listener);
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;

      expressionText.setText("");
      UserTask userTask = (UserTask) bo;
      int performerIndex = 0;
      if (userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0) {
        performerIndex = performerTypes.indexOf("Candidate users");
        StringBuffer expressionBuffer = new StringBuffer();
        for (String user : userTask.getCandidateUsers()) {
          if (expressionBuffer.length() > 0) {
            expressionBuffer.append(";");
          }
          expressionBuffer.append(user);
        }
        currentType = "Candidate users";
        expressionText.setText(expressionBuffer.toString());
      } else if (userTask.getCandidateGroups() != null && userTask.getCandidateGroups().size() > 0) {
        performerIndex = performerTypes.indexOf("Candidate groups");
        StringBuffer expressionBuffer = new StringBuffer();
        for (String group : userTask.getCandidateGroups()) {
          if (expressionBuffer.length() > 0) {
            expressionBuffer.append(";");
          }
          expressionBuffer.append(group);
        }
        currentType = "Candidate groups";
        expressionText.setText(expressionBuffer.toString());
      } else {
        performerIndex = performerTypes.indexOf("Assignee");
        if (userTask.getAssignee() != null && userTask.getAssignee().length() > 0) {
          currentType = "Assignee";
          expressionText.setText(userTask.getAssignee());
        }
      }
      if(formKeyText != null) {
        formKeyText.setText("");
        if (userTask.getFormKey() != null && userTask.getFormKey().length() > 0) {
          formKeyText.setText(userTask.getFormKey());
        }
      }
      
      dueDateText.setText("");
      if(userTask.getDueDate() != null) {
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

      performerTypeCombo.select(performerIndex == -1 ? 0 : performerIndex);
      performerTypeCombo.addFocusListener(listener);
      expressionText.addFocusListener(listener);
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
      final String performerType = performerTypeCombo.getText();
      if (e.getSource() instanceof CCombo && !currentType.equalsIgnoreCase(performerType)) {
        expressionText.setText("");
      }
      currentType = performerType;
      PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
        if (bo instanceof UserTask) {
          DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
          TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
          ActivitiUiUtil.runModelChange(new Runnable() {

            public void run() {
              Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
              if (bo == null) {
                return;
              }
              org.activiti.designer.bpmn2.model.UserTask userTask = (UserTask) bo;
              String expression = expressionText.getText();
              if (performerType != null && expression != null && expression.length() > 0) {
                if ("assignee".equalsIgnoreCase(performerType)) {
                  userTask.setAssignee(expression);
                  removeCandidateUsers(userTask);
                  removeCandidateGroups(userTask);
                } else if ("candidate users".equalsIgnoreCase(performerType)) {
                  String[] expressionList = null;
                  if (expression.contains(";")) {
                    expressionList = expression.split(";");
                  } else {
                    expressionList = new String[] { expression };
                  }
                  for (String user : expressionList) {
                    if (!candidateUserExists(userTask, user)) {
                      userTask.getCandidateUsers().add(user);
                    }
                  }
                  removeCandidateUsersNotInList(expressionList, userTask);
                  userTask.setAssignee(null);
                  removeCandidateGroups(userTask);
                } else {
                  String[] expressionList = null;
                  if (expression.contains(";")) {
                    expressionList = expression.split(";");
                  } else {
                    expressionList = new String[] { expression };
                  }
                  for (String group : expressionList) {
                    if (!candidateGroupExists(userTask, group)) {
                      userTask.getCandidateGroups().add(group);
                    }
                  }
                  removeCandidateGroupsNotInList(expressionList, userTask);
                  userTask.setAssignee(null);
                  removeCandidateUsers(userTask);
                }
              }
              String formKey = formKeyText.getText();
              if (formKey != null) {
                userTask.setFormKey(formKey);
              } else {
                userTask.setFormKey("");
              }
              
              String dueDate = dueDateText.getText();
              if (dueDate != null) {
                try {
	                userTask.setDueDate(new SimpleDateFormat().parse(dueDate));
                } catch (ParseException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                }
              } else {
                userTask.setDueDate(null);
              }
              
              if(priorityText.getText() != null && priorityText.getText().length() > 0) {
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

    private void removeCandidateUsers(UserTask userTask) {
      if (userTask.getCandidateUsers() == null)
        return;
      userTask.getCandidateUsers().clear();
    }

    private void removeCandidateUsersNotInList(String[] expressionList, UserTask userTask) {
      Iterator<String> entryIterator = userTask.getCandidateUsers().iterator();
      while (entryIterator.hasNext()) {
        String candidateUser = entryIterator.next();
        boolean found = false;
        for (String user : expressionList) {
          if (user.equals(candidateUser)) {
            found = true;
            break;
          }
        }
        if (found == false) {
          entryIterator.remove();
        }
      }
    }

    private boolean candidateUserExists(UserTask userTask, String userText) {
      if (userTask.getCandidateUsers() == null)
        return false;
      for (String user : userTask.getCandidateUsers()) {
        if (userText.equalsIgnoreCase(user)) {
          return true;
        }
      }
      return false;
    }

    private void removeCandidateGroups(UserTask userTask) {
      if (userTask.getCandidateGroups() == null)
        return;
      userTask.getCandidateGroups().clear();
    }

    private void removeCandidateGroupsNotInList(String[] expressionList, UserTask userTask) {
      Iterator<String> entryIterator = userTask.getCandidateGroups().iterator();
      while (entryIterator.hasNext()) {
        String candidateGroup = entryIterator.next();
        boolean found = false;
        for (String group : expressionList) {
          if (group.equals(candidateGroup)) {
            found = true;
            break;
          }
        }
        if (found == false) {
          entryIterator.remove();
        }
      }
    }

    private boolean candidateGroupExists(UserTask userTask, String groupText) {
      if (userTask.getCandidateGroups() == null)
        return false;
      for (String group : userTask.getCandidateGroups()) {
        if (groupText.equalsIgnoreCase(group)) {
          return true;
        }
      }
      return false;
    }

  };
  
  private Text createText(Composite parent, TabbedPropertySheetWidgetFactory factory, Control top) {
    Text text = factory.createText(parent, ""); //$NON-NLS-1$
    FormData data = new FormData();
    data.left = new FormAttachment(0, 160);
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

  private CCombo createCCombo(Composite parent, String[] values, TabbedPropertySheetWidgetFactory factory, Control top) {
    CCombo combo = factory.createCCombo(parent, SWT.NONE);
    combo.setItems(values);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, 0);
    if(top == null) {
      data.top = new FormAttachment(0, VSPACE);
    } else {
      data.top = new FormAttachment(top, VSPACE);
    }
    combo.setLayoutData(data);
    combo.addFocusListener(listener);
    return combo;
  }
}
