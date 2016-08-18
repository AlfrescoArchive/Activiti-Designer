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
package com.alfresco.designer.gui.property;

import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.property.ActivitiPropertySection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyAlfrescoMailTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text toText;
	private Text toManyText;
	private Text fromText;
	private Text subjectText;
	private Text htmlText;
	private Text nonHtmlText;
	private Text templateText;
	private Text templateModelText;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    toText = createTextControl(false);
    createLabel("To", toText);
    toManyText = createTextControl(false);
    createLabel("To many", toText);
    fromText = createTextControl(false);
    createLabel("From", fromText);
    subjectText = createTextControl(false);
    createLabel("Subject", subjectText);
    templateText = createTextControl(false);
    createLabel("Template", templateText);
    templateModelText = createTextControl(false);
    createLabel("Template model", templateModelText);
    htmlText = createTextControl(true);
    createLabel("Html", htmlText);
    nonHtmlText = createTextControl(true);
    createLabel("Non html", nonHtmlText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    ServiceTask task = (ServiceTask) businessObject;
    if (control == toText) {
      return getFieldString("mail.parameters.to", task);
    } else if (control == toManyText) {
      return getFieldString("mail.parameters.to_many", task);
    } else if (control == fromText) {
      return getFieldString("mail.parameters.from", task);
    } else if (control == subjectText) {
      return getFieldString("mail.parameters.subject", task);
    } else if (control == templateText) {
      return getFieldString("mail.parameters.template", task);
    } else if (control == templateModelText) {
      return getFieldString("mail.parameters.template_model", task);
    } else if (control == htmlText) {
      return getFieldString("mail.parameters.html", task);
    } else if (control == nonHtmlText) {
      return getFieldString("mail.parameters.text", task);
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    ServiceTask task = (ServiceTask) businessObject;
    if (control == toText) {
      setFieldString("mail.parameters.to", toText.getText(), task);
    } else if (control == toManyText) {
      setFieldString("mail.parameters.to_many", toManyText.getText(), task);
    } else if (control == fromText) {
      setFieldString("mail.parameters.from", fromText.getText(), task);
    } else if (control == subjectText) {
      setFieldString("mail.parameters.subject", subjectText.getText(), task);
    } else if (control == templateText) {
      setFieldString("mail.parameters.template", templateText.getText(), task);
    } else if (control == templateModelText) {
      setFieldString("mail.parameters.template_model", templateModelText.getText(), task);
    } else if (control == htmlText) {
      setFieldString("mail.parameters.html", htmlText.getText(), task);
    } else if (control == nonHtmlText) {
      setFieldString("mail.parameters.text", nonHtmlText.getText(), task);
    }
  }
}
