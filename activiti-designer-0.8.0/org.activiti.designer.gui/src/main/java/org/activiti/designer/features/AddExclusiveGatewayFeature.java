package org.activiti.designer.features;

import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.activiti.designer.util.StyleUtil;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.SubProcess;
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
		final int width = context.getWidth() <= 0 ? 60 : context.getWidth();
		final int height = context.getHeight() <= 0 ? 60 : context.getHeight();

		final IGaService gaService = Graphiti.getGaService();

		Polygon polygon;
		{
			int xy[] = new int[] { 0, 30, 30, 0, 60, 30, 30, 60, 0, 30 };
			final Polygon invisiblePolygon = gaService.createPolygon(containerShape, xy);
			invisiblePolygon.setFilled(false);
			invisiblePolygon.setLineVisible(false);
			gaService.setLocationAndSize(invisiblePolygon, context.getX(), context.getY(), width, height);

			// create and set visible circle inside invisible circle
			polygon = gaService.createPolygon(invisiblePolygon, xy);
			polygon.setParentGraphicsAlgorithm(invisiblePolygon);
			polygon.setStyle(StyleUtil.getStyleForEClass(getDiagram()));
			gaService.setLocationAndSize(polygon, 0, 0, width, height);

			// if addedClass has no resource we add it to the resource of the
			// diagram. In a real scenario the business model would have its own
			// resource
			if (addedGateway.eResource() == null) {
			  Object parentObject = getBusinessObjectForPictogramElement(parent);
        if (parentObject instanceof SubProcess) {
          ((SubProcess) parentObject).getFlowElements().add(addedGateway);
        } else {
          getDiagram().eResource().getContents().add(addedGateway);
        }
			}

			// create link and wire it
			link(containerShape, addedGateway);
		}
		
		{
			final Shape shape = peCreateService.createShape(containerShape, false);
			
			final Polyline polyline = gaService.createPolyline(shape, new int[] { width - 15, 15, 15, height-15 });
			polyline.setLineWidth(7);
			polyline.setStyle(StyleUtil.getStyleForEClass(getDiagram()));
		}
		
		{
			final Shape shape = peCreateService.createShape(containerShape, false);
			
			//final Polyline polyline = gaService.createPolyline(shape, new int[] { 27, 10, 27, height - 10 });
			final Polyline polyline = gaService.createPolyline(shape, new int[] { 15, 15, width - 15, height - 15});
			polyline.setLineWidth(7);
			polyline.setStyle(StyleUtil.getStyleForEClass(getDiagram()));
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
