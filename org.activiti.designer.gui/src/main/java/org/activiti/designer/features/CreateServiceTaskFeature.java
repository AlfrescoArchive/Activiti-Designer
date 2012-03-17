package org.activiti.designer.features;

import java.util.List;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.CustomProperty;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.property.extension.util.ExtensionUtil;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.eclipse.ExtensionConstants;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateServiceTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "servicetask";

  private String customServiceTaskId;

  public CreateServiceTaskFeature(IFeatureProvider fp) {
    super(fp, "ServiceTask", "Add service task");
  }

  public CreateServiceTaskFeature(IFeatureProvider fp, String name, String description, String customServiceTaskId) {
    super(fp, name, description);
    this.customServiceTaskId = customServiceTaskId;
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
  }

  @Override
  public Object[] create(ICreateContext context) {
    ServiceTask newServiceTask = new ServiceTask();
    addObjectToContainer(context, newServiceTask, "Service Task");

    // Process custom service tasks
    if (this.customServiceTaskId != null) {

      // Customize the name displayed by default
      final List<CustomServiceTask> customServiceTasks = ExtensionUtil.getCustomServiceTasks(ActivitiUiUtil.getProjectFromDiagram(getDiagram()));

      CustomServiceTask targetTask = null;

      for (final CustomServiceTask customServiceTask : customServiceTasks) {
        if (this.customServiceTaskId.equals(customServiceTask.getId())) {
          targetTask = customServiceTask;
          break;
        }
      }

      if (targetTask != null) {
        // Create custom property containing task name
        CustomProperty customServiceTaskProperty = new CustomProperty();

        customServiceTaskProperty.setId(ExtensionUtil.wrapCustomPropertyId(newServiceTask, ExtensionConstants.PROPERTY_ID_CUSTOM_SERVICE_TASK));
        customServiceTaskProperty.setName(ExtensionConstants.PROPERTY_ID_CUSTOM_SERVICE_TASK);
        customServiceTaskProperty.setSimpleValue(this.customServiceTaskId);

        newServiceTask.getCustomProperties().add(customServiceTaskProperty);
        newServiceTask.setImplementation(targetTask.getRuntimeClassname());
        newServiceTask.setName(targetTask.getName());
      }

    }
    
    return new Object[] { newServiceTask };
  }

  @Override
  public String getCreateImageId() {
    return ActivitiImageProvider.IMG_SERVICETASK;
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected Class getFeatureClass() {
    return new ServiceTask().getClass();
  }

}