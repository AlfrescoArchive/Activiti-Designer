package org.activiti.designer.features;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.CancelEventDefinition;
import org.activiti.bpmn.model.Transaction;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateBoundaryCancelFeature extends AbstractCreateBPMNFeature {

  public static final String FEATURE_ID_KEY = "boundarycancel";

  public CreateBoundaryCancelFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "CancelBoundaryEvent", "Add cancel boundary event");
  }

  public boolean canCreate(ICreateContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof Transaction == true) {
      return true;
    }
    return false;
  }

  public Object[] create(ICreateContext context) {
    BoundaryEvent boundaryEvent = new BoundaryEvent();
    CancelEventDefinition cancelEvent = new CancelEventDefinition();
    boundaryEvent.getEventDefinitions().add(cancelEvent);

    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    ((Transaction) parentObject).getBoundaryEvents().add(boundaryEvent);
    boundaryEvent.setAttachedToRef((Activity) parentObject);
    
    addObjectToContainer(context, boundaryEvent, "Cancel");

    // return newly created business object(s)
    return new Object[] { boundaryEvent };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_EVENT_CANCEL.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
