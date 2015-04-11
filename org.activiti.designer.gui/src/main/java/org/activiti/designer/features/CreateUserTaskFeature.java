package org.activiti.designer.features;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.CustomProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.PluginImage;
import org.activiti.designer.integration.annotation.Property;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.integration.usertask.CustomUserTask;
import org.activiti.designer.property.extension.field.FieldInfo;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateUserTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "usertask";
  
  private String customUserTaskId;

  public CreateUserTaskFeature(IFeatureProvider fp) {
    super(fp, "UserTask", "Add user task");
  }
  
  public CreateUserTaskFeature(IFeatureProvider fp, String name, String description, String customUserTaskId) {
    super(fp, name, description);
    this.customUserTaskId = customUserTaskId;
  }

  @Override
  public Object[] create(ICreateContext context) {
    UserTask newUserTask = new UserTask();
    newUserTask.setExtensionId(customUserTaskId);
    
    // Process custom user tasks
    if (newUserTask.isExtended()) {

      CustomUserTask targetTask = findCustomUserTask(newUserTask);

      if (targetTask != null) {

        newUserTask.setName(targetTask.getName());
        
        final List<Class<CustomUserTask>> classHierarchy = new ArrayList<Class<CustomUserTask>>();
        final List<FieldInfo> fieldInfoObjects = new ArrayList<FieldInfo>();

        Class clazz = targetTask.getClass();
        classHierarchy.add(clazz);

        boolean hierarchyOpen = true;
        while (hierarchyOpen) {
          clazz = clazz.getSuperclass();
          if (CustomUserTask.class.isAssignableFrom(clazz)) {
            classHierarchy.add(clazz);
          } else {
            hierarchyOpen = false;
          }
        }
        
        for (final Class<CustomUserTask> currentClass : classHierarchy) {
          for (final Field field : currentClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Property.class)) {
              fieldInfoObjects.add(new FieldInfo(field));
            }
          }
        }
        
        for (final FieldInfo fieldInfo : fieldInfoObjects) {

          final Property property = fieldInfo.getPropertyAnnotation();
          
          CustomProperty customProperty = ExtensionUtil.getCustomProperty(newUserTask, fieldInfo.getFieldName());

          if (customProperty == null) {
            customProperty = new CustomProperty();
            newUserTask.getCustomProperties().add(customProperty);
          }

          customProperty.setId(ExtensionUtil.wrapCustomPropertyId(newUserTask, fieldInfo.getFieldName()));
          customProperty.setName(fieldInfo.getFieldName());
          if (StringUtils.isNotEmpty(property.defaultValue())) {
            customProperty.setSimpleValue(property.defaultValue());
          }
        }
      }
    }
    
    addObjectToContainer(context, newUserTask, "User Task");

    // activate direct editing after object creation
    getFeatureProvider().getDirectEditingInfo().setActive(true);

    return new Object[] { newUserTask };
  }
  
  private CustomUserTask findCustomUserTask(UserTask userTask) {
    CustomUserTask result = null;

    if (userTask.isExtended()) {

      final List<CustomUserTask> customUserTasks = ExtensionUtil.getCustomUserTasks(ActivitiUiUtil.getProjectFromDiagram(getDiagram()));

      for (final CustomUserTask customUserTask : customUserTasks) {
        if (userTask.getExtensionId().equals(customUserTask.getId())) {
          result = customUserTask;
          break;
        }
      }
    }
    return result;
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_USERTASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
