package org.activiti.designer.features;

import org.activiti.bpmn.model.CancelEventDefinition;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateCancelEndEventFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "cancelendevent";

  public CreateCancelEndEventFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "CancelEndEvent", "Add cancel end event");
  }

  public Object[] create(ICreateContext context) {
    EndEvent endEvent = new EndEvent();
    CancelEventDefinition eventDef = new CancelEventDefinition();
    endEvent.getEventDefinitions().add(eventDef);
    addObjectToContainer(context, endEvent, "CancelEnd");

    // return newly created business object(s)
    return new Object[] { endEvent };
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
