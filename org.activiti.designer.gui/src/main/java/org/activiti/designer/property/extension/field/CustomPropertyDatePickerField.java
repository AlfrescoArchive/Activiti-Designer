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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.annotation.DatePickerProperty;
import org.activiti.designer.integration.servicetask.validator.RequiredFieldValidator;
import org.activiti.designer.property.PropertyCustomServiceTaskSection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * @author Tiese Barrell
 * @since 0.7.0
 * @version 1
 */
public class CustomPropertyDatePickerField extends AbstractCustomPropertyField {

  private DateTime calendarControl;
  private SimpleDateFormat sdf;

  public CustomPropertyDatePickerField(final PropertyCustomServiceTaskSection section, final ServiceTask serviceTask, final Field field) {
    super(section, serviceTask, field);
  }

  @Override
  public PropertyType getPrimaryPropertyType() {
    return PropertyType.TEXT;
  }

  @Override
  public void refresh() {

    final String value = getSimpleValueOrDefault();

    if (StringUtils.isNotBlank(value)) {
      Date date;
      try {
        date = sdf.parse(value);
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(date);

        // set fields
        calendarControl.setYear(calendar.get(Calendar.YEAR));
        calendarControl.setMonth(calendar.get(Calendar.MONTH));
        calendarControl.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        calendarControl.setHours(calendar.get(Calendar.HOUR_OF_DAY));
        calendarControl.setMinutes(calendar.get(Calendar.MINUTE));
        calendarControl.setSeconds(calendar.get(Calendar.SECOND));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
  }
  @Override
  public String getSimpleValue() {
    final Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(Calendar.YEAR, calendarControl.getYear());
    calendar.set(Calendar.MONTH, calendarControl.getMonth());
    calendar.set(Calendar.DAY_OF_MONTH, calendarControl.getDay());
    calendar.set(Calendar.HOUR_OF_DAY, calendarControl.getHours());
    calendar.set(Calendar.MINUTE, calendarControl.getMinutes());
    calendar.set(Calendar.SECOND, calendarControl.getSeconds());
    return sdf.format(calendar.getTime());
  }

  @Override
  public Composite render(final Composite parent, final TabbedPropertySheetWidgetFactory factory, final FocusListener listener) {

    final Composite result = factory.createFlatFormComposite(parent);
    FormData data;

    int controlStyle = SWT.CALENDAR;

    final DatePickerProperty datePickerPropertyAnnotation = getField().getAnnotation(DatePickerProperty.class);
    if (datePickerPropertyAnnotation != null) {
      sdf = new SimpleDateFormat(datePickerPropertyAnnotation.dateTimePattern());
      controlStyle = datePickerPropertyAnnotation.swtStyle();
    } else {
      sdf = new SimpleDateFormat(DatePickerProperty.DEFAULT_DATE_TIME_PATTERN);
      controlStyle = DatePickerProperty.DEFAULT_DATE_TIME_CONTROL_STYLE;
    }

    calendarControl = new DateTime(result, controlStyle | SWT.BORDER_SOLID);
    calendarControl.setEnabled(true);

    if (getPropertyAnnotation().required()) {
      addFieldValidator(calendarControl, RequiredFieldValidator.class);
    }

    if (getPropertyAnnotation().fieldValidator() != null) {
      addFieldValidator(calendarControl, getPropertyAnnotation().fieldValidator());
    }

    calendarControl.addFocusListener(listener);

    data = new FormData();
    data.left = new FormAttachment(0);
    data.top = new FormAttachment(0);
    data.right = new FormAttachment(100);
    calendarControl.setLayoutData(data);

    return result;
  }
}
