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

import org.eclipse.graphiti.util.IColorConstant;

/**
 * The Class DefaultGridPatternConfiguration.
 */
public class DefaultGridPatternConfiguration implements IGridPatternConfiguration {

	private int lineWidth = 1;

	private int minimumHeight = 40;

	private int minimumWidth = 40;

	private IColorConstant foregroundColor = IColorConstant.BLACK;

	private IColorConstant backgroundColor = IColorConstant.WHITE;

	private IColorConstant textColor = IColorConstant.BLACK;

	private double transparency = 0;

	private int minorUnitSeparatorWidth = 2;

	private int majorUnitSeparatorWidth = 4;

	private int majorUnitX = 0;

	private int majorUnitY = 0;

	public IColorConstant getBackgroundColor() {
		return backgroundColor;
	}

	public IColorConstant getForegroundColor() {
		return foregroundColor;
	}

	/**
	 * Gets the line width.
	 * 
	 * @return the lineWidth
	 */
	public int getLineWidth() {
		return lineWidth;
	}

	public int getMinimumHeight() {
		return minimumHeight;
	}

	public int getMinimumWidth() {
		return minimumWidth;
	}

	public IColorConstant getTextColor() {
		return textColor;
	}

	public double getTransparency() {
		return transparency;
	}

	public void setBackgroundColor(IColorConstant color) {
		backgroundColor = color;
	}

	public void setForegroundColor(IColorConstant color) {
		foregroundColor = color;
	}

	/**
	 * Sets the line width.
	 * 
	 * @param lineWidth
	 *            the lineWidth to set
	 */
	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public void setMinimumHeight(int minimumHeight) {
		this.minimumHeight = minimumHeight;
	}

	public void setMinimumWidth(int minimumWidth) {
		this.minimumWidth = minimumWidth;
	}

	public void setTextColor(IColorConstant color) {
		textColor = color;
	}

	/**
	 * Sets the transparency.
	 * 
	 * @param transparency
	 *            the new transparency
	 */
	public void setTransparency(double transparency) {
		this.transparency = transparency;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.examples.common.pattern.grid.IGridPatternConfiguration
	 * #getMajorUnitSeparatorWidth()
	 */
	public int getMajorUnitSeparatorWidth() {
		return majorUnitSeparatorWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.examples.common.pattern.grid.IGridPatternConfiguration
	 * #getMajorUnitX()
	 */
	public int getMajorUnitX() {
		return majorUnitX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.examples.common.pattern.grid.IGridPatternConfiguration
	 * #getMajorUnitY()
	 */
	public int getMajorUnitY() {
		return majorUnitY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.examples.common.pattern.grid.IGridPatternConfiguration
	 * #getMinorSeparatorWidth()
	 */
	public int getMinorSeparatorWidth() {
		return minorUnitSeparatorWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.examples.common.pattern.grid.IGridPatternConfiguration
	 * #setMajorUnitSeparatorWidth(int)
	 */
	public void setMajorUnitSeparatorWidth(int majorUnitSeparatorWidth) {
		this.majorUnitSeparatorWidth = majorUnitSeparatorWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.examples.common.pattern.grid.IGridPatternConfiguration
	 * #setMajorUnitX(int)
	 */
	public void setMajorUnitX(int majorUnitX) {
		this.majorUnitX = majorUnitX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.examples.common.pattern.grid.IGridPatternConfiguration
	 * #setMajorUnitY(int)
	 */
	public void setMajorUnitY(int majorUnitY) {
		this.majorUnitY = majorUnitY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.examples.common.pattern.grid.IGridPatternConfiguration
	 * #setMinorSeparatorWidth(int)
	 */
	public void setMinorSeparatorWidth(int minorSeparatorWidth) {
		this.minorUnitSeparatorWidth = minorSeparatorWidth;
	}
}
