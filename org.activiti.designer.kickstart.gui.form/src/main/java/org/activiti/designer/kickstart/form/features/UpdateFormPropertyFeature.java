package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * 
 * @author Frederik Heremans
 */
public class UpdateFormPropertyFeature extends AbstractUpdateFeature {

  public UpdateFormPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return bo instanceof FormPropertyDefinition;
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return Reason.createTrueReason();
  }

  @Override
  public boolean update(IUpdateContext context) {
    if (context.getPictogramElement() instanceof ContainerShape) {
      FormPropertyDefinition propdef = (FormPropertyDefinition) getBusinessObjectForPictogramElement(context
          .getPictogramElement());

      if (propdef != null) {
        MultiText text = findTextInChildren((Shape) context.getPictogramElement());
        if (text != null) {
          text.setValue(propdef.getName());
        }
        return true;
      }
    }
    return false;
  }
  
  protected MultiText findTextInChildren(Shape shape) {
    if(shape.getGraphicsAlgorithm() instanceof MultiText) {
      return (MultiText) shape.getGraphicsAlgorithm();
    }
    if(shape instanceof ContainerShape) {
      for(Shape child : ((ContainerShape) shape).getChildren()) {
        return findTextInChildren(child);
      }
    }
    return null;
  }

}
