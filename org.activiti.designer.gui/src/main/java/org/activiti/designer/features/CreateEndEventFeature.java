package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.EndEvent;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateEndEventFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "endevent";

  public CreateEndEventFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "EndEvent", "Add end event");
  }

  public Object[] create(ICreateContext context) {
    EndEvent endEvent = new EndEvent();
    addObjectToContainer(context, endEvent, "End");

    // return newly created business object(s)
    return new Object[] { endEvent };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_ENDEVENT_NONE.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
