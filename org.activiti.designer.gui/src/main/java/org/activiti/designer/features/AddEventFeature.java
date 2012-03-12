package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.EndEvent;
import org.activiti.designer.bpmn2.model.Event;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public class AddEventFeature extends AbstractAddShapeFeature {
	
	private static final int IMAGE_SIZE = 30;

	public AddEventFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public PictogramElement add(IAddContext context) {
		final Event addedEvent = (Event) context.getNewObject();
		System.out.println("addedEvent " + addedEvent);
		final ContainerShape parent = context.getTargetContainer();

		// CONTAINER SHAPE WITH CIRCLE
		final IPeCreateService peCreateService = Graphiti.getPeCreateService();
		final ContainerShape containerShape = peCreateService.createContainerShape(parent, true);

		// check whether the context has a size (e.g. from a create feature)
		// otherwise define a default size for the shape
		final int width = context.getWidth() <= 35 ? 35 : context.getWidth();
		final int height = context.getHeight() <= 35 ? 35 : context.getHeight();

		final IGaService gaService = Graphiti.getGaService();

		Ellipse circle;
		{
			final Ellipse invisibleCircle = gaService.createEllipse(containerShape);
			invisibleCircle.setFilled(false);
			invisibleCircle.setLineVisible(false);
			gaService.setLocationAndSize(invisibleCircle, context.getX(), context.getY(), width, height);

			// create and set visible circle inside invisible circle
			circle = gaService.createEllipse(invisibleCircle);
			circle.setParentGraphicsAlgorithm(invisibleCircle);
			circle.setStyle(StyleUtil.getStyleForEvent(getDiagram()));
			if (addedEvent instanceof EndEvent == true) {
			  circle.setLineWidth(3);
			}
			gaService.setLocationAndSize(circle, 0, 0, width, height);

			// create link and wire it
			link(containerShape, addedEvent);
		}

		// add a chopbox anchor to the shape
		peCreateService.createChopboxAnchor(containerShape);
		if (!(addedEvent instanceof EndEvent)) {

			// create an additional box relative anchor at middle-right
			final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
			boxAnchor.setRelativeWidth(1.0);
			boxAnchor.setRelativeHeight(0.51);
			boxAnchor.setReferencedGraphicsAlgorithm(circle);
			final Ellipse ellipse = ActivitiUiUtil.createInvisibleEllipse(boxAnchor, gaService);
			gaService.setLocationAndSize(ellipse, 0, 0, 0, 0);
		}
		
		if (addedEvent instanceof StartEvent && false) {
			
			final Shape shape = peCreateService.createShape(containerShape, false);
      final Image image = gaService.createImage(shape, ActivitiImageProvider.IMG_BOUNDARY_TIMER);
      image.setStretchH(true);
      image.setStretchV(true);
      image.setWidth(IMAGE_SIZE);
      image.setHeight(IMAGE_SIZE);
      
      gaService.setLocationAndSize(image, (width - IMAGE_SIZE) / 2, (height - IMAGE_SIZE) / 2, IMAGE_SIZE, IMAGE_SIZE);
		}
		
		layoutPictogramElement(containerShape);

		return containerShape;
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof Event) {
		  
		  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
      
      if (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess) {
        return true;
      }
		}
		return false;
	}

}
