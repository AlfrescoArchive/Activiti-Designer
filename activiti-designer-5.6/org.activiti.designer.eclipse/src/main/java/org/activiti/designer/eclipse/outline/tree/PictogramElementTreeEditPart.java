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

import org.eclipse.graphiti.mm.StyleContainer;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * A default-implementation for a TreeEditPart, which wraps a Shape. It can be
 * overwritten to provide different behaviour.
 */
public class PictogramElementTreeEditPart extends AbstractGraphicsTreeEditPart {

	/**
	 * Creates a new PictogramElementTreeEditPart for the given model Object.
	 * 
	 * @param configurationProvider
	 *            The IConfigurationProvider which defines the model
	 * @param shape
	 *            The Shape of this EditPart.
	 */
	public PictogramElementTreeEditPart(PictogramElement pictogramElement) {
		super(pictogramElement);
	}

	/**
	 * Returns the Shape of this EditPart
	 * 
	 * @return The Shape of this EditPart
	 */
	public PictogramElement getPictogramElement() {
		return (PictogramElement) getModel();
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
		PictogramElement pictogramElement = getPictogramElement();

		if (pictogramElement instanceof ContainerShape) {
			ContainerShape containerShape = (ContainerShape) pictogramElement;
			addAllElementsIfNotNull(retList, containerShape.getChildren());
		}
		if (pictogramElement instanceof AnchorContainer) {
			AnchorContainer notAnAnchorElement = (AnchorContainer) pictogramElement;
			addAllElementsIfNotNull(retList, notAnAnchorElement.getAnchors());
		}
		if (pictogramElement instanceof Connection) {
			Connection connection = (Connection) pictogramElement;
			Collection<ConnectionDecorator> connectionDecorators = connection.getConnectionDecorators();
			addAllElementsIfNotNull(retList, connectionDecorators);
		}
		if (pictogramElement instanceof FreeFormConnection) {
			FreeFormConnection connection = (FreeFormConnection) pictogramElement;
			Collection<Point> bendpoints = connection.getBendpoints();
			addAllElementsIfNotNull(retList, bendpoints);
		}
		if (pictogramElement instanceof Diagram) {
			Diagram diagram = (Diagram) pictogramElement;
			addAllElementsIfNotNull(retList, diagram.getConnections());
			Collection<Color> colors = diagram.getColors();
			addAllElementsIfNotNull(retList, colors);
		}

		if (pictogramElement instanceof StyleContainer) {
			StyleContainer styleContainer = (StyleContainer) pictogramElement;
			Collection<Style> styles = styleContainer.getStyles();
			addAllElementsIfNotNull(retList, styles);
		}
		if (pictogramElement != null) {
			GraphicsAlgorithm graphicsAlgorithm = pictogramElement.getGraphicsAlgorithm();
			if (graphicsAlgorithm != null) {
				retList.add(graphicsAlgorithm);
			}
		}
		return retList;
	}
}