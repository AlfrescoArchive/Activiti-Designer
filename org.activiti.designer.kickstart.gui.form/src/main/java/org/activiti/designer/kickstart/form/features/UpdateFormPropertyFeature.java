package org.activiti.designer.kickstart.form.features;

import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;

/**
 * 
 * @author Frederik Heremans
 */
public class UpdateFormPropertyFeature extends AbstractUpdateFeature {

  public UpdateFormPropertyFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return bo instanceof FormPropertyDefinition;
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    return Reason.createTrueReason();
  }

  @Override
  public boolean update(IUpdateContext context) {
    return true;
  }

}
