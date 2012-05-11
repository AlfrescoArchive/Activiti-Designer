package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.TimerEventDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateBoundaryTimerFeature extends AbstractCreateBPMNFeature {

  public static final String FEATURE_ID_KEY = "boundarytimer";

  public CreateBoundaryTimerFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "TimerBoundaryEvent", "Add timer boundary event");
  }

  public boolean canCreate(ICreateContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof Activity == true) {

      return true;
    }
    return false;
  }

  public Object[] create(ICreateContext context) {
    BoundaryEvent boundaryEvent = new BoundaryEvent();
    TimerEventDefinition timerEvent = new TimerEventDefinition();
    boundaryEvent.getEventDefinitions().add(timerEvent);

    boundaryEvent.setId(getNextId(boundaryEvent));

    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    ((Activity) parentObject).getBoundaryEvents().add(boundaryEvent);
    boundaryEvent.setAttachedToRef((Activity) parentObject);

    // do the add
    addGraphicalRepresentation(context, boundaryEvent);

    // return newly created business object(s)
    return new Object[] { boundaryEvent };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_BOUNDARY_TIMER.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
