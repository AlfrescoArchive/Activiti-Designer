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
package org.activiti.designer.eclipse.common;

import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Font;

/**
 * The Class ColoredFont.
 */
public class ColoredFont {

	private Font font;
	private Color color;

	/**
	 * Instantiates a new colored font.
	 * 
	 * @param font
	 *            the font
	 * @param color
	 *            the color
	 */
	public ColoredFont(Font font, Color color) {
		super();
		setFont(font);
		setColor(color);
	}

	/**
	 * Gets the color.
	 * 
	 * @return Returns the color.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Gets the font.
	 * 
	 * @return Returns the font.
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets the color.
	 * 
	 * @param color
	 *            The color to set.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Sets the font.
	 * 
	 * @param font
	 *            The font to set.
	 */
	public void setFont(Font font) {
		this.font = font;
	}

}
