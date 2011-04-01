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

import java.util.List;

import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.mapping.ILinkCreationInfo;
import org.eclipse.graphiti.pattern.mapping.IStructureMappingGrid;
import org.eclipse.graphiti.pattern.mapping.IStructureMappingMulti;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

/**
 * The Class GridPattern.
 */
public abstract class GridPattern extends AbstractPattern {

	private IStructureMappingGrid gridMapping;

	/**
	 * Instantiates a new grid pattern.
	 */
	public GridPattern() {
		super(new DefaultGridPatternConfiguration());
	}

	@Override
	final public PictogramElement add(IAddContext context) {
		Object mainBusinessObject = context.getNewObject();
		ContainerShape parentContainerShape = context.getTargetContainer();

		// CONTAINER SHAPE WITH RECTANGLE
		ContainerShape containerShape = Graphiti.getPeCreateService().createContainerShape(parentContainerShape, true);

		// check whether valid size is available, e.g. if called from the create
		// feature
		int minWidth = getConfiguration().getMinimumWidth();
		int minHeight = getConfiguration().getMinimumHeight();

		int width = context.getWidth() <= minWidth ? minWidth : context.getWidth();
		int height = context.getHeight() <= minHeight ? minHeight : context.getHeight();

		IGaService gaService = Graphiti.getGaService();
		{
			// create and set graphics algorithm
			Rectangle rectangle = gaService.createRectangle(containerShape);
			rectangle.setForeground(manageColor(getConfiguration().getForegroundColor()));
			rectangle.setBackground(manageColor(getConfiguration().getBackgroundColor()));
			rectangle.setLineWidth(getConfiguration().getLineWidth() * 2);
			rectangle.setTransparency(getConfiguration().getTransparency());
			gaService.setLocationAndSize(rectangle, context.getX(), context.getY(), width, height);

			// create link and wire it
			link(containerShape, mainBusinessObject);

			// create cell separators
			createSeparators(rectangle, getColumnCount() - 1, getConfiguration().getMajorUnitX());
			createSeparators(rectangle, getRowCount() - 1, getConfiguration().getMajorUnitY());
		}

		updatePictogramElement(containerShape);

		return containerShape;
	}

	private void createSeparators(GraphicsAlgorithm parentGraphicsAlgorithm, int separators, int majorUnit) {
		for (int i = 1; i <= separators; i++) {
			Polyline polyline = Graphiti.getGaCreateService().createPolyline(parentGraphicsAlgorithm, new int[] { 0, 0, 0, 0 });
			polyline.setForeground(manageColor(getConfiguration().getForegroundColor()));
			int lineWidth;
			if (majorUnit > 0 && (i % majorUnit) == 0) {
				lineWidth = getConfiguration().getMajorUnitSeparatorWidth();
			} else {
				lineWidth = getConfiguration().getMinorSeparatorWidth();
			}
			polyline.setLineWidth(lineWidth);
		}
	}

	@Override
	public boolean layout(ILayoutContext context) {
		boolean ret = true;
		IGaService gaService = Graphiti.getGaService();
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();

		int cellWidth = containerShape.getGraphicsAlgorithm().getWidth() / getColumnCount();
		int cellHeight = containerShape.getGraphicsAlgorithm().getHeight() / getRowCount();

		List<Shape> children = containerShape.getChildren();
		for (int x = 0; x < getColumnCount(); x++) {
			for (int y = 0; y < getRowCount(); y++) {
				int cellIndex = (y * getColumnCount()) + x;
				PictogramElement cellShape = children.get(cellIndex);
				int xPos = x * cellWidth;
				int yPos = y * cellHeight;
				gaService.setLocationAndSize(cellShape.getGraphicsAlgorithm(), xPos, yPos, cellWidth, cellHeight);
			}
		}

		List<GraphicsAlgorithm> polylines = containerShape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren();

		int xSep = getColumnCount() - 1;
		int currentX = cellWidth;

		for (int i = 0; i < xSep; i++) {
			List<Point> points = ((Polyline) polylines.get(i)).getPoints();
			points.set(0, gaService.createPoint(currentX, 0));
			points.set(1, gaService.createPoint(currentX, containerShape.getGraphicsAlgorithm().getHeight()));
			currentX += cellWidth;
		}

		int currentY = cellHeight;

		for (int i = xSep; i < polylines.size(); i++) {
			List<Point> points = ((Polyline) polylines.get(i)).getPoints();
			points.set(0, gaService.createPoint(0, currentY));
			points.set(1, gaService.createPoint(containerShape.getGraphicsAlgorithm().getWidth(), currentY));
			currentY += cellHeight;
		}

		return ret;
	}

