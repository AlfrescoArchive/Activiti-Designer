/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.MessageFlow;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.bpmn.BpmnExtensions;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyGeneralSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Text idText;
  private Text nameText;
  private List<String> languages;
  private Map<Text, String> languageTextMap = new HashMap<Text, String>();
  
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
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    BaseElement element = (BaseElement) businessObject;
    if (control == idText) {
      return element.getId();
      
    } else if (languages != null && languages.size() > 0) {
    	for (Text languageText : languageTextMap.keySet()) {
  			if (control == languageText) {
  				return getName(businessObject, languageTextMap.get(languageText));
  			}
  		}
    	
    } else if (control == nameText) {
      return getName(businessObject);
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, final Object businessObject) {
    BaseElement element = (BaseElement) businessObject;
    if (control == idText) {
      if (element instanceof FlowNode) {
        updateParentLane(element.getId(), idText.getText());
        updateFlows(element, idText.getText());
      }
      element.setId(idText.getText());
      
    } else if (languages != null && languages.size() > 0) {
    	for (Text languageText : languageTextMap.keySet()) {
  			if (control == languageText) {
  				setName(businessObject, languageText.getText(), languageTextMap.get(languageText));
  			}
  		}
      
    } else if (control == nameText) {
      setName(businessObject, nameText.getText());
    }
  }

  protected void updateParentLane(String oldElementId, String newElementId) {
    BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
    for (Process process : model.getBpmnModel().getProcesses()) {
      for (Lane lane : process.getLanes()) {
        if (lane.getFlowReferences().contains(oldElementId)) {
          lane.getFlowReferences().remove(oldElementId);
          lane.getFlowReferences().add(newElementId);
          return;
        }
      }
    }
  }

  protected void updateFlows(BaseElement element, String newElementId) {
    BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
    FlowNode flowNode = (FlowNode) element;
    for (Process process : model.getBpmnModel().getProcesses()) {
      updateSequenceFlows(process, flowNode.getId(), newElementId);
    }
  }
  
  protected void updateSequenceFlows(FlowElementsContainer container, String oldElementId, String newElementId) {
    for (FlowElement flowElement : container.getFlowElements()) {
      if (flowElement instanceof SequenceFlow) {
        SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
        if (sequenceFlow.getSourceRef().equals(oldElementId)) {
          sequenceFlow.setSourceRef(newElementId);
        }
        
        if (sequenceFlow.getTargetRef().equals(oldElementId)) {
          sequenceFlow.setTargetRef(newElementId);
        }
        
      } else if(flowElement instanceof SubProcess) {
        SubProcess subProcess = (SubProcess) flowElement;
        updateSequenceFlows(subProcess, oldElementId, newElementId);
      }
    }
  }
  
  protected String getName(Object bo) {
    String name = null;
    if (bo instanceof FlowElement) {
      name = ((FlowElement) bo).getName();
    } else if (bo instanceof Pool) {
      name = ((Pool) bo).getName();
    } else if (bo instanceof Lane) {
      name = ((Lane) bo).getName();
    } else if (bo instanceof MessageFlow) {
      name = ((MessageFlow) bo).getName();
    }
    return name;
  }
  
  protected void setName(Object bo, String name) {
    if (bo instanceof FlowElement) {
      ((FlowElement) bo).setName(name);
    } else if (bo instanceof Pool) {
      ((Pool) bo).setName(name);
    } else if (bo instanceof Lane) {
      ((Lane) bo).setName(name);
    } else if (bo instanceof MessageFlow) {
      ((MessageFlow) bo).setName(name);
    }
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
