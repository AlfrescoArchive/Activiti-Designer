/**
 * 
 */
package org.activiti.designer.property.custom;

/**
 * @author Tiese Barrell
 * @since 0.6.0
 * @version 1
 * 
 */
public enum PeriodPropertyElement {

	YEAR("y", "Year(s)", 0), MONTH("mo", "Month(s)", 1), WEEK("w", "Week(s)", 2), DAY("d", "Day(s)", 3), HOUR("h",
			"Hour(s)", 4), MINUTE("m", "Minute(s)", 5), SECOND("s", "Second(s)", 6);

	private final String shortFormat;
	private final String longFormat;
	private final int order;

	/**
	 * Constructs a new {@link PeriodPropertyElement}.
	 * 
	 * @param shortFormat
	 *            the short format to display
	 * @param order
	 *            the position in the storage order
	 */
	private PeriodPropertyElement(final String shortFormat, final String longFormat, final int order) {
		this.shortFormat = shortFormat;
		this.longFormat = longFormat;
		this.order = order;
	}

	/**
	 * @return the shortFormat
	 */
	public String getShortFormat() {
		return shortFormat;
	}

	/**
	 * @return the longFormat
	 */
	public String getLongFormat() {
		return longFormat;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	public static final PeriodPropertyElement byShortFormat(String shortFormat) {
		for (final PeriodPropertyElement element : values()) {
			if (element.getShortFormat().equalsIgnoreCase(shortFormat)) {
				return element;
			}
		}

		return null;
	}

}
