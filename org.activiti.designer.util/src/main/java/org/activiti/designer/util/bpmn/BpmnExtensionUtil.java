package org.activiti.designer.util.bpmn;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.MessageFlow;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.TextAnnotation;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class BpmnExtensionUtil {

  public static String getFlowElementName(FlowElement flowElement, AbstractUIPlugin plugin) {
    return getElementName(flowElement.getName(), flowElement, plugin);
  }
  
  public static String getMessageFlowName(MessageFlow messageFlow, AbstractUIPlugin plugin) {
    return getElementName(messageFlow.getName(), messageFlow, plugin);
  }
  
  public static String getPoolName(Pool pool, AbstractUIPlugin plugin) {
    return getElementName(pool.getName(), pool, plugin);
  }
  
  public static String getLaneName(Lane lane, AbstractUIPlugin plugin) {
    return getElementName(lane.getName(), lane, plugin);
  }
  
  public static String getTextAnnotationText(TextAnnotation annotation, AbstractUIPlugin plugin) {
    return getElementName(annotation.getText(), annotation, plugin);
  }
  
  protected static String getElementName(String name, BaseElement element, AbstractUIPlugin plugin) {
    String resultName = null;
    String defaultLanguage = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_DEFAULT_LANGUAGE, plugin);
    if (StringUtils.isNotEmpty(defaultLanguage)) {
      List<ExtensionElement> languageExtensions = element.getExtensionElements().get(BpmnExtensions.LANGUAGE_EXTENSION);
      if (languageExtensions != null && languageExtensions.size() > 0) {
        for (ExtensionElement extensionElement : languageExtensions) {
          List<ExtensionAttribute> languageAttributes = extensionElement.getAttributes().get("language");
          if (languageAttributes != null && languageAttributes.size() == 1) {
            String languageValue = languageAttributes.get(0).getValue();
            if (defaultLanguage.equals(languageValue)) {
              resultName = extensionElement.getElementText();
            }
          }
        }
      } else {
        resultName = name;
      }
    } else {
      resultName = name;
    }
    return resultName;
  }
  
  public static void setFlowElementName(FlowElement flowElement, String name, AbstractUIPlugin plugin) {
    String defaultLanguage = null;
    List<String> languages = PreferencesUtil.getStringArray(Preferences.ACTIVITI_LANGUAGES, plugin);
    if (languages != null && languages.size() > 0) {
      defaultLanguage = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_DEFAULT_LANGUAGE, plugin);
    }
    
    if (StringUtils.isNotEmpty(defaultLanguage)) {
      setElementName(flowElement, name, defaultLanguage, plugin);
    } else {
      flowElement.setName(name);
    }
  }
  
  public static void setTextAnnotationText(TextAnnotation annotation, String text, AbstractUIPlugin plugin) {
    String defaultLanguage = null;
    List<String> languages = PreferencesUtil.getStringArray(Preferences.ACTIVITI_LANGUAGES, plugin);
    if (languages != null && languages.size() > 0) {
      defaultLanguage = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_DEFAULT_LANGUAGE, plugin);
    }
    
    if (StringUtils.isNotEmpty(defaultLanguage)) {
      setElementName(annotation, text, defaultLanguage, plugin);
    } else {
      annotation.setText(text);
    }
  }
  
  protected static void setElementName(BaseElement element, String name, String defaultLanguage, AbstractUIPlugin plugin) {
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
        if (defaultLanguage.equals(languageValue)) {
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
      languageAttribute.setValue(defaultLanguage);
      languageElement.addAttribute(languageAttribute);
      extensionElements.add(languageElement);
    }
    
    languageElement.setElementText(name);
  }
}
