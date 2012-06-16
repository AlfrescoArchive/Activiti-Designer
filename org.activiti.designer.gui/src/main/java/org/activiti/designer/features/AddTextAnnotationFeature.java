package org.activiti.designer.features;

import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.TextAnnotation;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public class AddTextAnnotationFeature extends AbstractAddShapeFeature {

	public AddTextAnnotationFeature(final IFeatureProvider fp) {
	  super(fp);
	}
	
	@Override
	public boolean canAdd(IAddContext context) {
		final boolean isAnnotation = context.getNewObject() instanceof TextAnnotation;
		final boolean intoDiagram = context.getTargetContainer() instanceof Diagram;
		
		final Object parent = getBusinessObjectForPictogramElement(context.getTargetContainer());
		
		final boolean intoSubProcess = parent instanceof SubProcess;
		final boolean intoLane = parent instanceof Lane;

		return isAnnotation && (intoDiagram || intoLane || intoSubProcess);
	}

	@Override
	public PictogramElement add(IAddContext context) {
		final TextAnnotation annotation = (TextAnnotation) context.getNewObject();
		
		final IPeCreateService peCreateService = Graphiti.getPeCreateService();
		final ContainerShape containerShape = peCreateService.createContainerShape(context.getTargetContainer(), true);
		
		final IGaService gaService = Graphiti.getGaService();
		
		// TODO: we currently only support horizontal lanes!!!
		final int height = Math.max(50, context.getHeight());
		final int width = Math.max(100, context.getWidth());
		final int commentEdge = 20;
		
		final Rectangle rect = gaService.createInvisibleRectangle(containerShape);
		gaService.setLocationAndSize(rect, context.getX(), context.getY(), width, height);
		
		final Shape lineShape = peCreateService.createShape(containerShape, false);
		final Polyline line 
			= gaService.createPolyline(lineShape
					, new int[] { commentEdge, 0, 0, 0, 0, height, commentEdge, height });
		line.setStyle(StyleUtil.getStyleForTask(getDiagram()));
		line.setLineWidth(2);
		gaService.setLocationAndSize(line, 0, 0, commentEdge, height);
		
		final Shape textShape = peCreateService.createShape(containerShape, false);
		final MultiText text = gaService.createDefaultMultiText(getDiagram(), textShape, annotation.getText());
		text.setStyle(StyleUtil.getStyleForTask(getDiagram()));
		text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
		if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
		  text.setFont(gaService.manageFont(getDiagram(), text.getFont().getName(), 11));
		}
		gaService.setLocationAndSize(text, 5, 5, width - 5, height - 5);
		
		// link both, the container as well as the text shape so direct editing works together
		// with updating and property handling
		link(containerShape, annotation);
		link(textShape, annotation);
		
		peCreateService.createChopboxAnchor(containerShape);
			
		layoutPictogramElement(containerShape);
		
		return containerShape;
	}

}
