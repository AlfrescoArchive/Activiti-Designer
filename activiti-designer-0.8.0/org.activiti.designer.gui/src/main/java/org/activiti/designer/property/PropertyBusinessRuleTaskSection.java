package org.activiti.designer.property;

import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyBusinessRuleTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Text ruleNamesText;
  private Text inputVariableNamesText;
  private Button excludedButton;
  private Button nonExcludedButton;
  private Text resultVariableNameText;

  private Text documentationText;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    Composite composite = factory.createFlatFormComposite(parent);
    FormData data;
   
    ruleNamesText = factory.createText(composite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, VSPACE);
    ruleNamesText.setLayoutData(data);
    ruleNamesText.addFocusListener(listener);

    CLabel ruleNamesLabel = factory.createCLabel(composite, "Rule names:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(ruleNamesText, -HSPACE);
    data.top = new FormAttachment(ruleNamesText, 0, SWT.TOP);
    ruleNamesLabel.setLayoutData(data);

    inputVariableNamesText = factory.createText(composite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(ruleNamesText, VSPACE);
    inputVariableNamesText.setLayoutData(data);
    inputVariableNamesText.addFocusListener(listener);

    CLabel inputVariableNamesLabel = factory.createCLabel(composite, "Input variables:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(inputVariableNamesText, -HSPACE);
    data.top = new FormAttachment(inputVariableNamesText, 0, SWT.TOP);
    inputVariableNamesLabel.setLayoutData(data);
    
    Composite excludedComposite = factory.createComposite(composite);
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(inputVariableNamesText, VSPACE);
    excludedComposite.setLayoutData(data);
    excludedComposite.setLayout(new RowLayout());
    
    excludedButton = factory.createButton(excludedComposite, "Yes", SWT.RADIO);
    nonExcludedButton = factory.createButton(excludedComposite, "No", SWT.RADIO);

    CLabel excludedLabel = factory.createCLabel(composite, "Excluded:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(excludedComposite, -HSPACE);
    data.top = new FormAttachment(excludedComposite, 0, SWT.TOP);
    excludedLabel.setLayoutData(data);
    
    resultVariableNameText = factory.createText(composite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(excludedComposite, VSPACE);
    resultVariableNameText.setLayoutData(data);
    resultVariableNameText.addFocusListener(listener);

    CLabel resultVariableNameLabel = factory.createCLabel(composite, "Result variable:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(resultVariableNameText, -HSPACE);
    data.top = new FormAttachment(resultVariableNameText, 0, SWT.TOP);
    resultVariableNameLabel.setLayoutData(data);
  }

  @Override
  public void refresh() {
    ruleNamesText.removeFocusListener(listener);
    excludedButton.removeFocusListener(listener);
    nonExcludedButton.removeFocusListener(listener);
    inputVariableNamesText.removeFocusListener(listener);
    resultVariableNameText.removeFocusListener(listener);
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;

      BusinessRuleTask businessRuleTask = (BusinessRuleTask) bo;
      
      ruleNamesText.setText("");
      if(businessRuleTask.getRuleNames().size() > 0) {
        StringBuilder ruleNameBuilder = new StringBuilder();
        for (String ruleName: businessRuleTask.getRuleNames()) {
          if(ruleNameBuilder.length() > 0) {
            ruleNameBuilder.append(",");
          }
          ruleNameBuilder.append(ruleName);
        }
        ruleNamesText.setText(ruleNameBuilder.toString());
      }
      
      inputVariableNamesText.setText("");
      if(businessRuleTask.getInputVariables().size() > 0) {
        StringBuilder inputBuilder = new StringBuilder();
        for (String input: businessRuleTask.getInputVariables()) {
          if(inputBuilder.length() > 0) {
            inputBuilder.append(",");
          }
          inputBuilder.append(input);
        }
        inputVariableNamesText.setText(inputBuilder.toString());
      }
      
      excludedButton.setSelection(businessRuleTask.isExclude());
      nonExcludedButton.setSelection(!businessRuleTask.isExclude());
      
      resultVariableNameText.setText("");
      if(businessRuleTask.getResultVariableName() != null && businessRuleTask.getResultVariableName().length() > 0) {
        resultVariableNameText.setText(businessRuleTask.getResultVariableName());
      }
      
      ruleNamesText.addFocusListener(listener);
      excludedButton.addFocusListener(listener);
      nonExcludedButton.addFocusListener(listener);
      inputVariableNamesText.addFocusListener(listener);
      resultVariableNameText.addFocusListener(listener);
    }
  }

  private FocusListener listener = new FocusListener() {

    public void focusGained(final FocusEvent e) {
    }

    public void focusLost(final FocusEvent e) {
      PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
        if (bo instanceof BusinessRuleTask) {
          DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
          TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
          ActivitiUiUtil.runModelChange(new Runnable() {

            public void run() {
              Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
              if (bo == null) {
                return;
              }
              BusinessRuleTask businessRuleTask = (BusinessRuleTask) bo;
              String ruleNames = ruleNamesText.getText();
              if (ruleNames != null && ruleNames.length() > 0) {
                businessRuleTask.getRuleNames().clear();
                if(ruleNames.contains(",") == false) {
                  businessRuleTask.getRuleNames().add(ruleNames);
                } else {
                  String[] ruleNameList = ruleNames.split(",");
                  for (String rule : ruleNameList) {
                    businessRuleTask.getRuleNames().add(rule);
                  }
                }
              }
              String inputNames = inputVariableNamesText.getText();
              if (inputNames != null && inputNames.length() > 0) {
                businessRuleTask.getInputVariables().clear();
                if(inputNames.contains(",") == false) {
                  businessRuleTask.getInputVariables().add(inputNames);
                } else {
                  String[] inputNamesList = inputNames.split(",");
                  for (String input : inputNamesList) {
                    businessRuleTask.getInputVariables().add(input);
                  }
                }
              }
              businessRuleTask.setExclude(excludedButton.getSelection());
              String resultVariable = resultVariableNameText.getText();
              if (resultVariable != null) {
                businessRuleTask.setResultVariableName(resultVariable);
              } else {
                businessRuleTask.setResultVariableName("");
              }
            }
          }, editingDomain, "Model Update");
        }

      }
    }

  };

}
