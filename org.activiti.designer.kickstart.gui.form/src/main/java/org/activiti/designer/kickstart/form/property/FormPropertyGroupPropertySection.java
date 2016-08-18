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
package org.activiti.designer.kickstart.form.property;

import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class FormPropertyGroupPropertySection extends AbstractKickstartFormComponentSection {
  
  protected static final String SINGLE_COLUMN_MESSAGE = "Single column layout";
  protected static final String TWO_COLUMN_MESSAGE = "Two column layout";
  protected static final String THREE_COLUMN_MESSAGE = "Three column layout";
  
  protected static final String[] LAYOUT_VALUES = new String[] {SINGLE_COLUMN_MESSAGE, TWO_COLUMN_MESSAGE, THREE_COLUMN_MESSAGE};
  protected Text idControl;
  protected Text titleControl;
  protected Combo typeControl;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    titleControl = createTextControl(false);
    createLabel("Title", titleControl);
    
    idControl = createTextControl(false);
    createLabel("Group ID", idControl);
    
    typeControl = createCombobox(LAYOUT_VALUES, 0);
    createLabel("Type", typeControl);
  }

  
  @Override
  public void refresh() {
    super.refresh();
    
    // ID of "info" group cannot be changed
    FormPropertyGroup group = (FormPropertyGroup) getBusinessObject(getSelectedPictogramElement());
    if(group != null) {
      idControl.setEnabled(!KickstartFormMemoryModel.INFO_GROUP_ID.equals(group.getId()));
    }
  }
  
  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    FormPropertyGroup group = (FormPropertyGroup) businessObject;
    if(control == idControl) {
      return group.getId();
    } else if(control == titleControl) {
      return group.getTitle();
    } else if(control == typeControl) {
      return getTypeMessageForvalue(group.getType());
    }
    return null;
  }
  
  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    FormPropertyGroup group = (FormPropertyGroup) businessObject;
    if(control == idControl) {
      group.setId(idControl.getText());
    } else if(control == titleControl) {
      group.setTitle(titleControl.getText());
    } else if(control == typeControl) {
      group.setType(getTypeValueForMessage(typeControl.getText()));
    }
  }
  
  /**
   * @return the message to display for the given type.
   */
  protected String getTypeValueForMessage(String text) {
    if(TWO_COLUMN_MESSAGE.equals(text) ) {
      return AlfrescoConversionConstants.FORM_GROUP_LAYOUT_2_COLUMNS;
    } else if(THREE_COLUMN_MESSAGE.equals(text)) {
      return AlfrescoConversionConstants.FORM_GROUP_LAYOUT_3_COLUMNS;
    }
    return AlfrescoConversionConstants.FORM_GROUP_LAYOUT_1_COLUMN;
  }
  
  /**
   * @return the actual value to use for the group type, based on the selected
   * value.
   */
  protected String getTypeMessageForvalue(String type) {
    if(AlfrescoConversionConstants.FORM_GROUP_LAYOUT_2_COLUMNS.equals(type) ) {
      return TWO_COLUMN_MESSAGE;
    } else if(AlfrescoConversionConstants.FORM_GROUP_LAYOUT_3_COLUMNS.equals(type)) {
      return THREE_COLUMN_MESSAGE;
    }
    return SINGLE_COLUMN_MESSAGE;
  }
}
