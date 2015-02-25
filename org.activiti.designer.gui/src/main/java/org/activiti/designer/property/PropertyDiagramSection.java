/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.bpmn.BpmnExtensions;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyDiagramSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  protected Text idText;
  protected Text nameText;
  protected Text namespaceText;
  protected Text documentationText;
  protected Text candidateStarterUsersText;
  protected Text candidateStarterGroupsText;
  protected List<String> languages;
  protected Map<Text, String> languageTextMap = new HashMap<Text, String>();
	
  protected BpmnMemoryModel model = null;
	protected Process currentProcess = null;
	
	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    idText = createTextControl(false);
    createLabel("Id", idText);
    languages = PreferencesUtil.getStringArray(Preferences.ACTIVITI_LANGUAGES, ActivitiPlugin.getDefault());
    if (languages != null && languages.size() > 0) {
      for (String language : languages) {
        Text languageText = createTextControl(false);
        createLabel("Name (" + language + ")", languageText);
        languageTextMap.put(languageText, language);  
      }
    } else {
      nameText = createTextControl(false);
      createLabel("Name", nameText);
    }
    namespaceText = createTextControl(false);
    createLabel("Namespace", namespaceText);
    candidateStarterUsersText = createTextControl(false);
    createLabel("Candidate start users (comma separated)", candidateStarterUsersText);
    candidateStarterGroupsText = createTextControl(false);
    createLabel("Candidate start groups (comma separated)", candidateStarterGroupsText);
    documentationText = createTextControl(true);
    createLabel("Documentation", documentationText);
  }
	
	

  @Override
  public void refresh() {
    Object businessObject = getBusinessObject(getSelectedPictogramElement());
    
    model = getModel(getSelectedPictogramElement());
    if (businessObject instanceof Process) {
      currentProcess = (Process) businessObject;
      if (model.getBpmnModel().getPools().size() > 0) {
        setEnabled(false);
      } else {
        setEnabled(true);
      }
      
    } else if (businessObject instanceof Pool) {
      Pool pool = (Pool) businessObject;
      currentProcess = model.getBpmnModel().getProcess(pool.getId());
      setEnabled(true);
    }
    
    super.refresh();
  }



  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    if (languages != null && languages.size() > 0) {
      for (Text languageText : languageTextMap.keySet()) {
        if (control == languageText) {
          return getName(businessObject, languageTextMap.get(languageText));
        }
      }
    }
    
    if (control == idText) {
      return currentProcess.getId();
      
    } else if (control == nameText) {
      return currentProcess.getName();
    
    } else if (control == namespaceText) {
      if (StringUtils.isNotEmpty(model.getBpmnModel().getTargetNamespace())) {
        return model.getBpmnModel().getTargetNamespace();
      } else {
        return "http://www.activiti.org/test";
      }
      
    } else if (control == candidateStarterUsersText) {
      return getCommaSeperatedString(currentProcess.getCandidateStarterUsers());
    
    } else if (control == candidateStarterGroupsText) {
      return getCommaSeperatedString(currentProcess.getCandidateStarterGroups());
      
    } else if (control == documentationText) {
      return currentProcess.getDocumentation();
    }
    
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, final Object businessObject) {
    if (languages != null && languages.size() > 0) {
      for (Text languageText : languageTextMap.keySet()) {
        if (control == languageText) {
          setName(businessObject, languageText.getText(), languageTextMap.get(languageText));
        }
      }
    }
    
    if (control == idText) {
      currentProcess.setId(idText.getText());
      if (businessObject instanceof Pool) {
        Pool pool = (Pool) businessObject;
        pool.setProcessRef(currentProcess.getId());
      } else if (businessObject instanceof Process) {
        ((Process) businessObject).setId(currentProcess.getId());
      }
      
    } else if (control == nameText) {
      currentProcess.setName(nameText.getText());
      if (businessObject instanceof Process) {
        ((Process) businessObject).setName(currentProcess.getName());
      }
    
    } else if (control == namespaceText) {
      model.getBpmnModel().setTargetNamespace(namespaceText.getText());
    
    } else if (control == candidateStarterUsersText) {
      currentProcess.setCandidateStarterUsers(commaSeperatedStringToList(candidateStarterUsersText.getText()));
      if (businessObject instanceof Process) {
        ((Process) businessObject).setCandidateStarterUsers(currentProcess.getCandidateStarterUsers());
      }
    
    } else if (control == candidateStarterGroupsText) {
      currentProcess.setCandidateStarterGroups(commaSeperatedStringToList(candidateStarterGroupsText.getText()));
      if (businessObject instanceof Process) {
        ((Process) businessObject).setCandidateStarterGroups(currentProcess.getCandidateStarterGroups());
      }
    
    } else if (control == documentationText) {
      currentProcess.setDocumentation(documentationText.getText());
      if (businessObject instanceof Process) {
        ((Process) businessObject).setDocumentation(currentProcess.getDocumentation());
      }
    }
  }
	
  protected void setEnabled(boolean enabled) {
	  idText.setEnabled(enabled);
	  if (languageTextMap.size() > 0) {
      for (Text languageText : languageTextMap.keySet()) {
        languageText.setEnabled(enabled);
      }
    } else {
      nameText.setEnabled(enabled);
    }
    namespaceText.setEnabled(enabled);
    documentationText.setEnabled(enabled);
    candidateStarterUsersText.setEnabled(enabled);
    candidateStarterGroupsText.setEnabled(enabled);
	}
	
	protected String getName(Object bo, String language) {
    BaseElement element = (BaseElement) bo;
    String resultValue = null;
    if (element.getExtensionElements().containsKey(BpmnExtensions.LANGUAGE_EXTENSION)) {
      List<ExtensionElement> extensionElements = element.getExtensionElements().get(BpmnExtensions.LANGUAGE_EXTENSION);
      if (extensionElements != null && extensionElements.size() > 0) {
        for (ExtensionElement extensionElement : extensionElements) {
          List<ExtensionAttribute> languageAttributes = extensionElement.getAttributes().get("language");
          if (languageAttributes != null && languageAttributes.size() == 1) {
            String languageValue = languageAttributes.get(0).getValue();
            if (language.equals(languageValue)) {
              resultValue = extensionElement.getElementText();
            }
          }
        }
      }
    }
    
    if (resultValue != null && resultValue.length() > 0) {
      return resultValue;
    } else {
      return "";
    }
  }
	
	protected void setName(Object bo, String name, String language) {
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
}
