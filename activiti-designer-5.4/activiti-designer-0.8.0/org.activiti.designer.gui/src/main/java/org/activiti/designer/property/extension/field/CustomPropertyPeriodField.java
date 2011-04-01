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
import org.activiti.designer.property.PropertyCustomServiceTaskSection;
import org.activiti.designer.property.custom.PeriodPropertyElement;
import org.activiti.designer.property.extension.field.validator.PeriodRequiredFieldValidator;
import org.activiti.designer.property.extension.util.ExtensionPropertyUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 */
public class CustomPropertyPeriodField extends AbstractCustomPropertyField {

  private Composite periodControl;

  public CustomPropertyPeriodField(final PropertyCustomServiceTaskSection section, final ServiceTask serviceTask, final Field field) {
    super(section, serviceTask, field);
  }

  @Override
  public PropertyType getPrimaryPropertyType() {
    return PropertyType.PERIOD;
  }

  @Override
  public void refresh() {

    final String value = getSimpleValueFromModel();

    if (StringUtils.isNotEmpty(value)) {

      for (final Control childControl : periodControl.getChildren()) {
        if (childControl instanceof Spinner) {
          Spinner actualControl = (Spinner) childControl;
          String periodKey = (String) childControl.getData("PERIOD_KEY");
          PeriodPropertyElement element = PeriodPropertyElement.byShortFormat(periodKey);
          if (element != null) {
            actualControl.setSelection(ExtensionPropertyUtil.getPeriodPropertyElementFromValue(value, element));
          }
        }
      }
    }
  }

  @Override
  public String getSimpleValue() {
    return ExtensionPropertyUtil.getPeriodValueFromParent(periodControl);
  }

  @Override
  public Composite render(Composite parent, TabbedPropertySheetWidgetFactory factory, final FocusListener listener) {

    periodControl = factory.createFlatFormComposite(parent);
    FormData data;

    Control previousGroupAnchor = null;

    int i = 0;

    PeriodPropertyElement[] properties = PeriodPropertyElement.values();

    for (final PeriodPropertyElement element : properties) {

      final Spinner spinner = new Spinner(periodControl, SWT.BORDER);

      spinner.setData("PERIOD_KEY", element.getShortFormat());
      data = new FormData();
      data.top = new FormAttachment(periodControl);
      if (previousGroupAnchor != null) {
        data.left = new FormAttachment(previousGroupAnchor);
      }
      data.width = 30;
      spinner.setEnabled(true);
      spinner.setLayoutData(data);

      String labelText = element.getShortFormat();
      if (i != properties.length - 1) {
        labelText += " ,  ";
      }

      CLabel labelShort = factory.createCLabel(periodControl, labelText, SWT.NONE);

      data = new FormData();
      data.left = new FormAttachment(spinner);
      data.top = new FormAttachment(spinner, 0, SWT.CENTER);
      labelShort.setLayoutData(data);
      labelShort.setToolTipText(element.getLongFormat());

      previousGroupAnchor = labelShort;

      spinner.addFocusListener(listener);

      i++;
    }

    if (getPropertyAnnotation().required()) {
      addFieldValidator(periodControl, PeriodRequiredFieldValidator.class);
    }

    if (getPropertyAnnotation().fieldValidator() != null) {
      addFieldValidator(periodControl, getPropertyAnnotation().fieldValidator());
    }

    data = new FormData();
    data.left = new FormAttachment(0);
    data.top = new FormAttachment(0);
    data.right = new FormAttachment(100);
    periodControl.setLayoutData(data);

    return periodControl;
  }
}
