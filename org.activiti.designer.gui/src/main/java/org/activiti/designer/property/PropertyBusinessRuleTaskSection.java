package org.activiti.designer.property;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.BusinessRuleTask;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyBusinessRuleTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Text ruleNamesText;
  private Text inputVariableNamesText;
  private Button excludedButton;
  private Text resultVariableNameText;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    ruleNamesText = createTextControl(false);
    createLabel("Rule names", ruleNamesText);
    inputVariableNamesText = createTextControl(false);
    createLabel("Input variables", inputVariableNamesText);
    excludedButton = createCheckboxControl("Excluded");
    resultVariableNameText = createTextControl(false);
    createLabel("Result variable", resultVariableNameText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    BusinessRuleTask task = (BusinessRuleTask) businessObject;
    if (control == ruleNamesText) {
      return transformToString(task.getRuleNames());
      
    } else if (control == inputVariableNamesText) {
      return transformToString(task.getInputVariables());
    
    } else if (control == excludedButton) {
      return task.isExclude();
    
    } else if (control == resultVariableNameText) {
      return task.getResultVariableName();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    BusinessRuleTask task = (BusinessRuleTask) businessObject;
    if (control == ruleNamesText) {
      task.setRuleNames(convertToList(ruleNamesText.getText()));
      
    } else if (control == inputVariableNamesText) {
      task.setInputVariables(convertToList(inputVariableNamesText.getText()));
    
    } else if (control == excludedButton) {
      task.setExclude(excludedButton.getSelection());
      
    } else if (control == resultVariableNameText) {
      task.setResultVariableName(resultVariableNameText.getText());
    }
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
