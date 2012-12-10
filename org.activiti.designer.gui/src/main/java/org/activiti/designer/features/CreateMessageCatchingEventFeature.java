package org.activiti.designer.features;

import org.activiti.bpmn.model.IntermediateCatchEvent;
import org.activiti.bpmn.model.MessageEventDefinition;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateMessageCatchingEventFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "messageintermediatecatchevent";

  public CreateMessageCatchingEventFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "MessageCatchingEvent", "Add message intermediate catching event");
  }

  public Object[] create(ICreateContext context) {
    IntermediateCatchEvent catchEvent = new IntermediateCatchEvent();
    MessageEventDefinition eventDef = new MessageEventDefinition();
    catchEvent.getEventDefinitions().add(eventDef);
    addObjectToContainer(context, catchEvent, "MessageCatchEvent");

    // return newly created business object(s)
    return new Object[] { catchEvent };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_STARTEVENT_MESSAGE.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
