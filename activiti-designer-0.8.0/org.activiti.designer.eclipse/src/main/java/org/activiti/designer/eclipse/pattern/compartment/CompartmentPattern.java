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

import java.util.List;

import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.mapping.ILinkCreationInfo;
import org.eclipse.graphiti.pattern.mapping.IStructureMappingMulti;
import org.eclipse.graphiti.pattern.mapping.IStructureMappingSingle;
import org.eclipse.graphiti.pattern.mapping.data.LabelDataMapping;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaCreateService;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.ILinkService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * The Class CompartmentPattern.
 */
public abstract class CompartmentPattern extends AbstractPattern {

	private final static int TEXT_HEIGHT = 25;

	private IStructureMappingSingle headerMapping;

	private IStructureMappingMulti[] compartmentMappings;

	/**
	 * Instantiates a new compartment pattern.
	 */
	public CompartmentPattern() {
		super(new DefaultCompartmentPatternConfiguration());
	}

	@Override
	public PictogramElement add(IAddContext context) {
		Object mainBusinessObject = context.getNewObject();
		ContainerShape parentContainerShape = context.getTargetContainer();

		// CONTAINER SHAPE WITH ROUNDED RECTANGLE
		final IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaCreateService gaCreateService = Graphiti.getGaCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(parentContainerShape, true);

		getFeatureProvider().getDirectEditingInfo().setMainPictogramElement(containerShape);

		// check whether valid size is available, e.g. if called from the create
		// feature
		int width = context.getWidth() <= 0 ? 100 : context.getWidth();
		int height = context.getHeight() <= 0 ? 120 : context.getHeight();

		{
			// create and set graphics algorithm
			RoundedRectangle roundedRectangle = gaCreateService.createRoundedRectangle(containerShape, getConfiguration().getCornerWidth(),
					getConfiguration().getCornerHeight());
			roundedRectangle.setForeground(manageColor(getConfiguration().getForegroundColor()));
			roundedRectangle.setBackground(manageColor(getConfiguration().getBackgroundColor()));
			roundedRectangle.setLineWidth(getConfiguration().getLineWidth());
			roundedRectangle.setTransparency(getConfiguration().getTransparency());
			Graphiti.getGaService().setLocationAndSize(roundedRectangle, context.getX(), context.getY(), width, height);

			// create link and wire it
			link(containerShape, mainBusinessObject);
		}

		// HEADER SHAPE
		{
			// create shape for text
			Shape shape = peCreateService.createShape(containerShape, false);

			Text text;

			if (getConfiguration().isHeaderImageVisible()) {

				Rectangle rectangle = gaCreateService.createRectangle(shape);
				rectangle.setFilled(false);
				rectangle.setLineVisible(false);

				gaCreateService.createImage(rectangle, IPlatformImageConstants.IMG_EDIT_EXPANDALL);

				text = gaCreateService.createDefaultText(rectangle);
				text.setForeground(manageColor(getConfiguration().getTextColor()));
				text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
				text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
				text.getFont().setBold(true);

			} else { // just a text data mapping

				text = gaCreateService.createDefaultText(shape);
				text.setForeground(manageColor(getConfiguration().getTextColor()));
				text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
				text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
				text.getFont().setBold(true);
			}

			IDirectEditingInfo dei = getFeatureProvider().getDirectEditingInfo();
			dei.setPictogramElement(shape);
			dei.setGraphicsAlgorithm(text);

			// create link and wire it
			ILinkCreationInfo linkCreationInfo = getHeaderMapping().getLinkCreationInfo(mainBusinessObject);
			String linkProperty = linkCreationInfo.getProperty();
			if (linkProperty != null) {
				getLinkService().setLinkProperty(shape, linkProperty);
			}
			link(shape, linkCreationInfo.getBusinessObjects());
		}

		{
			for (int compartmentIndex = 0; compartmentIndex < getCompartmentCount(); compartmentIndex++) {

				// create a line shape above each compartment
				// SHAPE WITH LINE
				{
					// create shape for line
					Shape shape = peCreateService.createShape(containerShape, false);

					// create and set graphics algorithm
					Polyline polyline = gaCreateService.createPolyline(shape, new int[] { 0, 0, 0, 0 });
					polyline.setForeground(manageColor(getConfiguration().getForegroundColor()));
					polyline.setLineWidth(getConfiguration().getLineWidth());
				}

				ContainerShape compartmentContainerShape = peCreateService.createContainerShape(containerShape, false);
				Rectangle compartmentRectangle = gaCreateService.createRectangle(compartmentContainerShape);
				compartmentRectangle.setFilled(false);
				compartmentRectangle.setLineVisible(false);

				// createLink(compartmentContainerShape, mainBusinessObject);
			}
		}

		peCreateService.createChopboxAnchor(containerShape);

		updatePictogramElement(containerShape);

		return containerShape;
	}

