package org.activiti.designer.kickstart.form.features;

import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * @author Frederik Heremans
 */
public class DirectEditFormComponentFeature extends AbstractDirectEditingFeature {
	
	public DirectEditFormComponentFeature(IFeatureProvider fp) {
		super(fp);
	}

	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
	  PictogramElement pe = context.getPictogramElement();
	  Object bo = getBusinessObjectForPictogramElement(pe);
	  return bo instanceof FormPropertyDefinition;
	}

	public String getInitialValue(IDirectEditingContext context) {
		// Return the current name of the EClass
		PictogramElement pe = context.getPictogramElement();
		FormPropertyDefinition definition = (FormPropertyDefinition) getBusinessObjectForPictogramElement(pe);
		return definition.getName();
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		if (value.length() < 1) {
		  return "The label should be at least one character long";
		}
		return null;
	}

	public void setValue(String value, IDirectEditingContext context) {
		// set the new name for the EClass
		PictogramElement pe = context.getPictogramElement();
		FormPropertyDefinition formPropertyDefinition = (FormPropertyDefinition) getBusinessObjectForPictogramElement(pe);
		formPropertyDefinition.setName(value);

		// Explicitly update the shape to display the new value in the diagram
		// Note, that this might not be necessary in future versions of the GFW
		// (currently in discussion)

		// we know, that pe is the Shape of the Text, so its container is the
		// main shape of the EClass
		updatePictogramElement(((Shape) pe).getContainer());
	}
}
