package org.activiti.designer.features;

import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.activiti.designer.util.OSEnum;
import org.activiti.designer.util.OSUtil;
import org.activiti.designer.util.StyleUtil;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public class AddEventFeature extends AbstractAddShapeFeature {

	public AddEventFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public PictogramElement add(IAddContext context) {
		final Event addedEvent = (Event) context.getNewObject();
		final ContainerShape parent = context.getTargetContainer();

		// CONTAINER SHAPE WITH CIRCLE
		final IPeCreateService peCreateService = Graphiti.getPeCreateService();
		final ContainerShape containerShape = peCreateService.createContainerShape(parent, true);

		// check whether the context has a size (e.g. from a create feature)
		// otherwise define a default size for the shape
		final int width = context.getWidth() <= 0 ? 55 : context.getWidth();
		final int height = context.getHeight() <= 0 ? 55 : context.getHeight();

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
			circle.setStyle(StyleUtil.getStyleForEClass(getDiagram()));
			gaService.setLocationAndSize(circle, 0, 0, width, height);

			// if addedClass has no resource we add it to the resource of the
			// diagram
			// in a real scenario the business model would have its own resource
			if (addedEvent.eResource() == null) {
				Object parentObject = getBusinessObjectForPictogramElement(parent);
	      if (parentObject instanceof SubProcess) {
	        ((SubProcess) parentObject).getFlowElements().add(addedEvent);
	      } else {
	        getDiagram().eResource().getContents().add(addedEvent);
	      }
			}

			// create link and wire it
			link(containerShape, addedEvent);
		}

		// SHAPE WITH TEXT
		{
			// create shape for text
			final Shape shape = peCreateService.createShape(containerShape, false);

			// create and set text graphics algorithm
			final Text text = gaService.createDefaultText(shape, addedEvent.getName());
			text.setStyle(StyleUtil.getStyleForEClassText(getDiagram()));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			text.getFont().setBold(true);
			if(OSUtil.getOperatingSystem() == OSEnum.Mac) {
				text.getFont().setSize(11);
			}
			gaService.setLocationAndSize(text, 0, 0, width, 55);

			// create link and wire it
			link(shape, addedEvent);

			// provide information to support direct-editing directly
			// after object creation (must be activated additionally)
			final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
			// set container shape for direct editing after object creation
			directEditingInfo.setMainPictogramElement(containerShape);
			// set shape and graphics algorithm where the editor for
			// direct editing shall be opened after object creation
			directEditingInfo.setPictogramElement(shape);
			directEditingInfo.setGraphicsAlgorithm(text);
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