	/**
	 * Activates direct editing for header text control.
	 */
	protected void activateHeaderTextAutoDirectEditing() {
		getFeatureProvider().getDirectEditingInfo().setActive(true);
	}

	@Override
	public boolean layout(ILayoutContext context) {
		boolean ret = false;
		IGaService gaService = Graphiti.getGaService();
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		int continousY = getConfiguration().getOuterIndentTop();

		List<Shape> containerShapeChildren = containerShape.getChildren();
		// width
		{
			int containerWidth = containerShape.getGraphicsAlgorithm().getWidth();
			int minimumWidth = getConfiguration().getMinimumWidth();
			if (containerWidth < minimumWidth) {
				containerShape.getGraphicsAlgorithm().setWidth(minimumWidth);
				containerWidth = minimumWidth;
				ret = true;
			}
			int innerWidth = containerWidth - getConfiguration().getOuterIndentLeft() - getConfiguration().getOuterIndentRight(); // without
			// indents

			for (Shape shape : containerShapeChildren) {
				GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();

				// move horizontal lines
				if (ga instanceof Polyline) {
					Polyline pl = (Polyline) ga;
					List<Point> points = pl.getPoints();
					int x0 = points.get(0).getX();
					points.set(0, gaService.createPoint(x0, continousY));
					points.set(1, gaService.createPoint(containerWidth, continousY));
				} else {
					if (shape instanceof ContainerShape) {
						ContainerShape compartmentContainerShape = (ContainerShape) shape;
						compartmentContainerShape.getGraphicsAlgorithm().setY(continousY);
						// loop the shapes inside a compartment
						int innerY = 0;
						for (Shape innerCompartmentShape : compartmentContainerShape.getChildren()) {
							GraphicsAlgorithm innerGa = innerCompartmentShape.getGraphicsAlgorithm();
							gaService.setLocationAndSize(innerGa, 0, innerY, innerWidth, TEXT_HEIGHT);
							GraphicsAlgorithm imageGa = innerGa.getGraphicsAlgorithmChildren().get(0);
							gaService.setLocationAndSize(imageGa, 0, 0, TEXT_HEIGHT, TEXT_HEIGHT);
							GraphicsAlgorithm textGa = innerGa.getGraphicsAlgorithmChildren().get(1);
							gaService.setLocationAndSize(textGa, TEXT_HEIGHT, 0, innerWidth - TEXT_HEIGHT, TEXT_HEIGHT);
							innerY += TEXT_HEIGHT;
						}
						int compartmentHeight = compartmentContainerShape.getChildren().size() * TEXT_HEIGHT;
						gaService.setLocationAndSize(compartmentContainerShape.getGraphicsAlgorithm(), getConfiguration()
								.getOuterIndentLeft(), continousY, innerWidth, compartmentHeight, true);
					} else { // header shape
						gaService.setLocationAndSize(shape.getGraphicsAlgorithm(), getConfiguration().getOuterIndentLeft(),
								getConfiguration().getOuterIndentTop(), innerWidth, TEXT_HEIGHT);

						if (getConfiguration().isHeaderImageVisible()) {
							GraphicsAlgorithm imageGa = ga.getGraphicsAlgorithmChildren().get(0);
							GraphicsAlgorithm textGa = ga.getGraphicsAlgorithmChildren().get(1);
							gaService.setLocationAndSize(imageGa, 0, 0, TEXT_HEIGHT, TEXT_HEIGHT);
							gaService.setLocationAndSize(textGa, TEXT_HEIGHT, 0, innerWidth - TEXT_HEIGHT, TEXT_HEIGHT);
						}
					}
					continousY += gaService.calculateSize(ga).getHeight();
				}
			}
		}

		// height
		{
			int additionalHeight = 0;
			int compartmentCount = containerShapeChildren.size();
			if (compartmentCount >= 1) {
				Object lastCompartment = containerShapeChildren.get(compartmentCount - 1);
				if (lastCompartment instanceof ContainerShape) {
					ContainerShape lastCompartmentCs = (ContainerShape) lastCompartment;
					if (lastCompartmentCs.getChildren().size() == 0) {
						additionalHeight = getConfiguration().getCornerHeight() / 2;
					}
				}
			}

			int containerHeight = containerShape.getGraphicsAlgorithm().getHeight();
			int newHeight = Math.max(containerHeight, continousY + additionalHeight + getConfiguration().getOuterIndentBottom());
			newHeight = Math.max(newHeight, getConfiguration().getMinimumHeight());
			if (containerHeight != newHeight) {
				containerShape.getGraphicsAlgorithm().setHeight(newHeight);
				ret = true;
			}
		}

		return ret;
	}

