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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.annotation.PropertyItems;
import org.activiti.designer.integration.servicetask.validator.RequiredFieldValidator;
import org.activiti.designer.property.PropertyCustomServiceTaskSection;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * @author Tiese Barrell
 * @since 0.7.0
 * @version 1
 */
public class CustomPropertyComboboxChoiceField extends AbstractCustomPropertyField {

  private CCombo comboControl;
  private PropertyItems propertyItemsAnnotation;

  private Map<String, String> values;

  public CustomPropertyComboboxChoiceField(final PropertyCustomServiceTaskSection section, final ServiceTask serviceTask, final Field field) {
    super(section, serviceTask, field);
  }

  @Override
  public PropertyType getPrimaryPropertyType() {
    return PropertyType.COMBOBOX_CHOICE;
  }

  @Override
  public void refresh() {
    final String storedValue = getSimpleValueFromModel();
    for (final Entry<String, String> entry : values.entrySet()) {
      if (entry.getKey().equals(storedValue)) {
        comboControl.setText(entry.getValue());
        break;
      }
    }
  }

  @Override
  public String getSimpleValue() {
    String result = "";
    for (final Entry<String, String> entry : values.entrySet()) {
      if (entry.getValue().equals(comboControl.getText())) {
        result = entry.getKey();
        break;
      }
    }
    return result;
  }

  @Override
  public Composite render(final Composite parent, final TabbedPropertySheetWidgetFactory factory, final FocusListener listener) {

    final Composite result = factory.createFlatFormComposite(parent);
    FormData data;

    String[] labels = null;

    if (propertyItemsAnnotation == null) {
      propertyItemsAnnotation = getField().getAnnotation(PropertyItems.class);
      if (propertyItemsAnnotation != null) {

        final String[] itemValues = propertyItemsAnnotation.value();

        values = new HashMap<String, String>();
        labels = new String[itemValues.length / 2];

        for (int i = 0; i < itemValues.length; i += 2) {
          values.put(itemValues[i + 1], itemValues[i]);
          labels[i / 2] = itemValues[i];
        }
      }
    }

    comboControl = factory.createCCombo(result, SWT.DROP_DOWN);
    comboControl.setEnabled(true);

    comboControl.setItems(labels);

    if (getPropertyAnnotation().required()) {
      addFieldValidator(comboControl, RequiredFieldValidator.class);
    }

    if (getPropertyAnnotation().fieldValidator() != null) {
      addFieldValidator(comboControl, getPropertyAnnotation().fieldValidator());
    }

    comboControl.addFocusListener(listener);

    data = new FormData();
    data.left = new FormAttachment(0);
    data.top = new FormAttachment(0);
    data.right = new FormAttachment(100);
    comboControl.setLayoutData(data);

    return result;
  }
}
