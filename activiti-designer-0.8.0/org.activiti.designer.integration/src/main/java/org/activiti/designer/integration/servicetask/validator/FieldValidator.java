/**
 * 
 */
package org.activiti.designer.integration.servicetask.validator;

import org.eclipse.swt.widgets.Control;

/**
 * Interface to validate field values. Primarily intended for simple fields,
 * such as text fields that need a quick verification
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public interface FieldValidator {

	void validate(Control control) throws ValidationException;

}
