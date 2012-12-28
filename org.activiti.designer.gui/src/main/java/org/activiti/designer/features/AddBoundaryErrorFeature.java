package org.activiti.designer.features;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;

public class AddBoundaryErrorFeature extends AbstractAddBoundaryFeature {

  public AddBoundaryErrorFeature(IFeatureProvider fp) {
    super(fp);
  }
  
  @Override
  protected String getImageKey() {
    return PluginImage.IMG_BOUNDARY_ERROR.getImageKey();
  }

  @Override
  public boolean canAdd(IAddContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess == false && parentObject instanceof CallActivity == false && parentObject instanceof ServiceTask == false) {

      return false;
    }
    if (context.getNewObject() instanceof BoundaryEvent == false) {
      return false;
    }
    return true;
  }
}