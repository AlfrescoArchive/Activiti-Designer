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
package org.activiti.designer.property.extension.field.validator;

import org.activiti.designer.integration.validator.FieldValidator;
import org.activiti.designer.integration.validator.ValidationException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

/**
 * {@link FieldValidator} that validates required period fields. Supports
 * {@link Composite} controls.
 * 
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 * 
 */
public class PeriodRequiredFieldValidator implements FieldValidator {

  public PeriodRequiredFieldValidator() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.activiti.designer.integration.servicetask.FieldValidator#validate
   * (java.lang.Object)
   */
  @Override
  public void validate(Control control) throws ValidationException {

    Composite parent = null;
    if (control instanceof Composite) {
      parent = (Composite) control;
    }

    if (parent == null) {
      throw new ValidationException(
              "Unable to validate control because it is not a period's parent composite. This probably means the process node was incorrectly configured");
    }

    boolean atLeastOneSpecified = false;

    for (final Control currentControl : parent.getChildren()) {
      if (currentControl instanceof Spinner) {
        final int elementValue = ((Spinner) currentControl).getSelection();
        if (elementValue != 0) {
          atLeastOneSpecified = true;
          break;
        }
      }
    }

    if (!atLeastOneSpecified) {
      throw new ValidationException("This field is required");
    }
  }
}