package org.activiti.designer.features;

import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Pool;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class UpdatePoolAndLaneFeature extends AbstractUpdateFeature {

	public UpdatePoolAndLaneFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canUpdate(IUpdateContext context) {
	  Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return (bo instanceof Pool || bo instanceof Lane);
	}

  public IReason updateNeeded(IUpdateContext context) {
		// retrieve name from pictogram model
		String pictogramName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Shape shape : cs.getChildren()) {
				if (shape.getGraphicsAlgorithm() instanceof Text) {
					Text text = (Text) shape.getGraphicsAlgorithm();
					pictogramName = text.getValue();
				}
			}
		}

		// retrieve name from business model
		String businessName = null;
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof Pool) {
			businessName = ((Pool) bo).getName();
		} else {
		  businessName = ((Lane) bo).getName();
		}

		// update needed, if names are different
		boolean updateNameNeeded = ((pictogramName == null && businessName != null) || 
				(pictogramName != null && !pictogramName.equals(businessName)));
		
		if (updateNameNeeded) {
			return Reason.createTrueReason(); //$NON-NLS-1$
		} else {
			return Reason.createFalseReason();
		}
	}

	public boolean update(IUpdateContext context) {
		// retrieve name from business model
		String businessName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof Pool) {
      businessName = ((Pool) bo).getName();
    } else {
      businessName = ((Lane) bo).getName();
    }

		// Set name in pictogram model
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Shape shape : cs.getChildren()) {
				if (shape.getGraphicsAlgorithm() instanceof Text) {
					Text text = (Text) shape.getGraphicsAlgorithm();
					text.setValue(businessName);
					return true;
				}
				if (shape.getGraphicsAlgorithm() instanceof MultiText) {
					MultiText text = (MultiText) shape.getGraphicsAlgorithm();
					text.setValue(businessName);
					return true;
				}
			}
		}

		return false;
	}
}
