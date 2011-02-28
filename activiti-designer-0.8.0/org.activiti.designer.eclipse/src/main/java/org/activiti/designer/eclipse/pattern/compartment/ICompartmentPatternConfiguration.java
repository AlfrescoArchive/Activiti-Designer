/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.eclipse.pattern.compartment;

import org.eclipse.graphiti.pattern.config.IColorConfiguration;
import org.eclipse.graphiti.pattern.config.IIndentConfiguration;
import org.eclipse.graphiti.pattern.config.IMinimumSizeConfiguration;

/**
 * The Interface ICompartmentPatternConfiguration.
 */
public interface ICompartmentPatternConfiguration extends IColorConfiguration, IIndentConfiguration, IMinimumSizeConfiguration {

	/**
	 * Sets the line width.
	 * 
	 * @param lineWidth
	 *            the lineWidth to set
	 */
	void setLineWidth(int lineWidth);

	/**
	 * Gets the line width.
	 * 
	 * @return the lineWidth
	 */
	int getLineWidth();

	/**
	 * Gets the corner height.
	 * 
	 * @return the corner height
	 */
	int getCornerHeight();

	/**
	 * Gets the corner width.
	 * 
	 * @return the corner width
	 */
	int getCornerWidth();

	/**
	 * Sets the corner height.
	 * 
	 * @param i
	 *            the new corner height
	 */
	void setCornerHeight(int i);

	/**
	 * Sets the corner width.
	 * 
	 * @param i
	 *            the new corner width
	 */
	void setCornerWidth(int i);

	/**
	 * Sets the header image visible.
	 * 
	 * @param isHeaderImageVisible
	 *            the is header image visible
	 */
	void setHeaderImageVisible(boolean isHeaderImageVisible);

	/**
	 * Checks if is header image visible.
	 * 
	 * @return true, if is header image visible
	 */
	boolean isHeaderImageVisible();
}
