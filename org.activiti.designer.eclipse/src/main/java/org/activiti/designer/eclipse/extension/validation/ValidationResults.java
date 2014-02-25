package org.activiti.designer.eclipse.extension.validation;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.BaseElement;

/**
 * Process verification results listing
 * 
 * @author Jurosh
 *
 */
public class ValidationResults {
	
	// TODO use just numbers, no need to store string
	public static final String TYPE_INFO = "Information";
	public static final String TYPE_ERROR = "Error";
	public static final String TYPE_WARNING = "Warning";
	
	// TODO create levels

	private List<ValidationResult> results = new ArrayList<ValidationResult>();
	
	public void add(ValidationResult result) {
		results.add(result);
	}
	
	public List<ValidationResult> getResults() {
		return results;
	}
	
	/**
	 * Validation result wrapper
	 * 
	 * @author Jurosh
	 *
	 */
	public static class ValidationResult {
	
		private String type;
		private String reason;
		
		private BaseElement element1;
		private BaseElement element2;
		private BaseElement[] elements;
	
		public ValidationResult(String type, String reason, BaseElement... element) {
			this.type = type;
			this.reason = reason;
			this.elements = element;
			this.element1 = element[0];
//			this.element2 = element[1];
			// TODO use all elements
		}
	
		@Override
		public String toString() {
			return type + " " + reason;
		}
	
		public String getType() {
			return type;
		}
	
		public String getElement1Id() {
			return element1 == null ? "" : element1.getId();
		}
	
		public String getElement2Id() {
			return element2 == null ? "" : element2.getId();
		}
	
		public String getReason() {
			return reason;
		}

		public BaseElement getElement() {
			return element1;
		}
	
	}
}
