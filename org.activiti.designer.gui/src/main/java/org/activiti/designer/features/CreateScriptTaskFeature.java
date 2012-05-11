package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.ScriptTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateScriptTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "scripttask";

  public CreateScriptTaskFeature(IFeatureProvider fp) {
    super(fp, "ScriptTask", "Add script task");
  }

  @Override
  public Object[] create(ICreateContext context) {
    ScriptTask newScriptTask = new ScriptTask();
    addObjectToContainer(context, newScriptTask, "Script Task");

    return new Object[] { newScriptTask };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_SCRIPTTASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
