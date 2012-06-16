package org.activiti.designer.features;

import org.activiti.designer.bpmn2.model.TextAnnotation;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

public class LayoutTextAnnotationFeature extends AbstractLayoutFeature {

	public LayoutTextAnnotationFeature(final IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public boolean canLayout(ILayoutContext context) {
		final PictogramElement pe = context.getPictogramElement();
		
		if (!(pe instanceof ContainerShape)) {
			return false;
		}
		
		final Object bo = getBusinessObjectForPictogramElement(pe);
		
		return bo instanceof TextAnnotation;
	}

	@Override
	public boolean layout(ILayoutContext context) {
		final ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		final GraphicsAlgorithm ga = containerShape.getGraphicsAlgorithm();
		final IGaService gaService = Graphiti.getGaService();
		
		boolean changed = false;
		
		if (ga.getWidth() < 100) {
			// we deny anything smaller than 100 pixels width
			ga.setWidth(100);
			
			changed = true;
		}
		
		if (ga.getHeight() < 50) {
			// we deny anything smaller than 50 pixels height
			ga.setHeight(50);
			
			changed = true;
		}
		
		int containerWidth = ga.getWidth();
		int containerHeight = ga.getHeight();
		
		for (final Shape shape : containerShape.getChildren()) {
			final GraphicsAlgorithm shapeGa = shape.getGraphicsAlgorithm();
			final IDimension size = gaService.calculateSize(shapeGa);
			
			if (containerWidth != size.getWidth() && shapeGa instanceof MultiText) {
				gaService.setWidth(shapeGa, containerWidth - 5);
				
				changed = true;
			}
			
			if (containerHeight != size.getHeight()) {
				if (shapeGa instanceof Polyline) {
					final Polyline line = (Polyline) shapeGa;
					
					line.getPoints().set(2, getNewPoint(line, 2, containerHeight, gaService));
					line.getPoints().set(3, getNewPoint(line, 3, containerHeight, gaService));
					
					changed = true;
				}
				else if (shapeGa instanceof MultiText) {
					gaService.setHeight(shapeGa, containerHeight - 5);
					
					changed = true;
				}
			}
		}
	
		return changed;
	}

	private Point getNewPoint(Polyline line, int pointIndex, int height, IGaService gaService) {
		final Point p = line.getPoints().get(pointIndex);
		
		return gaService.createPoint(p.getX(), height);
	}

}
