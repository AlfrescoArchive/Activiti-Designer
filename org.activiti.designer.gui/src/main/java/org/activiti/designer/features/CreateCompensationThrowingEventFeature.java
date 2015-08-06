package org.activiti.designer.features;

import org.activiti.bpmn.model.CompensateEventDefinition;
import org.activiti.bpmn.model.ThrowEvent;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateCompensationThrowingEventFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "compensationintermediatethrowevent";

  public CreateCompensationThrowingEventFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "CompensationThrowingEvent", "Add compensation intermediate throwing event");
  }

  public Object[] create(ICreateContext context) {
    ThrowEvent throwEvent = new ThrowEvent();
    CompensateEventDefinition eventDef = new CompensateEventDefinition();
    throwEvent.getEventDefinitions().add(eventDef);
    addObjectToContainer(context, throwEvent, "CompensationThrowingEvent");

    // return newly created business object(s)
    return new Object[] { throwEvent };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_THROW_COMPENSATION.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
