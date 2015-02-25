package org.activiti.designer.features;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.CustomProperty;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.PluginImage;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.integration.servicetask.DelegateType;
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.property.extension.field.FieldInfo;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

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

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Object[] create(ICreateContext context) {
    ServiceTask newServiceTask = new ServiceTask();
    newServiceTask.setName("Service Task");
    newServiceTask.setExtensionId(customServiceTaskId);

    // Process custom service tasks
    if (newServiceTask.isExtended()) {

      CustomServiceTask targetTask = findCustomServiceTask(newServiceTask);

      if (targetTask != null) {

        // What should happen if the class contain more than one annotations?
        switch (targetTask.getDelegateType()) {
        case JAVA_DELEGATE_CLASS:
          newServiceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
          newServiceTask.setImplementation(targetTask.getDelegateSpecification());
          break;
        case EXPRESSION:
          newServiceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
          newServiceTask.setImplementation(targetTask.getDelegateSpecification());
          break;
        case JAVA_DELEGATE_EXPRESSION:
          newServiceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
          newServiceTask.setImplementation(targetTask.getDelegateSpecification());
          break;
        case NONE:
          break;
        default:
          break;
        }

        newServiceTask.setName(targetTask.getName());
        
        final List<Class<CustomServiceTask>> classHierarchy = new ArrayList<Class<CustomServiceTask>>();
        final List<FieldInfo> fieldInfoObjects = new ArrayList<FieldInfo>();

        Class clazz = targetTask.getClass();
        classHierarchy.add(clazz);

        boolean hierarchyOpen = true;
        while (hierarchyOpen) {
          clazz = clazz.getSuperclass();
          if (CustomServiceTask.class.isAssignableFrom(clazz)) {
            classHierarchy.add(clazz);
          } else {
            hierarchyOpen = false;
          }
        }

        // only process properties if the type is not an expression.
        if (DelegateType.JAVA_DELEGATE_CLASS == targetTask.getDelegateType()) {
          for (final Class<CustomServiceTask> currentClass : classHierarchy) {
            for (final Field field : currentClass.getDeclaredFields()) {
              if (field.isAnnotationPresent(Property.class)) {
                fieldInfoObjects.add(new FieldInfo(field));
              }
            }
          }
        }
        
        for (final FieldInfo fieldInfo : fieldInfoObjects) {

          final Property property = fieldInfo.getPropertyAnnotation();
          
          CustomProperty customProperty = ExtensionUtil.getCustomProperty(newServiceTask, fieldInfo.getFieldName());

          if (customProperty == null) {
            customProperty = new CustomProperty();
            newServiceTask.getCustomProperties().add(customProperty);
          }

          customProperty.setId(ExtensionUtil.wrapCustomPropertyId(newServiceTask, fieldInfo.getFieldName()));
          customProperty.setName(fieldInfo.getFieldName());
          if (StringUtils.isNotEmpty(property.defaultValue())) {
            customProperty.setSimpleValue(property.defaultValue());
          }
        }
      }
    }

    addObjectToContainer(context, newServiceTask, newServiceTask.getName());

    return new Object[] { newServiceTask };
  }

  private CustomServiceTask findCustomServiceTask(ServiceTask serviceTask) {
    CustomServiceTask result = null;

    if (serviceTask.isExtended()) {

      final List<CustomServiceTask> customServiceTasks = ExtensionUtil.getCustomServiceTasks(ActivitiUiUtil.getProjectFromDiagram(getDiagram()));

      for (final CustomServiceTask customServiceTask : customServiceTasks) {
        if (serviceTask.getExtensionId().equals(customServiceTask.getId())) {
          result = customServiceTask;
          break;
        }
      }
    }
    return result;
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_SERVICETASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}