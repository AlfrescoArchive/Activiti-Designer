/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.designer.property.extension.field;

import java.lang.reflect.Field;

import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.validator.RequiredFieldValidator;
import org.activiti.designer.property.PropertyCustomServiceTaskSection;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 */
public class CustomPropertyMultilineTextField extends AbstractCustomPropertyField {

  private Text textControl;

  public CustomPropertyMultilineTextField(final PropertyCustomServiceTaskSection section, final ServiceTask serviceTask, final Field field) {
    super(section, serviceTask, field);
  }

  @Override
  public PropertyType getPrimaryPropertyType() {
    return PropertyType.MULTILINE_TEXT;
  }

  @Override
  public void refresh() {
    textControl.setText(getSimpleValueOrDefault());
  }

  @Override
  public String getSimpleValue() {
    return textControl.getText();
  }

  @Override
  public Composite render(final Composite parent, final TabbedPropertySheetWidgetFactory factory, final FocusListener listener) {

    final Composite result = factory.createFlatFormComposite(parent);
    FormData data;

    textControl = factory.createText(result, "", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER_SOLID);
    textControl.setEnabled(true);

    if (getPropertyAnnotation().required()) {
      addFieldValidator(textControl, RequiredFieldValidator.class);
    }

    if (getPropertyAnnotation().fieldValidator() != null) {
      addFieldValidator(textControl, getPropertyAnnotation().fieldValidator());
    }

    textControl.addFocusListener(listener);

    data = new FormData();
    data.left = new FormAttachment(0);
    data.top = new FormAttachment(0);
    data.right = new FormAttachment(100);
    data.height = 80;
    textControl.setLayoutData(data);

    return result;
  }
}
