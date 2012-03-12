package org.activiti.designer.features;

import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.activiti.designer.bpmn2.model.Gateway;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public class AddExclusiveGatewayFeature extends AbstractAddShapeFeature {

	public AddExclusiveGatewayFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public PictogramElement add(IAddContext context) {
		final ExclusiveGateway addedGateway = (ExclusiveGateway) context.getNewObject();
		final ContainerShape parent = context.getTargetContainer();

		// CONTAINER SHAPE WITH CIRCLE
		final IPeCreateService peCreateService = Graphiti.getPeCreateService();
		final ContainerShape containerShape = peCreateService.createContainerShape(parent, true);

		// check whether the context has a size (e.g. from a create feature)
		// otherwise define a default size for the shape
		final int width = context.getWidth() <= 0 ? 40 : context.getWidth();
		final int height = context.getHeight() <= 0 ? 40 : context.getHeight();

		final IGaService gaService = Graphiti.getGaService();

		Polygon polygon;
		{
			int xy[] = new int[] { 0, 20, 20, 0, 40, 20, 20, 40, 0, 20 };
			final Polygon invisiblePolygon = gaService.createPolygon(containerShape, xy);
			invisiblePolygon.setFilled(false);
			invisiblePolygon.setLineVisible(false);
			gaService.setLocationAndSize(invisiblePolygon, context.getX(), context.getY(), width, height);

			// create and set visible circle inside invisible circle
			polygon = gaService.createPolygon(invisiblePolygon, xy);
			polygon.setParentGraphicsAlgorithm(invisiblePolygon);
			polygon.setStyle(StyleUtil.getStyleForEvent(getDiagram()));
			gaService.setLocationAndSize(polygon, 0, 0, width, height);

			// create link and wire it
			link(containerShape, addedGateway);
		}
		
		{
			final Shape shape = peCreateService.createShape(containerShape, false);
			
			final Polyline polyline = gaService.createPolyline(shape, new int[] { width - 10, 10, 10, height - 10 });
			polyline.setLineWidth(5);
			polyline.setStyle(StyleUtil.getStyleForEvent(getDiagram()));
		}
		
		{
			final Shape shape = peCreateService.createShape(containerShape, false);
			final Polyline polyline = gaService.createPolyline(shape, new int[] { 10, 10, width - 10, height - 10});
			polyline.setLineWidth(5);
			polyline.setStyle(StyleUtil.getStyleForEvent(getDiagram()));
		}

		{
			// add a chopbox anchor to the shape
			peCreateService.createChopboxAnchor(containerShape);
			final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
			boxAnchor.setRelativeWidth(0.51);
			boxAnchor.setRelativeHeight(0.10);
			boxAnchor.setReferencedGraphicsAlgorithm(polygon);
			final Ellipse ellipse = ActivitiUiUtil.createInvisibleEllipse(boxAnchor, gaService);
			gaService.setLocationAndSize(ellipse, 0, 0, 0, 0);
		}

		{
			// add a another chopbox anchor to the shape
			peCreateService.createChopboxAnchor(containerShape);
			final BoxRelativeAnchor boxAnchor2 = peCreateService.createBoxRelativeAnchor(containerShape);
			boxAnchor2.setRelativeWidth(0.51);
			boxAnchor2.setRelativeHeight(0.93);
			boxAnchor2.setReferencedGraphicsAlgorithm(polygon);
			final Ellipse ellipse = ActivitiUiUtil.createInvisibleEllipse(boxAnchor2, gaService);
			gaService.setLocationAndSize(ellipse, 0, 0, 0, 0);
		}

		// call the layout feature
		layoutPictogramElement(containerShape);

		return containerShape;
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof Gateway) {
		  
		  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
      
      if (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess) {
        return true;
      }
		}
		return false;
	}

}
