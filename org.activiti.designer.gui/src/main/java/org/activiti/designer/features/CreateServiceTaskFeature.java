package org.activiti.designer.features;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.CustomProperty;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.PluginImage;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.integration.annotation.Locale;
import org.activiti.designer.integration.annotation.Locales;
import org.activiti.designer.integration.annotation.Property;
import org.activiti.designer.integration.annotation.TaskName;
import org.activiti.designer.integration.annotation.TaskNames;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.integration.servicetask.DelegateType;
import org.activiti.designer.property.extension.field.FieldInfo;
import org.activiti.designer.util.bpmn.BpmnExtensions;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
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
    
    boolean isCustomNameSet = false;

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
            
            if (currentClass.isAnnotationPresent(TaskNames.class)) {
              TaskNames taskNames = currentClass.getAnnotation(TaskNames.class);
              if (taskNames.value() != null && taskNames.value().length > 0) {
                for (TaskName taskName : taskNames.value()) {
                  setCustomTaskName(newServiceTask, taskName.name(), taskName.locale());
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
      addObjectToContainer(context, newServiceTask, newServiceTask.getName());
    } else {
      addObjectToContainer(context, newServiceTask);
    }

    return new Object[] { newServiceTask };
  }

  protected CustomServiceTask findCustomServiceTask(ServiceTask serviceTask) {
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
    return PluginImage.IMG_SERVICETASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}