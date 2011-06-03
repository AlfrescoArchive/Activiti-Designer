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

import org.activiti.designer.integration.servicetask.PropertyType;
import org.eclipse.bpmn2.ComplexDataType;
import org.eclipse.bpmn2.CustomProperty;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * Component that holds reference to a created control or composite control for
 * a {@link CustomProperty} in the property section.
 * 
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 */
public interface CustomPropertyField {

  Composite render(Composite parent, TabbedPropertySheetWidgetFactory factory, FocusListener listener);

  PropertyType getPrimaryPropertyType();

  String getCustomPropertyId();

  /**
   * Get the simple value for the field.
   */
  String getSimpleValue();

  /**
   * Get the complex value for the field.
   */
  ComplexDataType getComplexValue();

  /**
   * Returns whether the field contains a complex value.
   */
  boolean isComplex();

  /**
   * Refresh the control(s) for the field. Restores values from the model to the
   * control(s).
   */
  void refresh();

  /**
   * Validate the control(s) for the field.
   */
  void validate();

}
