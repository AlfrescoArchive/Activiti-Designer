package org.activiti.designer.property;

import java.util.List;

import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
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
    createLabel("Candidate groups (comma separated):", composite, factory, candidateGroupsText);

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

      candidateUsersText.setText(getCandidatesString(userTask.getCandidateUsers()));
      candidateGroupsText.setText(getCandidatesString(userTask.getCandidateGroups()));

      formKeyText.setText("");
      if (formKeyText != null) {
        if (StringUtils.isNotEmpty(userTask.getFormKey())) {
          formKeyText.setText(userTask.getFormKey());
        }
      }

      dueDateText.setText("");
      if (StringUtils.isNotEmpty(userTask.getDueDate())) {
        dueDateText.setText(userTask.getDueDate().toString());
      }

      priorityText.setText("");
      if (userTask.getPriority() != null) {
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
          final UserTask userTask = (UserTask) bo;
          updateUserTaskField(userTask, e.getSource());
        }
      }
    }
  };
  
  protected String getCandidatesString(List<String> candidateList) {
    StringBuffer expressionBuffer = new StringBuffer();
    if (candidateList.size() > 0) {
      for (String candidate : candidateList) {
        if (expressionBuffer.length() > 0) {
          expressionBuffer.append(",");
        }
        expressionBuffer.append(candidate.trim());
      }
    }
    return expressionBuffer.toString();
  }
  
  protected void updateUserTaskField(final UserTask userTask, final Object source) {
    String oldValue = null;
    String newValue = ((Text) source).getText();
    if (source == assigneeText) {
      oldValue = userTask.getAssignee();
    } else if (source == candidateUsersText) {
      oldValue = getCandidatesString(userTask.getCandidateUsers());
    } else if (source == candidateGroupsText) {
      oldValue = getCandidatesString(userTask.getCandidateGroups());
    } else if (source == formKeyText) {
      oldValue = userTask.getFormKey();
    } else if (source == dueDateText) {
      oldValue = userTask.getDueDate();
    } else if (source == priorityText) {
      oldValue = userTask.getPriority();
    } else if (source == documentationText) {
      oldValue = userTask.getDocumentation();
    }
    
    if (StringUtils.isEmpty(oldValue) || oldValue.equals(newValue) == false) {
      IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
        
        @Override
        public void execute(IContext context) {
          if (source == assigneeText) {
            userTask.setAssignee(assigneeText.getText());
          } else if (source == candidateUsersText) {
            updateCandidates(userTask, source);
          } else if (source == candidateGroupsText) {
            updateCandidates(userTask, source);
          } else if (source == formKeyText) {
            userTask.setFormKey(formKeyText.getText());
          } else if (source == dueDateText) {
            userTask.setDueDate(dueDateText.getText());
          } else if (source == priorityText) {
            userTask.setPriority(priorityText.getText());
          } else if (source == documentationText) {
            userTask.setDocumentation(documentationText.getText());
          }
        }
        
        @Override
        public boolean canExecute(IContext context) {
          return true;
        }
      };
      CustomContext context = new CustomContext();
      execute(feature, context);
    }
  }
  
  protected void updateCandidates(UserTask userTask, Object source) {
    String candidates = ((Text) source).getText();
    if (StringUtils.isNotEmpty(candidates)) {
      String[] expressionList = null;
      if (candidates.contains(",")) {
        expressionList = candidates.split(",");
      } else {
        expressionList = new String[] { candidates };
      }
      
      if (source == candidateUsersText) {
        userTask.getCandidateUsers().clear();
      } else {
        userTask.getCandidateGroups().clear();
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

  private Text createText(Composite parent, TabbedPropertySheetWidgetFactory factory, Control top) {
    Text text = factory.createText(parent, ""); //$NON-NLS-1$
    FormData data = new FormData();
    data.left = new FormAttachment(0, 250);
    data.right = new FormAttachment(100, -HSPACE);
    if (top == null) {
      data.top = new FormAttachment(0, VSPACE);
    } else {
      data.top = new FormAttachment(top, VSPACE);
    }
    text.setLayoutData(data);
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
