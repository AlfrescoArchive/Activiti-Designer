package org.activiti.designer.features;

import java.util.List;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.activiti.designer.property.extension.CustomServiceTaskContext;
import org.activiti.designer.property.extension.util.ExtensionUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddServiceTaskFeature extends AddTaskFeature {

  public AddServiceTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected String getIcon(EObject bo) {

    if (ExtensionUtil.isCustomServiceTask(bo)) {
      final List<CustomServiceTaskContext> customServiceTaskContexts = ExtensionUtil.getCustomServiceTaskContexts(ActivitiUiUtil
              .getProjectFromDiagram(getDiagram()));
      for (CustomServiceTaskContext customServiceTaskContext : customServiceTaskContexts) {
        if (customServiceTaskContext.getServiceTask().getId().equals(ExtensionUtil.getCustomServiceTaskId(bo))) {
          return customServiceTaskContext.getShapeImageKey();
        }
      }
    }
    return ActivitiImageProvider.IMG_SERVICETASK;
  }
}