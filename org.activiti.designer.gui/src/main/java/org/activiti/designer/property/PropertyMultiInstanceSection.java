/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.property;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyMultiInstanceSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Combo sequentialCombo;
	private Text loopCardinaltyText;
	private Text collectionText;
	private Text elementVariableText;
	private Text completionConditionText;
	private String[] sequentialValues = new String[] {"true", "false"};
	
	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
	  sequentialCombo = createCombobox(sequentialValues, 1);
    createLabel("Sequential", sequentialCombo);
    loopCardinaltyText = createTextControl(false);
    createLabel("Loop cardinality", loopCardinaltyText);
    collectionText = createTextControl(false);
    createLabel("Collection", collectionText);
    elementVariableText = createTextControl(false);
    createLabel("Element variable", elementVariableText);
    completionConditionText = createTextControl(false);
    createLabel("Completion condition", completionConditionText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    Activity activity = (Activity) businessObject;
    MultiInstanceLoopCharacteristics multiInstanceDef = activity.getLoopCharacteristics();
    if (multiInstanceDef != null) {
      if (control == sequentialCombo) {
        return multiInstanceDef.isSequential();
      } else if (control == loopCardinaltyText) {
        return multiInstanceDef.getLoopCardinality();
      } else if (control == collectionText) {
        return multiInstanceDef.getInputDataItem();
      } else if (control == elementVariableText) {
        return multiInstanceDef.getElementVariable();
      } else if (control == completionConditionText) {
        return multiInstanceDef.getCompletionCondition();
      }
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    Activity activity = (Activity) businessObject;
    MultiInstanceLoopCharacteristics multiInstanceDef = activity.getLoopCharacteristics();
    if (multiInstanceDef == null) {
      multiInstanceDef = new MultiInstanceLoopCharacteristics();
      activity.setLoopCharacteristics(multiInstanceDef);
    }
    
    if (control == sequentialCombo) {
      multiInstanceDef.setSequential(Boolean.valueOf(sequentialValues[sequentialCombo.getSelectionIndex()]));
    } else if (control == loopCardinaltyText) {
      multiInstanceDef.setLoopCardinality(loopCardinaltyText.getText());
    } else if (control == collectionText) {
      multiInstanceDef.setInputDataItem(collectionText.getText());
    } else if (control == elementVariableText) {
      multiInstanceDef.setElementVariable(elementVariableText.getText());
    } else if (control == completionConditionText) {
      multiInstanceDef.setCompletionCondition(completionConditionText.getText());
    }
  }
}
