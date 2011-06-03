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

package org.activiti.designer.property.extension.util;

import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.property.custom.PeriodPropertyElement;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

/**
 * Provides utilities for extension property editing.
 * 
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 */
public final class ExtensionPropertyUtil {

  private ExtensionPropertyUtil() {

  }

  /**
   * Inspects the provided parent component and extracts the value for the
   * {@link PropertyType#PERIOD} enclosed.
   * 
   * @param parent
   *          the parent component
   */
  public static final String getPeriodValueFromParent(final Composite parent) {

    String[] values = new String[PeriodPropertyElement.values().length];

    for (final Control control : parent.getChildren()) {
      if (control instanceof Spinner) {
        final String periodKey = (String) control.getData("PERIOD_KEY");
        final PeriodPropertyElement element = PeriodPropertyElement.byShortFormat(periodKey);

        if (element != null) {
          final int elementValue = ((Spinner) control).getSelection();
          final String elementStringValue = elementValue + element.getShortFormat();
          values[element.getOrder()] = elementStringValue;
        }
      }
    }

    final StringBuilder builder = new StringBuilder();
    for (int i = 0; i < values.length; i++) {
      builder.append(values[i]);
      if (i != values.length - 1) {
        builder.append(" ");
      }
    }
    String value = builder.toString();
    return value;
  }

  /**
   * Inspects the provided value and extracts the value for the
   * {@link PeriodPropertyElement} provided.
   * 
   * @param parent
   *          the parent component
   */
  public static final int getPeriodPropertyElementFromValue(final String value, final PeriodPropertyElement propertyElement) {

    int result = 0;
    final String[] elementValues = value.split(" ");

    if (propertyElement != null) {
      final String stripped = StringUtils.substringBeforeLast(elementValues[propertyElement.getOrder()], propertyElement.getShortFormat());
      result = Integer.parseInt(stripped);
    }
    return result;
  }
}
