package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public class AddLaneFeature extends AbstractAddShapeFeature {

	public AddLaneFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public PictogramElement add(IAddContext context) {

		final Lane addedLane = (Lane) context.getNewObject();
		final ContainerShape parent = context.getTargetContainer();

		// CONTAINER SHAPE WITH ROUNDED RECTANGLE
		final IPeCreateService peCreateService = Graphiti.getPeCreateService();
		final ContainerShape containerShape = peCreateService.createContainerShape(parent, true);

		int x = context.getX();
		int y = context.getY();
		int width = context.getWidth();
		int height = context.getHeight();

		final IGaService gaService = Graphiti.getGaService();
		{
			// create invisible outer rectangle expanded by
			// the width needed for the anchor
			final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
			gaService.setLocationAndSize(invisibleRectangle, x, y, width, height);

			// create and set visible rectangle inside invisible rectangle
			Rectangle rectangle = gaService.createRectangle(invisibleRectangle);
			rectangle.setParentGraphicsAlgorithm(invisibleRectangle);
			rectangle.setStyle(StyleUtil.getStyleForPool(getDiagram()));
			gaService.setLocationAndSize(rectangle, 0, 0, width, height);

			// create link and wire it
			link(containerShape, addedLane);
		}
		
		// SHAPE WITH TEXT
		{
			// create shape for text
			final Shape shape = peCreateService.createShape(containerShape, false);

			// create and set text graphics algorithm
			final Text text = gaService.createDefaultText(getDiagram(), shape, addedLane.getName());
			text.setStyle(StyleUtil.getStyleForEvent(getDiagram()));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			gaService.setLocationAndSize(text, 0, 0, 20, height);
			text.setAngle(-90);
			Font font = null;
      if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
        font = gaService.manageFont(getDiagram(), text.getFont().getName(), 11, false, true);
      }
      else {
        font = gaService.manageDefaultFont(getDiagram(), false, true);
      }      
      text.setFont(font);

			// create link and wire it
			link(shape, addedLane);
		}

		layoutPictogramElement(containerShape);
		return containerShape;
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof Lane) {
		  
		  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
			if (parentObject instanceof Pool) {
				return true;
			}
		}
		return false;
	}

	protected String getIcon() {
		return ActivitiImageProvider.IMG_LANE;
	}

}
