package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.CallActivity;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateCallActivityFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "callactivity";

  public CreateCallActivityFeature(IFeatureProvider fp) {
    super(fp, "CallActivity", "Add call activity");
  }

  @Override
  public Object[] create(ICreateContext context) {
    CallActivity callActivity = new CallActivity();
    addObjectToContainer(context, callActivity, "Call activity");

    // return newly created business object(s)
    return new Object[] { callActivity };

  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_CALLACTIVITY.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
