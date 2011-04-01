package org.activiti.designer.property;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.CandidateGroup;
import org.eclipse.bpmn2.CandidateUser;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyUserTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private CCombo performerTypeCombo;
  private List<String> performerTypes = Arrays.asList("Assignee", "Candidate users", "Candidate groups");
  private String currentType = "Assignee";
  private Text expressionText;
  private Text formKeyText;

  private Text documentationText;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    Composite composite = factory.createFlatFormComposite(parent);
    FormData data;

    performerTypeCombo = factory.createCCombo(composite, SWT.NONE);
    performerTypeCombo.setItems((String[]) performerTypes.toArray());
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, VSPACE);
    performerTypeCombo.setLayoutData(data);
    performerTypeCombo.addFocusListener(listener);

    CLabel performerTypeLabel = factory.createCLabel(composite, "Performer type:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(performerTypeCombo, -HSPACE);
    data.top = new FormAttachment(performerTypeCombo, 0, SWT.CENTER);
    performerTypeLabel.setLayoutData(data);

    expressionText = factory.createText(composite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(performerTypeCombo, VSPACE);
    expressionText.setLayoutData(data);
    expressionText.addFocusListener(listener);

    CLabel expressionLabel = factory.createCLabel(composite, "Expression:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(expressionText, -HSPACE);
    data.top = new FormAttachment(expressionText, 0, SWT.TOP);
    expressionLabel.setLayoutData(data);

    formKeyText = factory.createText(composite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(expressionText, VSPACE);
    formKeyText.setLayoutData(data);
    formKeyText.addFocusListener(listener);

    CLabel formKeyLabel = factory.createCLabel(composite, "Form key:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(formKeyText, -HSPACE);
    data.top = new FormAttachment(formKeyText, 0, SWT.TOP);
    formKeyLabel.setLayoutData(data);

    documentationText = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
    data = new FormData(SWT.DEFAULT, 100);
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(formKeyText, VSPACE);
    documentationText.setLayoutData(data);
    documentationText.addFocusListener(listener);

    CLabel scriptLabel = factory.createCLabel(composite, "Documentation:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(documentationText, -HSPACE);
    data.top = new FormAttachment(documentationText, 0, SWT.TOP);
    scriptLabel.setLayoutData(data);

  }

  @Override
  public void refresh() {
    performerTypeCombo.removeFocusListener(listener);
    expressionText.removeFocusListener(listener);
    formKeyText.removeFocusListener(listener);
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
        for (CandidateUser user : userTask.getCandidateUsers()) {
          if (expressionBuffer.length() > 0) {
            expressionBuffer.append(";");
          }
          expressionBuffer.append(user.getUser());
        }
        expressionText.setText(expressionBuffer.toString());
      } else if (userTask.getCandidateGroups() != null && userTask.getCandidateGroups().size() > 0) {
        performerIndex = performerTypes.indexOf("Candidate groups");
        StringBuffer expressionBuffer = new StringBuffer();
        for (CandidateGroup group : userTask.getCandidateGroups()) {
          if (expressionBuffer.length() > 0) {
            expressionBuffer.append(";");
          }
          expressionBuffer.append(group.getGroup());
        }
        expressionText.setText(expressionBuffer.toString());
      } else {
        performerIndex = performerTypes.indexOf("Assignee");
        if (userTask.getAssignee() != null && userTask.getAssignee().length() > 0) {
          expressionText.setText(userTask.getAssignee());
        }
      }
      formKeyText.setText("");
      if (userTask.getFormKey() != null && userTask.getFormKey().length() > 0) {
        formKeyText.setText(userTask.getFormKey());
      }

      documentationText.setText("");
      if (userTask.getDocumentation() != null && userTask.getDocumentation().size() > 0) {
        documentationText.setText(userTask.getDocumentation().get(0).getText());
      }

      performerTypeCombo.select(performerIndex == -1 ? 0 : performerIndex);
      performerTypeCombo.addFocusListener(listener);
      expressionText.addFocusListener(listener);
      formKeyText.addFocusListener(listener);
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
              UserTask userTask = (UserTask) bo;
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
                      CandidateUser candidateUser = Bpmn2Factory.eINSTANCE.createCandidateUser();
                      candidateUser.setUser(user);
                      getDiagram().eResource().getContents().add(candidateUser);
                      userTask.getCandidateUsers().add(candidateUser);
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
                      CandidateGroup candidateGroup = Bpmn2Factory.eINSTANCE.createCandidateGroup();
                      candidateGroup.setGroup(group);
                      getDiagram().eResource().getContents().add(candidateGroup);
                      userTask.getCandidateGroups().add(candidateGroup);
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

              String documentation = documentationText.getText();
              if (documentation != null) {

                Documentation documentationModel;
                if (userTask.getDocumentation().size() < 1) {
                  documentationModel = Bpmn2Factory.eINSTANCE.createDocumentation();
                  userTask.getDocumentation().add(documentationModel);
                } else {
                  documentationModel = userTask.getDocumentation().get(0);
                }
                documentationModel.setText(documentation);
              }
            }
          }, editingDomain, "Model Update");
        }

      }
    }

    private void removeCandidateUsers(UserTask userTask) {
      if (userTask.getCandidateUsers() == null)
        return;
      for (CandidateUser user : userTask.getCandidateUsers()) {
        getDiagram().eResource().getContents().remove(user);
      }
      userTask.getCandidateUsers().clear();
    }

    private void removeCandidateUsersNotInList(String[] expressionList, UserTask userTask) {
      Iterator<CandidateUser> entryIterator = userTask.getCandidateUsers().iterator();
      while (entryIterator.hasNext()) {
        CandidateUser candidateUser = entryIterator.next();
        boolean found = false;
        for (String user : expressionList) {
          if (user.equals(candidateUser.getUser())) {
            found = true;
            break;
          }
        }
        if (found == false) {
          getDiagram().eResource().getContents().remove(candidateUser);
          entryIterator.remove();
        }
      }
    }

    private boolean candidateUserExists(UserTask userTask, String userText) {
      if (userTask.getCandidateUsers() == null)
        return false;
      for (CandidateUser user : userTask.getCandidateUsers()) {
        if (userText.equalsIgnoreCase(user.getUser())) {
          return true;
        }
      }
      return false;
    }

    private void removeCandidateGroups(UserTask userTask) {
      if (userTask.getCandidateGroups() == null)
        return;
      for (CandidateGroup group : userTask.getCandidateGroups()) {
        getDiagram().eResource().getContents().remove(group);
      }
      userTask.getCandidateGroups().clear();
    }

    private void removeCandidateGroupsNotInList(String[] expressionList, UserTask userTask) {
      Iterator<CandidateGroup> entryIterator = userTask.getCandidateGroups().iterator();
      while (entryIterator.hasNext()) {
        CandidateGroup candidateGroup = entryIterator.next();
        boolean found = false;
        for (String group : expressionList) {
          if (group.equals(candidateGroup.getGroup())) {
            found = true;
            break;
          }
        }
        if (found == false) {
          getDiagram().eResource().getContents().remove(candidateGroup);
          entryIterator.remove();
        }
      }
    }

    private boolean candidateGroupExists(UserTask userTask, String groupText) {
      if (userTask.getCandidateGroups() == null)
        return false;
      for (CandidateGroup group : userTask.getCandidateGroups()) {
        if (groupText.equalsIgnoreCase(group.getGroup())) {
          return true;
        }
      }
      return false;
    }

  };

}