	@Override
	public boolean update(IUpdateContext context) {
		boolean ret = false;

		PictogramElement pe = context.getPictogramElement();

		if (isPatternRoot(pe)) {
			ret = updateRoot((ContainerShape) pe);
		} else {
			ContainerShape rootContainer = getPatternRoot(pe);
			if (rootContainer != null) {
				ret = updateRoot(rootContainer);
			}
		}

		return ret;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {

		// check only if the given pictogram element is the outer container
		// shape

		PictogramElement pe = context.getPictogramElement();

		if (isPatternRoot(pe)) {

			ContainerShape containerShape = (ContainerShape) pe;

			List<Shape> shapes = containerShape.getChildren();

			// check cell structure
			int cells = getRowCount() * getColumnCount();
			if (cells != shapes.size()) {
				return Reason.createTrueReason();
			}

			// check cell content
			for (Shape shape : shapes) {
				IReason ret = updateCellNeeded(shape, getGridMapping());
				if (ret.toBoolean()) {
					return ret;
				}
			}
		}

		return Reason.createFalseReason();
	}

	/**
	 * Creates the grid mapping.
	 * 
	 * @return the i structure mapping grid
	 */
	abstract protected IStructureMappingGrid createGridMapping();

	private IStructureMappingGrid getGridMapping() {
		if (gridMapping == null) {
			gridMapping = createGridMapping();
		}
		return gridMapping;
	}

	private ContainerShape getPatternRoot(PictogramElement pe) {
		ContainerShape ret = null;
		// Check that a pictogram element was provided and that it is alive
		if (pe != null && pe.eResource() != null) {
			int i = 0;
			do {
				if (isPatternRoot(pe)) {
					ret = (ContainerShape) pe;
				} else if (pe instanceof Shape) {
					pe = ((Shape) pe).getContainer();
				}
				i++;
			} while (ret == null && i < 2);
		}
		return ret;
	}

	@Override
	protected boolean isPatternControlled(PictogramElement pictogramElement) {
		ContainerShape patternRoot = getPatternRoot(pictogramElement);
		return patternRoot != null;
	}

	@Override
	protected boolean isPatternRoot(PictogramElement pe) {
		boolean ret = false;
		// Check if not null, of right instance and alive
		if (pe instanceof ContainerShape && pe.eResource() != null) {
			GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
			if (ga instanceof Rectangle) {
				Object bo = getBusinessObjectForPictogramElement(pe);
				ret = isMainBusinessObjectApplicable(bo);
			}
		}
		return ret;
	}

	private IReason updateCellNeeded(Shape shape, IStructureMappingMulti mapping) {
		IReason ret = Reason.createFalseReason();

		PictogramLink linkForPictogramElement = Graphiti.getLinkService().getLinkForPictogramElement(shape);
		if (linkForPictogramElement != null) {
			Text textGa = (Text) shape.getGraphicsAlgorithm();
			boolean booleanRet = !getText(mapping, linkForPictogramElement).equals(textGa.getValue());
			if (booleanRet) {
				ret = Reason.createTrueReason(mapping.getDataMapping().getUpdateWarning(linkForPictogramElement));
			}
		}
		return ret;
	}

	private boolean updateRoot(ContainerShape outerContainerShape) {
		boolean ret = false;
		Object mainBusinessObject = getBusinessObjectForPictogramElement(outerContainerShape);

		// check cell structure
		int cells = getRowCount() * getColumnCount();
		if (cells != outerContainerShape.getChildren().size()) {
			// remove old cells
			Object[] a = outerContainerShape.getChildren().toArray();
			for (int i = 0; i < a.length; i++) {
				Graphiti.getPeService().deletePictogramElement((PictogramElement) a[i]);
			}
			// create new cells
			createCellShapes(outerContainerShape, getGridMapping().getLinkCreationInfos(mainBusinessObject));
		}

		updateCellShapes(outerContainerShape);

		return ret;
	}

	private void updateCellShapes(ContainerShape outerContainerShape) {

		List<Shape> shapes = outerContainerShape.getChildren();
		for (Shape shape : shapes) {
			PictogramLink linkForPictogramElement = Graphiti.getLinkService().getLinkForPictogramElement(shape);
			if (linkForPictogramElement != null) {
				Text textGa = (Text) shape.getGraphicsAlgorithm();
				textGa.setValue(getText(getGridMapping(), linkForPictogramElement));
			}
		}
	}

	private void createCellShapes(ContainerShape containerShape, List<ILinkCreationInfo> linkCreationInfos) {

		// add text shapes for each BO
		for (ILinkCreationInfo linkCreationInfo : linkCreationInfos) {

			Shape shape = Graphiti.getPeCreateService().createShape(containerShape, false);

			Text text = Graphiti.getGaService().createDefaultText(shape);
			text.setForeground(manageColor(getConfiguration().getTextColor()));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			text.getFont().setBold(false);
			text.getFont().setItalic(true);
			text.getFont().setSize(10);
			text.getFont().setName("Baskerville Old Face");

			// create link and wire it
			link(shape, linkCreationInfo.getBusinessObjects());
			String linkProperty = linkCreationInfo.getProperty();
			if (linkProperty != null) {
				Graphiti.getLinkService().setLinkProperty(shape, linkProperty);
			}
		}
	}

	/**
	 * Gets the configuration.
	 * 
	 * @return the configuration
	 */
	protected IGridPatternConfiguration getConfiguration() {
		return (IGridPatternConfiguration) getPatternConfiguration();
	}

	private int getColumnCount() {
		return getGridMapping().getColumns();
	}

	private int getRowCount() {
		return getGridMapping().getRows();
	}
}
