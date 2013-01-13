package org.activiti.designer.features;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.ErrorEventDefinition;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateBoundaryErrorFeature extends AbstractCreateBPMNFeature {

  public static final String FEATURE_ID_KEY = "boundaryerror";

  public CreateBoundaryErrorFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "ErrorBoundaryEvent", "Add error boundary event");
  }

  public boolean canCreate(ICreateContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess == true || parentObject instanceof CallActivity == true || parentObject instanceof ServiceTask == true) {
      return true;
    }
    return false;
  }

  public Object[] create(ICreateContext context) {
    BoundaryEvent boundaryEvent = new BoundaryEvent();
    ErrorEventDefinition errorEvent = new ErrorEventDefinition();
    boundaryEvent.getEventDefinitions().add(errorEvent);

    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    ((Activity) parentObject).getBoundaryEvents().add(boundaryEvent);
    boundaryEvent.setAttachedToRef((Activity) parentObject);
    
    addObjectToContainer(context, boundaryEvent, "Error");

    // return newly created business object(s)
    return new Object[] { boundaryEvent };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_ENDEVENT_ERROR.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
