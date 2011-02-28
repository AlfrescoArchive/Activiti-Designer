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
package org.activiti.designer.eclipse.pattern.grid;

import org.eclipse.graphiti.pattern.config.IColorConfiguration;
import org.eclipse.graphiti.pattern.config.IMinimumSizeConfiguration;

/**
 * The Interface IGridPatternConfiguration.
 */
public interface IGridPatternConfiguration extends IColorConfiguration, IMinimumSizeConfiguration {

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
	 * Sets the minor separator width.
	 * 
	 * @param minorSeparatorWidth
	 *            the new minor separator width
	 */
	void setMinorSeparatorWidth(int minorSeparatorWidth);

	/**
	 * Gets the minor separator width.
	 * 
	 * @return the minor separator width
	 */
	int getMinorSeparatorWidth();

	/**
	 * Sets the major unit separator width.
	 * 
	 * @param majorUnitSeparatorWidth
	 *            the new major unit separator width
	 */
	void setMajorUnitSeparatorWidth(int majorUnitSeparatorWidth);

	/**
	 * Gets the major unit separator width.
	 * 
	 * @return the major unit separator width
	 */
	int getMajorUnitSeparatorWidth();

	/**
	 * Sets the major unit x.
	 * 
	 * @param majorUnitX
	 *            the new major unit x
	 */
	void setMajorUnitX(int majorUnitX);

	/**
	 * Gets the major unit x.
	 * 
	 * @return the major unit x
	 */
	int getMajorUnitX();

	/**
	 * Sets the major unit y.
	 * 
	 * @param majorUnitY
	 *            the new major unit y
	 */
	void setMajorUnitY(int majorUnitY);

	/**
	 * Gets the major unit y.
	 * 
	 * @return the major unit y
	 */
	int getMajorUnitY();

}
