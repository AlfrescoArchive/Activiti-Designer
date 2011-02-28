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
package org.activiti.designer.eclipse.outline.tree;

import org.eclipse.graphiti.mm.algorithms.styles.Color;

/**
 * A default-implementation for a TreeEditPart, which wraps a Shape. It can be
 * overwritten to provide different behaviour.
 */
public class ColorTreeEditPart extends AbstractGraphicsTreeEditPart {

	/**
	 * Creates a new PictogramElementTreeEditPart for the given model Object.
	 * 
	 * @param configurationProvider
	 *            The IConfigurationProvider which defines the model
	 * @param shape
	 *            The Shape of this EditPart.
	 */
	public ColorTreeEditPart(Color color) {
		super(color);
	}

	public Color getColor() {
		return (Color) getModel();
	}

	// ======================= overwriteable behaviour ========================

	/**
	 * Creates the EditPolicies of this EditPart. Subclasses often overwrite
	 * this method to change the behaviour of the editpart.
	 */
	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected String getText() {
		return super.getText() + " - (" + getColor().getRed() + ", " + getColor().getGreen() + ", " + getColor().getBlue() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}