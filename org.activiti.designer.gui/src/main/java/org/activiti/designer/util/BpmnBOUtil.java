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
/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.designer.util;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.HasExecutionListeners;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.ValuedDataObject;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;


/**
 * @author Tijs Rademakers
 */
public class BpmnBOUtil {
  
  public static Object getExecutionListenerBO(PictogramElement pe, Diagram diagram) {
  	BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
    Object bo = null;
    if(pe instanceof Diagram) {
      bo = model.getBpmnModel().getMainProcess();
    } else {
      bo = model.getFeatureProvider().getBusinessObjectForPictogramElement(pe);
    }
    return bo;
  }

  public static List<ActivitiListener> getExecutionListeners(Object bo, Diagram diagram) {
    List<ActivitiListener> listenerList = null;
    if (bo instanceof HasExecutionListeners) {
      listenerList = ((HasExecutionListeners) bo).getExecutionListeners();
    } else if (bo instanceof Pool) {
      Pool pool = ((Pool) bo);
      BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
      listenerList = model.getBpmnModel().getProcess(pool.getId()).getExecutionListeners();
    }
    return listenerList;
  }
  
  public static void addExecutionListener(Object bo, ActivitiListener listener, Diagram diagram) {
  	if (bo instanceof HasExecutionListeners) {
    	((HasExecutionListeners) bo).getExecutionListeners().add(listener);
    } else if (bo instanceof Pool) {
      Pool pool = ((Pool) bo);
      BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
      model.getBpmnModel().getProcess(pool.getId()).getExecutionListeners().add(listener);
    }
  }
  
  public static void setExecutionListener(Object bo, ActivitiListener listener, int index, Diagram diagram) {
  	if (bo instanceof HasExecutionListeners) {
    	((HasExecutionListeners) bo).getExecutionListeners().set(index, listener);
    } else if (bo instanceof Pool) {
      Pool pool = ((Pool) bo);
      BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
      model.getBpmnModel().getProcess(pool.getId()).getExecutionListeners().set(index, listener);
    }
  }
  
  public static void setExecutionListeners(Object bo, List<ActivitiListener> listeners, Diagram diagram) {
    if (bo instanceof HasExecutionListeners) {
      ((HasExecutionListeners) bo).setExecutionListeners(listeners);
    } else if (bo instanceof Pool) {
      Pool pool = ((Pool) bo);
      BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
      model.getBpmnModel().getProcess(pool.getId()).setExecutionListeners(listeners);
    }
  }
  
  public static void removeExecutionListener(Object bo, int index, Diagram diagram) {
  	if (bo instanceof HasExecutionListeners) {
    	((HasExecutionListeners) bo).getExecutionListeners().remove(index);
    } else if (bo instanceof Pool) {
      Pool pool = ((Pool) bo);
      BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
      model.getBpmnModel().getProcess(pool.getId()).getExecutionListeners().remove(index);
    }
  }

  public static List<ValuedDataObject> getDataObjects(Object bo, Diagram diagram) {
    List<ValuedDataObject> dataObjects = null;
    if (bo instanceof Process) {
        dataObjects = ((Process) bo).getDataObjects();
    } else if (bo instanceof SubProcess) {
        dataObjects = ((SubProcess) bo).getDataObjects();
    }
    return dataObjects;
  }
	  
  public static void addDataObject(Object bo, ValuedDataObject dataObject, Diagram diagram) {
  	if (bo instanceof Process) {
      ((Process) bo).getDataObjects().add(dataObject);
    } else if (bo instanceof SubProcess) {
      ((SubProcess) bo).getDataObjects().add(dataObject);
    }
  }
	  
  public static void setDataObject(Object bo, ValuedDataObject dataObject, int index, Diagram diagram) {
  	if (bo instanceof Process) {
      ((Process) bo).getDataObjects().set(index, dataObject);
    } else if (bo instanceof SubProcess) {
      ((SubProcess) bo).getDataObjects().set(index, dataObject);
    }
  }
	  
  public static void removeDataObject(Object bo, int index, Diagram diagram) {
  	if (bo instanceof Process) {
     ((Process) bo).getDataObjects().remove(index);
	} else if (bo instanceof SubProcess) {
	  ((SubProcess) bo).getDataObjects().remove(index);
	}
  }
	  
  public static List<FieldExtension> getFieldModelList(String fieldString) {
    String[] fieldStringList = fieldString.split("|");
    List<FieldExtension> fieldList = new ArrayList<FieldExtension>();
    for (String field : fieldStringList) {
      String[] fieldExtensionStringList = field.split(":");
      if(fieldExtensionStringList != null && fieldExtensionStringList.length >= 2) {
        FieldExtension fieldExtension = new FieldExtension();
        fieldExtension.setFieldName(fieldExtensionStringList[0]);
        String expression = null;
        for(int i = 1; i < fieldExtensionStringList.length; i++) {
          if(i == 1) {
            expression = fieldExtensionStringList[i];
          } else {
            expression += ":" + fieldExtensionStringList[i];
          }
        }
        if (expression.contains("${") || expression.contains("#{")) {
          fieldExtension.setExpression(expression);
        } else {
          fieldExtension.setStringValue(expression);
        }
        fieldList.add(fieldExtension);
      }
    }
    return fieldList;
  }
}
