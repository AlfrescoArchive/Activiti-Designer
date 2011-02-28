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

import org.eclipse.graphiti.util.IColorConstant;

/**
 * The Class DefaultCompartmentPatternConfiguration.
 */
public class DefaultCompartmentPatternConfiguration implements ICompartmentPatternConfiguration {

	private int lineWidth = 1;

	private int minimumHeight = 40;

	private int minimumWidth = 25;

	private int outerIndentTop = 2;

	private int outerIndentBottom = 2;

	private int outerIndentLeft = 5;

	private int outerIndentRight = 5;

	private IColorConstant foregroundColor = IColorConstant.BLACK;

	private IColorConstant backgroundColor = IColorConstant.WHITE;

	private IColorConstant textColor = IColorConstant.BLACK;

	private double transparency = 0;

	private int cornerHeight = 5;

	private int cornerWidth = 5;

	private boolean isHeaderImageVisible = false;

	public IColorConstant getBackgroundColor() {
		return backgroundColor;
	}

	public int getCornerHeight() {
		return cornerHeight;
	}

	public int getCornerWidth() {
		return cornerWidth;
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

	public int getOuterIndentBottom() {
		return outerIndentBottom;
	}

	public int getOuterIndentLeft() {
		return outerIndentLeft;
	}

	public int getOuterIndentRight() {
		return outerIndentRight;
	}

	public int getOuterIndentTop() {
		return outerIndentTop;
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

	public void setOuterIndentBottom(int outerIndentBottom) {
		this.outerIndentBottom = outerIndentBottom;
	}

	public void setOuterIndentLeft(int outerIndentLeft) {
		this.outerIndentLeft = outerIndentLeft;
	}

	public void setOuterIndentRight(int outerIndentRight) {
		this.outerIndentRight = outerIndentRight;
	}

	public void setOuterIndentTop(int outerIndentTop) {
		this.outerIndentTop = outerIndentTop;
	}

	public void setTextColor(IColorConstant color) {
		textColor = color;
	}

	public void setCornerHeight(int cornerHeight) {
		this.cornerHeight = cornerHeight;
	}

	public void setCornerWidth(int cornerWidth) {
		this.cornerWidth = cornerWidth;
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
	 * @seeorg.eclipse.graphiti.examples.common.pattern.compartment.
	 * ICompartmentPatternConfiguration#isHeaderImageVisible()
	 */
	public boolean isHeaderImageVisible() {
		return isHeaderImageVisible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.graphiti.examples.common.pattern.compartment.
	 * ICompartmentPatternConfiguration#setHeaderImageVisible(boolean)
	 */
	public void setHeaderImageVisible(boolean isHeaderImageVisible) {
		this.isHeaderImageVisible = isHeaderImageVisible;
	}
}
