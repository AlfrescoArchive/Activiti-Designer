package org.activiti.designer.kickstart.process.features;

import java.util.List;

import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.kickstart.process.PluginImage;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.extension.CustomServiceTaskContext;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddServiceTaskFeature extends AddTaskFeature {

  public AddServiceTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected String getIcon(Object bo) {

    if (ExtensionUtil.isCustomServiceTask(bo)) {
      ServiceTask serviceTask = (ServiceTask) bo;
      final List<CustomServiceTaskContext> customServiceTaskContexts = ExtensionUtil.getCustomServiceTaskContexts(
              ActivitiUiUtil.getProjectFromDiagram(getDiagram()));
      for (CustomServiceTaskContext customServiceTaskContext : customServiceTaskContexts) {
        if (customServiceTaskContext.getServiceTask().getId().equals(serviceTask.getExtensionId())) {
          return customServiceTaskContext.getShapeImageKey();
        }
      }
    }
    return PluginImage.IMG_SERVICETASK.getImageKey();
  }
}