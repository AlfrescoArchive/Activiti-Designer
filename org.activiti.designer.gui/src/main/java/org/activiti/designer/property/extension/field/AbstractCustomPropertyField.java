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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.designer.bpmn2.model.ComplexDataType;
import org.activiti.designer.bpmn2.model.CustomProperty;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.integration.servicetask.validator.FieldValidator;
import org.activiti.designer.integration.servicetask.validator.ValidationException;
import org.activiti.designer.property.PropertyCustomServiceTaskSection;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Abstract base class for {@link CustomPropertyField}s.
 * 
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 */
public abstract class AbstractCustomPropertyField implements CustomPropertyField {

  private static final RGB ERROR_COLOR = new RGB(255, 220, 220);

  private Map<Control, List<FieldValidator>> validators = new HashMap<Control, List<FieldValidator>>();

  private final PropertyCustomServiceTaskSection section;
  private final ServiceTask serviceTask;
  private final String customPropertyId;

  private final Field field;

  private final Property propertyAnnotation;
  private final Help helpAnnotation;

  public AbstractCustomPropertyField(final PropertyCustomServiceTaskSection section, final ServiceTask serviceTask, final Field field) {
    this.section = section;
    this.serviceTask = serviceTask;
    this.customPropertyId = field.getName();
    this.field = field;

    // Read and save references to annotations to be used later
    this.propertyAnnotation = field.getAnnotation(Property.class);
    this.helpAnnotation = field.getAnnotation(Help.class);
  }
  @Override
  public String getCustomPropertyId() {
    return this.customPropertyId;
  }

  @Override
  public String getSimpleValue() {
    return null;
  }
  @Override
  public ComplexDataType getComplexValue() {
    return null;
  }

  @Override
  public boolean isComplex() {
    return false;
  }

  @Override
  public void validate() {
    for (final Entry<Control, List<FieldValidator>> controlValidator : this.validators.entrySet()) {
      if (!controlValidator.getKey().isDisposed()) {
        for (final FieldValidator validator : controlValidator.getValue()) {
          try {
            validator.validate(controlValidator.getKey());
            handleRemoveExceptionForControl(controlValidator.getKey());
          } catch (ValidationException e) {
            handleAddExceptionForControl(controlValidator.getKey(), e);
          }
        }
      }
    }
  }

  protected void addFieldValidator(final Control control, final Class< ? extends FieldValidator> fieldValidatorClass) {

    if (!validators.containsKey(control)) {
      validators.put(control, new ArrayList<FieldValidator>());
    }

    try {
      this.validators.get(control).add(fieldValidatorClass.newInstance());
    } catch (Exception e) {
      // TODO: handle
      // fail silently
    }

  }

  protected String getSimpleValueFromModel() {
    String result = null;
    final CustomProperty property = ExtensionUtil.getCustomProperty(serviceTask, customPropertyId);
    if (property != null) {
      final String propertyValue = property.getSimpleValue();
      if (propertyValue != null) {
        result = propertyValue;
      }
    }
    return result;
  }

  /**
   * Extends {@link #getSimpleValueFromModel()} by evaluating if the field has a
   * default value configured and providing that if there is no value stored in
   * the model (i.e., {@link #getSimpleValueFromModel()} returns null)s.
   * 
   * @return the result of {@link #getSimpleValueFromModel()} if it exists,
   *         otherwise the default value for the field if one exists, an empty
   *         string otherwise. This method will never return null.
   */
  protected String getSimpleValueOrDefault() {
    String result = getSimpleValueFromModel();
    if (result == null && StringUtils.isNotBlank(getPropertyAnnotation().defaultValue())) {
      result = getPropertyAnnotation().defaultValue();
    } else if (result == null) {
      result = "";
    }
    return result;
  }

  protected ComplexDataType getComplexValueFromModel() {
    ComplexDataType result = null;
    final CustomProperty property = ExtensionUtil.getCustomProperty(serviceTask, customPropertyId);
    if (property != null) {
      final ComplexDataType propertyValue = property.getComplexValue();
      if (propertyValue != null) {
        result = propertyValue;
      }
    }
    return result;
  }

  protected Property getPropertyAnnotation() {
    return propertyAnnotation;
  }

  protected Help getHelpAnnotation() {
    return helpAnnotation;
  }

  protected Field getField() {
    return this.field;
  }

  protected PropertyCustomServiceTaskSection getSection() {
    return section;
  }

  protected void runModelChange(final Runnable runnable) {
    getSection().runModelChange(runnable);
  }

  private void handleAddExceptionForControl(final Control control, final ValidationException e) {

    if (control instanceof Text) {
      Text text = (Text) control;
      text.setBackground(new Color(control.getDisplay(), ERROR_COLOR));
      text.setToolTipText(e.getMessage());
    } else if (control instanceof CCombo) {
      CCombo combo = (CCombo) control;
      combo.setBackground(new Color(control.getDisplay(), ERROR_COLOR));
      combo.setToolTipText(e.getMessage());
    } else if (control instanceof Composite) {
      Composite composite = (Composite) control;
      composite.setBackground(new Color(control.getDisplay(), ERROR_COLOR));
      for (final Control childControl : composite.getChildren()) {
        childControl.setBackground(new Color(control.getDisplay(), ERROR_COLOR));
      }
      composite.setToolTipText(e.getMessage());
    }
  }

  private void handleRemoveExceptionForControl(final Control control) {
    if (control instanceof Text) {
      Text text = (Text) control;
      text.setBackground(null);
      text.setToolTipText(null);
    } else if (control instanceof CCombo) {
      CCombo combo = (CCombo) control;
      combo.setBackground(null);
      combo.setToolTipText(null);
    } else if (control instanceof Composite) {
      Composite composite = (Composite) control;
      composite.setBackground(null);
      for (final Control childControl : composite.getChildren()) {
        childControl.setBackground(null);
      }
      composite.setToolTipText(null);
    }
  }

}
