package org.activiti.designer.features;

import org.activiti.designer.bpmn2.model.TextAnnotation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class UpdateTextAnnotationFeature extends AbstractUpdateFeature {

	public UpdateTextAnnotationFeature(final IFeatureProvider fp) {
	  super(fp);
	}
	
	@Override
	public boolean canUpdate(IUpdateContext context) {
		final Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		
		return bo instanceof TextAnnotation;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		// retrieve text of annotation
		String pictogramText = null;
		
		final PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
		  final ContainerShape cs = (ContainerShape) pictogramElement;
		  for (final Shape shape : cs.getChildren()) {
			if (shape.getGraphicsAlgorithm() instanceof AbstractText) {
			  final AbstractText text = (AbstractText) shape.getGraphicsAlgorithm();
				  
			  pictogramText = text.getValue();
			}
		  }
		}
		
		String businessText = null;
		final Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof TextAnnotation) {
			final TextAnnotation ta = (TextAnnotation) bo;
			businessText = ta.getText();
		}
		
		if (pictogramText == null && businessText != null 
				|| pictogramText != null && !pictogramText.equals(businessText))
		{
			return Reason.createTrueReason();
		}
		
		return Reason.createFalseReason();
	}

	@Override
	public boolean update(IUpdateContext context) {
		
		String businessText = null;
		final PictogramElement pe = context.getPictogramElement();
		final Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof TextAnnotation) {
			final TextAnnotation ta = (TextAnnotation) bo;
			businessText = ta.getText();
		}
		
		if (pe instanceof ContainerShape) {
			final ContainerShape cs = (ContainerShape) pe;
			for (Shape shape : cs.getChildren()) {
				if (shape.getGraphicsAlgorithm() instanceof AbstractText) {
					final AbstractText text = (AbstractText) shape.getGraphicsAlgorithm();
					
					text.setValue(businessText);
					
					return true;
				}
			}
		}
		
		return false;
	}

}
