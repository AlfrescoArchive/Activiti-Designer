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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.graphiti.mm.algorithms.styles.Style;

/**
 * A default-implementation for a TreeEditPart, which wraps a Shape. It can be
 * overwritten to provide different behaviour.
 */
public class StyleTreeEditPart extends AbstractGraphicsTreeEditPart {

	/**
	 * Creates a new PictogramElementTreeEditPart for the given model Object.
	 * 
	 * @param configurationProvider
	 *            The IConfigurationProvider which defines the model
	 * @param shape
	 *            The Shape of this EditPart.
	 */
	public StyleTreeEditPart(Style style) {
		super(style);
	}

	public Style getStyle() {
		return (Style) getModel();
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
		return super.getText() + " - (" + getStyle().getId() + " // " + getStyle().getDescription() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	protected List<Object> getModelChildren() {
		List<Object> retList = new ArrayList<Object>();
		Style style = getStyle();
		Collection<Style> childStyles = style.getStyles();
		retList.addAll(childStyles);
		// retList.add(style.getForeground());
		// retList.add(style.getBackground());
		// retList.add(style.getFont());
		return retList;
	}
}