	@Override
	public boolean update(IUpdateContext context) {
		boolean ret = false;

		PictogramElement pe = context.getPictogramElement();

		if (isPatternRoot(pe)) {
			ret = updateAndLayoutRoot((ContainerShape) pe);
		} else {
			ContainerShape rootContainer = getPatternRoot(pe);
			if (rootContainer != null) {
				ret = updateAndLayoutRoot(rootContainer);
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

			ContainerShape outerContainerShape = (ContainerShape) pe;

			// check header
			if (getHeaderMapping().getDataMapping() instanceof LabelDataMapping) {

			} else {
				Shape textShape = outerContainerShape.getChildren().get(0);
				PictogramLink linkForPictogramElement = getLinkService().getLinkForPictogramElement(textShape);
				if (linkForPictogramElement != null) {
					GraphicsAlgorithm ga = textShape.getGraphicsAlgorithm();
					String currentValue = ((Text) ga).getValue();
					String value = getText(getHeaderMapping(), linkForPictogramElement);
					// compare values
					if (currentValue == null || !currentValue.equals(value)) {
						return Reason.createTrueReason(getHeaderMapping().getDataMapping().getUpdateWarning(linkForPictogramElement)); // ("header
						// out
						// of
						// date");
					}
				}
			}
			// check compartments
			Object mainBusinessObject = getBusinessObjectForPictogramElement(outerContainerShape);
			for (int compartmentIndex = 0; compartmentIndex < getCompartmentCount(); compartmentIndex++) {
				ContainerShape compartmentContainerShape = (ContainerShape) outerContainerShape.getChildren().get(
						(compartmentIndex + 1) * 2);
				if (compartmentContainerShape != null) {
					IStructureMappingMulti mapping = getCompartmentMapping(compartmentIndex);

					IReason ret = updateCompartmentNeeded(compartmentContainerShape, mainBusinessObject, mapping);
					if (ret.toBoolean()) {
						return ret;
					}
				}
			}
		}

		return Reason.createFalseReason();
	}

	/**
	 * Creates the compartment mappings.
	 * 
	 * @return the i structure mapping multi[]
	 */
	abstract protected IStructureMappingMulti[] createCompartmentMappings();

	/**
	 * Creates the header mapping.
	 * 
	 * @return the i structure mapping single
	 */
	abstract protected IStructureMappingSingle createHeaderMapping();

	/**
	 * @param compartmentContainerShape
	 * @param linkCreationInfos
	 */
	private void createShapesInCompartment(ContainerShape compartmentContainerShape, List<ILinkCreationInfo> linkCreationInfos) {

		IGaCreateService gaCreateService = Graphiti.getGaCreateService();

		// add multi text shapes
		for (ILinkCreationInfo linkCreationInfo : linkCreationInfos) {

			Shape shape = Graphiti.getPeCreateService().createShape(compartmentContainerShape, false);

			Rectangle rectangle = gaCreateService.createRectangle(shape);
			rectangle.setFilled(false);
			rectangle.setLineVisible(false);

			gaCreateService.createImage(rectangle, IPlatformImageConstants.IMG_EDIT_EXPANDALL);

			Text text = gaCreateService.createDefaultText(rectangle);
			text.setForeground(manageColor(getConfiguration().getTextColor()));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
			text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			text.getFont().setBold(true);

			// create link and wire it
			Object[] businessObjects = linkCreationInfo.getBusinessObjects();
			link(shape, businessObjects);
			String linkProperty = linkCreationInfo.getProperty();
			if (linkProperty != null) {
				getLinkService().setLinkProperty(shape, linkProperty);
			}
		}
	}

	/**
	 * @return Returns the compartmentCount.
	 */
	private int getCompartmentCount() {
		return getCompartmentMappings().length;
	}

	private IStructureMappingMulti getCompartmentMapping(int index) {
		return getCompartmentMappings()[index];
	}

	private IStructureMappingMulti[] getCompartmentMappings() {
		if (compartmentMappings == null) {
			compartmentMappings = createCompartmentMappings();
		}
		return compartmentMappings;
	}

	private IStructureMappingSingle getHeaderMapping() {
		if (headerMapping == null) {
			headerMapping = createHeaderMapping();
		}
		return headerMapping;
	}

	private ContainerShape getPatternRoot(PictogramElement pe) {
		ContainerShape ret = null;

		// Check that a pictogram element was provided and that it is alive
		if (pe == null || pe.eResource() == null) {
			return ret;
		}

		int i = 0;
		do {
			if (isPatternRoot(pe)) {
				ret = (ContainerShape) pe;
			} else if (pe instanceof Shape) {
				pe = ((Shape) pe).getContainer();
			}
			i++;
		} while (ret == null && i < 3);
		return ret;
	}

	private boolean isCompartment(PictogramElement pe) {
		boolean ret = false;
		if (pe instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pe;
			ContainerShape container = cs.getContainer();
			ret = isPatternRoot(container);
		}
		return ret;
	}

	@SuppressWarnings("unused")
	private boolean isCompartmentEntry(PictogramElement pe) {
		boolean ret = false;
		if (pe instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pe;
			ContainerShape container = cs.getContainer();
			ret = isCompartment(container);
		}
		return ret;
	}

	@SuppressWarnings("unused")
	private boolean isHeader(PictogramElement pe) {
		boolean ret = false;
		if (pe instanceof Shape) {
			Shape shape = (Shape) pe;
			GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
			if (ga instanceof Text) {
				ContainerShape container = shape.getContainer();
				ret = isPatternRoot(container);
			}
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
			if (ga instanceof RoundedRectangle) {
				Object bo = getBusinessObjectForPictogramElement(pe);
				ret = isMainBusinessObjectApplicable(bo);
			}
		}
		return ret;
	}

	private boolean updateCompartment(Object mainBusinessObject, ContainerShape compartmentContainerShape, IStructureMappingMulti mapping) {
		boolean ret = false;

		List<ILinkCreationInfo> linkCreationInfos = mapping.getLinkCreationInfos(mainBusinessObject);
		List<Shape> currentCompartmentElements = compartmentContainerShape.getChildren();
		if (linkCreationInfos.size() != currentCompartmentElements.size()) {
			Object[] a = compartmentContainerShape.getChildren().toArray();
			for (int i = 0; i < a.length; i++) {
				Graphiti.getPeService().deletePictogramElement((PictogramElement) a[i]);
			}
			createShapesInCompartment(compartmentContainerShape, linkCreationInfos);
			ret = true;
		}
		List<Shape> childShapes = compartmentContainerShape.getChildren();
		for (Shape shape : childShapes) {
			ret = ret | updateCompartmentEntry(shape, mapping);
		}
		return ret;
	}

	private boolean updateCompartmentEntry(Shape shape, IStructureMappingMulti mapping) {
		boolean ret = false;

		PictogramLink linkForPictogramElement;
		linkForPictogramElement = getLinkService().getLinkForPictogramElement(shape);
		if (linkForPictogramElement != null) {
			Image imageGa = (Image) shape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(0);
			Text textGa = (Text) shape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(1);
			imageGa.setId(getImage(mapping, linkForPictogramElement));
			textGa.setValue(getText(mapping, linkForPictogramElement));
			ret = true;
		}
		return ret;
	}

	private IReason updateCompartmentNeeded(ContainerShape compartmentContainerShape, Object mainBusinessObject,
			IStructureMappingMulti mapping) {
		PictogramLink linkForPictogramElement;
		List<ILinkCreationInfo> linkCreationInfos = mapping.getLinkCreationInfos(mainBusinessObject);
		List<Shape> childShapes = compartmentContainerShape.getChildren();
		int childShapeCount = childShapes.size();
		if (linkCreationInfos.size() != childShapeCount) {
			// structural change inside the compartment
			return Reason.createTrueReason("compartment changes");
		} else {
			// check whether compartment content has changed
			for (Shape shape : childShapes) {
				linkForPictogramElement = getLinkService().getLinkForPictogramElement(shape);
				if (linkForPictogramElement != null) {
					Image imageGa = (Image) shape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(0);
					Text textGa = (Text) shape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(1);
					String currentImage = imageGa.getId();
					String currentText = textGa.getValue();
					String image = getImage(mapping, linkForPictogramElement);
					String text = getText(mapping, linkForPictogramElement);
					// compare values
					if (currentText == null || !currentText.equals(text)) {
						return Reason.createTrueReason(mapping.getDataMapping().getUpdateWarning(linkForPictogramElement)); // ("text
						// differs");
					}
					if (!(currentImage == null && image == null)) {
						if (currentImage == null || !currentImage.equals(image)) {
							return Reason.createTrueReason(mapping.getDataMapping().getUpdateWarning(linkForPictogramElement)); // ("image
							// differs");
						}
					}
				}
			}
		}
		return Reason.createFalseReason();
	}

	private boolean updateHeader(Shape headerShape) {
		boolean ret = false;
		PictogramLink linkForPictogramElement = getLinkService().getLinkForPictogramElement(headerShape);
		if (linkForPictogramElement != null) {

			if (getConfiguration().isHeaderImageVisible()) {
				Image imageGa = (Image) headerShape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(0);
				Text textGa = (Text) headerShape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(1);
				imageGa.setId(getImage(getHeaderMapping(), linkForPictogramElement));
				textGa.setValue(getText(getHeaderMapping(), linkForPictogramElement));
			} else {
				String value = getText(getHeaderMapping(), linkForPictogramElement);
				((Text) headerShape.getGraphicsAlgorithm()).setValue(value);
			}
			ret = true;
		}
		return ret;
	}

	private boolean updateAndLayoutRoot(ContainerShape outerContainerShape) {
		Object mainBusinessObject = getBusinessObjectForPictogramElement(outerContainerShape);

		// header text
		Shape textShape = outerContainerShape.getChildren().get(0);
		boolean ret = updateHeader(textShape);

		// compartments
		for (int compartmentIndex = 0; compartmentIndex < getCompartmentCount(); compartmentIndex++) {

			ContainerShape compartmentContainerShape = (ContainerShape) outerContainerShape.getChildren().get((compartmentIndex + 1) * 2);
			IStructureMappingMulti mapping = getCompartmentMapping(compartmentIndex);
			if (compartmentContainerShape != null) {
				ret = ret | updateCompartment(mainBusinessObject, compartmentContainerShape, mapping);
			}
		}

		layoutPictogramElement(outerContainerShape);

		return ret;
	}

	/**
	 * Gets the configuration.
	 * 
	 * @return the configuration
	 */
	protected ICompartmentPatternConfiguration getConfiguration() {
		return (ICompartmentPatternConfiguration) getPatternConfiguration();
	}

	@Override
	public void completeInfo(IDirectEditingInfo info, Object bo) {
		super.completeInfo(info, bo);
		PictogramElement mainPictogramElement = info.getMainPictogramElement();
		ContainerShape mainCs = (ContainerShape) mainPictogramElement;
		if (mainCs != null) {
			for (Object childShape : mainCs.getChildren()) {
				if (childShape instanceof ContainerShape) {
					ContainerShape compartmentCs = (ContainerShape) childShape;
					for (Object compartmentEntry : compartmentCs.getChildren()) {
						if (compartmentEntry instanceof Shape) {
							Shape compartmentEntryShape = (Shape) compartmentEntry;
							Object firstBusinessObjectForPictogramElement = getBusinessObjectForPictogramElement(compartmentEntryShape);
							if (bo.equals(firstBusinessObjectForPictogramElement)) {
								GraphicsAlgorithm ga = compartmentEntryShape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(1);
								if (ga instanceof Text) {
									info.setPictogramElement(compartmentEntryShape);
									info.setGraphicsAlgorithm(ga);
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	protected ILinkService getLinkService() {
		return Graphiti.getLinkService();
	}
}
