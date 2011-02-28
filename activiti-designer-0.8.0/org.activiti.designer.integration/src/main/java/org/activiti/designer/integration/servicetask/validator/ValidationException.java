/**
 * 
 */
package org.activiti.designer.integration.servicetask.validator;

/**
 * @author a139923
 * 
 */
public class ValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1862271923772265577L;

	/**
	 * 
	 */
	public ValidationException() {
	}

	/**
	 * @param message
	 */
	public ValidationException(String message) {
		super(message);
	}

	/**
	 * @param throwable
	 */
	public ValidationException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param message
	 * @param throwable
	 */
	public ValidationException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
