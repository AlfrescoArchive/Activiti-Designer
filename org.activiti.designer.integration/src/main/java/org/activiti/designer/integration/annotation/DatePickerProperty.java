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
/**
 * 
 */
package org.activiti.designer.integration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;

import org.activiti.designer.integration.servicetask.PropertyType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;

/**
 * Defines attributes for {@link PropertyType#DATE_PICKER} fields.
 * 
 * @author Tiese Barrell
 * @version 1
 * @since 0.7.0
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DatePickerProperty {

  public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
  public static final int DEFAULT_DATE_TIME_CONTROL_STYLE = SWT.CALENDAR;

  /**
   * The pattern for the date time to be stored in the output. This pattern is
   * used to instruct {@link SimpleDateFormat} and should therefore be supported
   * by it.
   * 
   * @see {@link SimpleDateFormat}
   */
  abstract String dateTimePattern() default DEFAULT_DATE_TIME_PATTERN;

  /**
   * The style to be used for the {@link DateTime} control. Only use values
   * supported by the {@link DateTime} control, such as {@link SWT#CALENDAR},
   * {@link SWT#DATE}.
   * 
   * @see {@link DateTime}
   * @see {@literal http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/api/org/eclipse/swt/widgets/DateTime.html}
   * 
   */
  abstract int swtStyle() default DEFAULT_DATE_TIME_CONTROL_STYLE;

}
