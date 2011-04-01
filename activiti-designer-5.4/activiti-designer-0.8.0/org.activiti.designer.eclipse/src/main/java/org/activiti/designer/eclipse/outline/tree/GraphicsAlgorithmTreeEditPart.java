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
import java.util.List;

import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;

/**
 * A default-implementation for a TreeEditPart, which wraps a Shape. It can be
 * overwritten to provide different behaviour.
 */
public class GraphicsAlgorithmTreeEditPart extends AbstractGraphicsTreeEditPart {

	/**
	 * Creates a new PictogramElementTreeEditPart for the given model Object.
	 * 
	 * @param configurationProvider
	 *            The IConfigurationProvider which defines the model
	 * @param shape
	 *            The Shape of this EditPart.
	 */
	public GraphicsAlgorithmTreeEditPart(GraphicsAlgorithm graphicsAlgorithm) {
		super(graphicsAlgorithm);
	}

	/**
	 * Returns the Shape of this EditPart
	 * 
	 * @return The Shape of this EditPart
	 */
	public GraphicsAlgorithm getGraphicsAlgorithm() {
		return (GraphicsAlgorithm) getModel();
	}

	// ======================= overwriteable behaviour ========================

	/**
	 * Creates the EditPolicies of this EditPart. Subclasses often overwrite
	 * this method to change the behaviour of the editpart.
	 */
	@Override
	protected void createEditPolicies() {
	}

	/**
	 * Returns the children of this EditPart.
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@Override
	protected List<Object> getModelChildren() {
		List<Object> retList = new ArrayList<Object>();
		GraphicsAlgorithm graphicsAlgorithm = getGraphicsAlgorithm();
		if (graphicsAlgorithm != null && graphicsAlgorithm.eResource() != null) {
			final List<GraphicsAlgorithm> graphicsAlgorithmChildren = graphicsAlgorithm.getGraphicsAlgorithmChildren();
			addAllElementsIfNotNull(retList, graphicsAlgorithmChildren);
			if (graphicsAlgorithm instanceof Polyline) {
				Polyline pl = (Polyline) graphicsAlgorithm;
				addAllElementsIfNotNull(retList, pl.getPoints());
			}
		}
		return retList;
	}
}