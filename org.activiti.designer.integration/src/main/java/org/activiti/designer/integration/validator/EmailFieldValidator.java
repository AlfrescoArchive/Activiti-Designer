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
package org.activiti.designer.integration.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * {@link FieldValidator} that validates email addresses. Supports {@link Text}
 * controls.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public class EmailFieldValidator implements FieldValidator {

	private static final String EMAIL_PATTERN = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b";

	private final Pattern pattern;

	public EmailFieldValidator() {
		pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.activiti.designer.integration.servicetask.FieldValidator#validate
	 * (java.lang.Object)
	 */
	@Override
	public void validate(Control control) throws ValidationException {

		String fieldValue = null;
		if (control instanceof Text) {
			fieldValue = ((Text) control).getText();
		}

		if (fieldValue != null) {

			Matcher m = pattern.matcher(fieldValue);
			if (!m.matches()) {
				throw new ValidationException(String.format(
						"The value '%s' is not a valid email address. Addresses should match the pattern %s ",
						fieldValue, EMAIL_PATTERN));
			}

		} else {
			throw new ValidationException("The value provided must be of type string from a Text control");
		}
	}

}
