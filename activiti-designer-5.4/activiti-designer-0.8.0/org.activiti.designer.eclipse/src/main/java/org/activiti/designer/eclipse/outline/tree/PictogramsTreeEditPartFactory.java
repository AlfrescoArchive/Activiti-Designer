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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * A concrete implementation of the interface IEditPartFactory for Trees, which
 * works on a pictogram model.
 */
public class PictogramsTreeEditPartFactory implements EditPartFactory {

	/**
	 * Creates a new PictogramsEditPartFactory.
	 */
	public PictogramsTreeEditPartFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart,
	 * java.lang.Object)
	 */
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart ret = null;
		if (model instanceof PictogramElement) {
			ret = new PictogramElementTreeEditPart((PictogramElement) model);
		} else if (model instanceof GraphicsAlgorithm) {
			ret = new GraphicsAlgorithmTreeEditPart((GraphicsAlgorithm) model);
		} else if (model instanceof ConnectionDecorator) {
			ret = new ConnectionDecoratorTreeEditPart((ConnectionDecorator) model);
		} else if (model instanceof Point) {
			ret = new PointTreeEditPart((Point) model);
		} else if (model instanceof Color) {
			ret = new ColorTreeEditPart((Color) model);
		} else if (model instanceof Style) {
			ret = new StyleTreeEditPart((Style) model);
		}
		return ret;
	}
}