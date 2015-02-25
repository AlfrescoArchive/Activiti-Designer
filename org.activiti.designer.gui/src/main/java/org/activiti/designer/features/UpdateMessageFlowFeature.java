package org.activiti.designer.features;

import org.activiti.bpmn.model.MessageFlow;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.TextUtil;
import org.activiti.designer.util.bpmn.BpmnExtensionUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class UpdateMessageFlowFeature extends AbstractUpdateFeature {

	public UpdateMessageFlowFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canUpdate(IUpdateContext context) {
	  Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return (bo instanceof MessageFlow);
	}

  public IReason updateNeeded(IUpdateContext context) {
		String pictogramName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
    Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    MessageFlow messageFlow = (MessageFlow) bo;
    
		if (pictogramElement instanceof FreeFormConnection) {
		
      EList<ConnectionDecorator> decoratorList = ((FreeFormConnection) pictogramElement).getConnectionDecorators();
      for (ConnectionDecorator decorator : decoratorList) {
        if (decorator.getGraphicsAlgorithm() instanceof MultiText) {
          MultiText text = (MultiText) decorator.getGraphicsAlgorithm();
          pictogramName = text.getValue();
        }
      }
		}

		String businessName = BpmnExtensionUtil.getMessageFlowName(messageFlow, ActivitiPlugin.getDefault());

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
		if (bo instanceof MessageFlow) {
		  MessageFlow messageFlow = (MessageFlow) bo;
			businessName = BpmnExtensionUtil.getMessageFlowName(messageFlow, ActivitiPlugin.getDefault());
		}
		
		boolean updated = false;

		// Set name in pictogram model
		if (pictogramElement instanceof FreeFormConnection) {
    
      EList<ConnectionDecorator> decoratorList = ((FreeFormConnection) pictogramElement).getConnectionDecorators();
      for (ConnectionDecorator decorator : decoratorList) {
        if (decorator.getGraphicsAlgorithm() instanceof MultiText) {
          MultiText text = (MultiText) decorator.getGraphicsAlgorithm();
          text.setValue(businessName);
          TextUtil.setTextSize(text);
          updated = true;
        }
      }
    }

		return updated;
	}
}
