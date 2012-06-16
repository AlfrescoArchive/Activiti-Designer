package org.activiti.designer.features;

import org.activiti.designer.bpmn2.model.TextAnnotation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class DirectEditTextAnnotationFeature extends
		AbstractDirectEditingFeature {

	public DirectEditTextAnnotationFeature(final IFeatureProvider fp) {
	    super(fp);
    }
	
	@Override
	public int getEditingType() {
		return TYPE_MULTILINETEXT;
	}

	@Override
	public String getInitialValue(final IDirectEditingContext context) {
		
		final PictogramElement pe = context.getPictogramElement();
		final TextAnnotation annotation = (TextAnnotation) getBusinessObjectForPictogramElement(pe);	
		
		return annotation.getText();
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		final PictogramElement pe = context.getPictogramElement();
		final Object bo = getBusinessObjectForPictogramElement(pe);
		final GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
	
		return bo instanceof TextAnnotation && ga instanceof MultiText;
	}

	@Override
	public void setValue(String value, IDirectEditingContext context) {
		final PictogramElement pe = context.getPictogramElement();
		final TextAnnotation annotation = (TextAnnotation) getBusinessObjectForPictogramElement(pe);
		
		annotation.setText(value);
		
		updatePictogramElement(((Shape) pe).getContainer());
	}
}
