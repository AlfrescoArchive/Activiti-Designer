package org.activiti.designer.property;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.BusinessRuleTask;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
    excludedButton.removeSelectionListener(buttonSelected);
    nonExcludedButton.removeSelectionListener(buttonSelected);
    inputVariableNamesText.removeFocusListener(listener);
    resultVariableNameText.removeFocusListener(listener);
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = getBusinessObject(pe);
      if (bo == null)
        return;

      BusinessRuleTask businessRuleTask = (BusinessRuleTask) bo;
      
      ruleNamesText.setText(transformToString(businessRuleTask.getRuleNames()));
      inputVariableNamesText.setText(transformToString(businessRuleTask.getInputVariables()));
      
      excludedButton.setSelection(businessRuleTask.isExclude());
      nonExcludedButton.setSelection(!businessRuleTask.isExclude());
      
      resultVariableNameText.setText("");
      if(businessRuleTask.getResultVariableName() != null && businessRuleTask.getResultVariableName().length() > 0) {
        resultVariableNameText.setText(businessRuleTask.getResultVariableName());
      }
      
      ruleNamesText.addFocusListener(listener);
      excludedButton.addSelectionListener(buttonSelected);
      nonExcludedButton.addSelectionListener(buttonSelected);
      inputVariableNamesText.addFocusListener(listener);
      resultVariableNameText.addFocusListener(listener);
    }
  }
  
  private SelectionListener buttonSelected = new SelectionListener() {

    @Override
    public void widgetSelected(final SelectionEvent event) {
      final PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        final Object bo = getBusinessObject(pe);
        if (bo instanceof BusinessRuleTask) {
          BusinessRuleTask ruleTask = (BusinessRuleTask) bo;
          if (event.getSource() == excludedButton) {
            if (excludedButton.getSelection()) {
              if (ruleTask.isExclude() == false) {
                updateExclude(true, ruleTask);
              }
            }
          } else if (event.getSource() == nonExcludedButton) {
            if (nonExcludedButton.getSelection()) {
              if (ruleTask.isExclude()) {
                updateExclude(false, ruleTask);
              }
            }
          }
        }
      }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
      widgetSelected(event);
    }
  };
  
  protected void updateExclude(final boolean exclude, final BusinessRuleTask ruleTask) {
    IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
      
      @Override
      public void execute(IContext context) {
        ruleTask.setExclude(exclude);
      }
      
      @Override
      public boolean canExecute(IContext context) {
        return true;
      }
    };
    CustomContext context = new CustomContext();
    execute(feature, context);
  }
  
  protected String transformToString(List<String> nameList) {
    StringBuilder nameBuilder = new StringBuilder();
    if (nameList.size() > 0) {
      for (String name: nameList) {
        if(nameBuilder.length() > 0) {
          nameBuilder.append(",");
        }
        nameBuilder.append(name);
      }
    }
    return nameBuilder.toString();
  }

  private FocusListener listener = new FocusListener() {

    public void focusGained(final FocusEvent e) {
    }

    public void focusLost(final FocusEvent e) {
      PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        final Object bo = getBusinessObject(pe);
        if (bo instanceof BusinessRuleTask) {
          BusinessRuleTask businessRuleTask = (BusinessRuleTask) bo;
          updateBusinessRuleField(businessRuleTask, e.getSource());
        }
      }
    }
  };
  
  protected void updateBusinessRuleField(final BusinessRuleTask businessRuleTask, final Object source) {
    String oldValue = null;
    final String newValue = ((Text) source).getText();
    if (source == ruleNamesText) {
      oldValue = transformToString(businessRuleTask.getRuleNames());
    } else if (source == inputVariableNamesText) {
      oldValue = transformToString(businessRuleTask.getInputVariables());
    } else if (source == resultVariableNameText) {
      oldValue = businessRuleTask.getResultVariableName();
    }
    
    if ((StringUtils.isEmpty(oldValue) && StringUtils.isNotEmpty(newValue)) || (StringUtils.isNotEmpty(oldValue) && newValue.equals(oldValue) == false)) {
      IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
        
        @Override
        public void execute(IContext context) {
          if (source == ruleNamesText) {
            businessRuleTask.getRuleNames().clear();
            businessRuleTask.getRuleNames().addAll(convertToList(newValue));
          } else if (source == inputVariableNamesText) {
            businessRuleTask.getInputVariables().clear();
            businessRuleTask.getInputVariables().addAll(convertToList(newValue));
          } else if (source == resultVariableNameText) {
            businessRuleTask.setResultVariableName(newValue);
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
  
  protected List<String> convertToList(String stringList) {
    List<String> nameList = new ArrayList<String>();
    if (StringUtils.isNotEmpty(stringList)) {
      if(stringList.contains(",") == false) {
        nameList.add(stringList);
      } else {
        String[] nameArray = stringList.split(",");
        for (String name : nameArray) {
          nameList.add(name);
        }
      }
    }
    return nameList;
  }

}
