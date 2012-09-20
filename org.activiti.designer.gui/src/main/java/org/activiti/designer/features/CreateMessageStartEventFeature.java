package org.activiti.designer.features;

/**
 * @author Saeid Mirzaei
 */

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.EventSubProcess;
import org.activiti.designer.bpmn2.model.MessageEventDefinition;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateMessageStartEventFeature extends AbstractCreateBPMNFeature {

  public static final String FEATURE_ID_KEY = "messagestartevent";

  public CreateMessageStartEventFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "MessageStartEvent", "Add message start event");
  }

  public boolean canCreate(ICreateContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof EventSubProcess)
      return false;
    return super.canCreate(context);
  }

  public Object[] create(ICreateContext context) {
    StartEvent startEvent = new StartEvent();
    MessageEventDefinition  timerEvent = new MessageEventDefinition();
    startEvent.getEventDefinitions().add(timerEvent);
    addObjectToContainer(context, startEvent, "Message start");

    // return newly created business object(s)
    return new Object[] { startEvent };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_STARTEVENT_MESSAGEICON.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
