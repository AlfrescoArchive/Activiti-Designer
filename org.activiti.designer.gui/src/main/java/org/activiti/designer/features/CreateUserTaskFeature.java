package org.activiti.designer.features;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.CustomProperty;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.PluginImage;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.integration.annotation.Locale;
import org.activiti.designer.integration.annotation.Locales;
import org.activiti.designer.integration.annotation.Property;
import org.activiti.designer.integration.annotation.TaskName;
import org.activiti.designer.integration.annotation.TaskNames;
import org.activiti.designer.integration.usertask.CustomUserTask;
import org.activiti.designer.property.extension.field.FieldInfo;
import org.activiti.designer.util.bpmn.BpmnExtensions;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
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
    
    boolean isCustomNameSet = false;
    
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
          if (currentClass.isAnnotationPresent(TaskNames.class)) {
            TaskNames taskNames = currentClass.getAnnotation(TaskNames.class);
            if (taskNames.value() != null && taskNames.value().length > 0) {
              for (TaskName taskName : taskNames.value()) {
                setCustomTaskName(newUserTask, taskName.name(), taskName.locale());
                isCustomNameSet = true;
              }
            }
          }
          
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
          
          final Locales localesAnnotation = fieldInfo.getLocalesAnnotation();
          String localeDefaultValue = null;
          if (localesAnnotation != null && localesAnnotation.value() != null && localesAnnotation.value().length > 0) {
            String defaultLanguage = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_DEFAULT_LANGUAGE, ActivitiPlugin.getDefault());
            if (StringUtils.isNotEmpty(defaultLanguage)) {
              for (Locale locale : localesAnnotation.value()) {
                if (defaultLanguage.equalsIgnoreCase(locale.locale())) {
                  localeDefaultValue = locale.defaultValue();
                }
              }
            }
          }
          
          if (StringUtils.isNotEmpty(localeDefaultValue)) {  
            customProperty.setSimpleValue(localeDefaultValue);
            
          } else if (StringUtils.isNotEmpty(property.defaultValue())) {
            customProperty.setSimpleValue(property.defaultValue());
          }
        }
      }
    }
    
    if (isCustomNameSet == false) {
      addObjectToContainer(context, newUserTask, "User Task");
    } else {
      addObjectToContainer(context, newUserTask);
    }

    // activate direct editing after object creation
    getFeatureProvider().getDirectEditingInfo().setActive(true);

    return new Object[] { newUserTask };
  }
  
  protected CustomUserTask findCustomUserTask(UserTask userTask) {
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
  
  protected void setCustomTaskName(Object bo, String name, String language) {
    BaseElement element = (BaseElement) bo;
    List<ExtensionElement> extensionElements = null;
    if (element.getExtensionElements().containsKey(BpmnExtensions.LANGUAGE_EXTENSION)) {
      extensionElements = element.getExtensionElements().get(BpmnExtensions.LANGUAGE_EXTENSION);
    }
    
    if (extensionElements == null) {
      extensionElements = new ArrayList<ExtensionElement>();
      element.getExtensionElements().put(BpmnExtensions.LANGUAGE_EXTENSION, extensionElements);
    }
    
    ExtensionElement languageElement = null;
    for (ExtensionElement extensionElement : extensionElements) {
      List<ExtensionAttribute> languageAttributes = extensionElement.getAttributes().get("language");
      if (languageAttributes != null && languageAttributes.size() == 1) {
        String languageValue = languageAttributes.get(0).getValue();
        if (language.equals(languageValue)) {
          languageElement = extensionElement;
        }
      }
    }
    
    if (languageElement == null) {
      languageElement = new ExtensionElement();
      languageElement.setName(BpmnExtensions.LANGUAGE_EXTENSION);
      languageElement.setNamespace(BpmnExtensions.DESIGNER_EXTENSION_NAMESPACE);
      languageElement.setNamespacePrefix(BpmnExtensions.DESIGNER_EXTENSION_NAMESPACE_PREFIX);
      ExtensionAttribute languageAttribute = new ExtensionAttribute("language");
      languageAttribute.setValue(language);
      languageElement.addAttribute(languageAttribute);
      extensionElements.add(languageElement);
    }
    
    languageElement.setElementText(name);
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
