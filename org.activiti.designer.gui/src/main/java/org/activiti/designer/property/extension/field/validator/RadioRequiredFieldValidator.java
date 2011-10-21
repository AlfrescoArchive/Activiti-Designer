package org.activiti.designer.property.extension.field.validator;

import org.activiti.designer.integration.servicetask.validator.FieldValidator;
import org.activiti.designer.integration.servicetask.validator.ValidationException;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * {@link FieldValidator} that validates required radio fields. Supports
 * {@link Composite} controls.
 * 
 * @author Tiese Barrell
 * @since 0.7.0
 * @version 1
 * 
 */
public class RadioRequiredFieldValidator implements FieldValidator {

  public RadioRequiredFieldValidator() {
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
              "Unable to validate control because it is not a radio's parent composite. This probably means the process node was incorrectly configured");
    }

    boolean selectionApplied = false;

    for (final Control currentControl : parent.getChildren()) {
      if (currentControl instanceof Button) {
        if (((Button) currentControl).getSelection()) {
          selectionApplied = true;
          break;
        }
      }
    }

    if (!selectionApplied) {
      throw new ValidationException("This field is required");
    }
  }
}