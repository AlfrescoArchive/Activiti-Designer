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
import org.activiti.designer.property.PropertyCustomServiceTaskSection;
import org.activiti.designer.property.extension.field.validator.RadioRequiredFieldValidator;
import org.apache.commons.lang.StringUtils;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * @author Tiese Barrell
 * @since 0.7.0
 * @version 1
 */
public class CustomPropertyRadioChoiceField extends AbstractCustomPropertyField {

  private static final String STORAGE_VALUE = "storageValue";

  private Composite parentControl;
  private PropertyItems propertyItemsAnnotation;

  private Map<String, String> values;

  public CustomPropertyRadioChoiceField(final PropertyCustomServiceTaskSection section, final ServiceTask serviceTask, final Field field) {
    super(section, serviceTask, field);
  }

  @Override
  public PropertyType getPrimaryPropertyType() {
    return PropertyType.RADIO_CHOICE;
  }

  @Override
  public void refresh() {
    final String value = getSimpleValueOrDefault();

    for (final Entry<String, String> entry : values.entrySet()) {
      if (entry.getKey().equals(value)) {
        for (final Control currentControl : parentControl.getChildren()) {

          if (currentControl instanceof Button) {
            final Object data = currentControl.getData(STORAGE_VALUE);
            if (data != null && data instanceof String) {
              if (StringUtils.equals(entry.getKey(), (String) data)) {
                ((Button) currentControl).setSelection(true);
              }
            } else {
              ((Button) currentControl).setSelection(false);
            }
          }
        }
      }
    }
  }
  @Override
  public String getSimpleValue() {
    String result = "";

    for (final Control currentControl : parentControl.getChildren()) {

      if (currentControl instanceof Button) {
        boolean selected = ((Button) currentControl).getSelection();
        if (selected) {
          final Object data = currentControl.getData(STORAGE_VALUE);
          if (data != null && data instanceof String) {
            result = (String) data;
            break;
          }
        }

      }
    }
    return result;
  }

  @Override
  public Composite render(final Composite parent, final TabbedPropertySheetWidgetFactory factory, final FocusListener listener) {

    parentControl = factory.createFlatFormComposite(parent);
    FormData data;

    if (propertyItemsAnnotation == null) {
      propertyItemsAnnotation = getField().getAnnotation(PropertyItems.class);
      if (propertyItemsAnnotation != null) {

        final String[] itemValues = propertyItemsAnnotation.value();

        values = new HashMap<String, String>();

        for (int i = 0; i < itemValues.length; i += 2) {
          values.put(itemValues[i + 1], itemValues[i]);
        }
      }
    }

    Control previousAnchor = parentControl;

    for (final Entry<String, String> currentItem : values.entrySet()) {
      final Button currentButton = factory.createButton(parentControl, currentItem.getValue(), SWT.RADIO | SWT.BORDER_SOLID);
      currentButton.setData(STORAGE_VALUE, currentItem.getKey());
      currentButton.setEnabled(true);

      data = new FormData();
      data.left = new FormAttachment(previousAnchor);
      currentButton.setLayoutData(data);

      previousAnchor = currentButton;

      currentButton.addFocusListener(listener);
    }

    if (getPropertyAnnotation().required()) {
      addFieldValidator(parentControl, RadioRequiredFieldValidator.class);
    }

    if (getPropertyAnnotation().fieldValidator() != null) {
      addFieldValidator(parentControl, getPropertyAnnotation().fieldValidator());
    }

    parentControl.addFocusListener(listener);

    data = new FormData();
    data.left = new FormAttachment(0);
    data.top = new FormAttachment(0);
    data.right = new FormAttachment(100);
    parentControl.setLayoutData(data);

    return parentControl;
  }
}
