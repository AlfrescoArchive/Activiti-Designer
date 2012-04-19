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

import org.activiti.designer.bpmn2.model.ActivitiListener;
import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.model.FieldExtensionModel;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;


/**
 * @author Tijs Rademakers
 */
public class BpmnBOUtil {
  
  public static Object getExecutionListenerBO(PictogramElement pe, Diagram diagram) {
  	Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
    Object bo = null;
    if(pe instanceof Diagram) {
      bo = model.getMainProcess();
    } else {
      bo = model.getFeatureProvider().getBusinessObjectForPictogramElement(pe);
    }
    return bo;
  }

  public static List<ActivitiListener> getListeners(Object bo, Diagram diagram) {
    List<ActivitiListener> listenerList = null;
    if(bo instanceof UserTask) {
    	listenerList = ((UserTask) bo).getTaskListeners();
    } else if(bo instanceof Activity) {
      listenerList = ((Activity) bo).getExecutionListeners();
    } else if(bo instanceof SequenceFlow) {
      listenerList = ((SequenceFlow) bo).getExecutionListeners();
    } else if(bo instanceof Process) {
      listenerList = ((Process) bo).getExecutionListeners();
    } else if(bo instanceof Pool) {
      Pool pool = ((Pool) bo);
      Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
      listenerList = model.getProcess(pool.getId()).getExecutionListeners();
    }
    return listenerList;
  }
  
  public static void addListener(Object bo, ActivitiListener listener, Diagram diagram) {
  	if(bo instanceof UserTask) {
    	((UserTask) bo).getTaskListeners().add(listener);
    } else if(bo instanceof Activity) {
      ((Activity) bo).getExecutionListeners().add(listener);
    } else if(bo instanceof SequenceFlow) {
      ((SequenceFlow) bo).getExecutionListeners().add(listener);
    } else if(bo instanceof Process) {
      ((Process) bo).getExecutionListeners().add(listener);
    } else if(bo instanceof Pool) {
      Pool pool = ((Pool) bo);
      Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
      model.getProcess(pool.getId()).getExecutionListeners().add(listener);
    }
  }
  
  public static void setListener(Object bo, ActivitiListener listener, int index, Diagram diagram) {
  	if(bo instanceof UserTask) {
    	((UserTask) bo).getTaskListeners().set(index, listener);
    } else if(bo instanceof Activity) {
      ((Activity) bo).getExecutionListeners().set(index, listener);
    } else if(bo instanceof SequenceFlow) {
      ((SequenceFlow) bo).getExecutionListeners().set(index, listener);
    } else if(bo instanceof Process) {
      ((Process) bo).getExecutionListeners().set(index, listener);
    } else if(bo instanceof Pool) {
      Pool pool = ((Pool) bo);
      Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
      model.getProcess(pool.getId()).getExecutionListeners().set(index, listener);
    }
  }
  
  public static void removeListener(Object bo, ActivitiListener listener, Diagram diagram) {
  	if(bo instanceof UserTask) {
    	((UserTask) bo).getTaskListeners().remove(listener);
    } else if(bo instanceof Activity) {
      ((Activity) bo).getExecutionListeners().remove(listener);
    } else if(bo instanceof SequenceFlow) {
      ((SequenceFlow) bo).getExecutionListeners().remove(listener);
    } else if(bo instanceof Process) {
      ((Process) bo).getExecutionListeners().remove(listener);
    } else if(bo instanceof Pool) {
      Pool pool = ((Pool) bo);
      Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
      model.getProcess(pool.getId()).getExecutionListeners().remove(listener);
    }
  }
  
  public static List<FieldExtensionModel> getFieldModelList(String fieldString) {
    String[] fieldStringList = fieldString.split("± ");
    List<FieldExtensionModel> fieldList = new ArrayList<FieldExtensionModel>();
    for (String field : fieldStringList) {
      String[] fieldExtensionStringList = field.split(":");
      if(fieldExtensionStringList != null && fieldExtensionStringList.length >= 2) {
        FieldExtensionModel fieldExtension = new FieldExtensionModel();
        fieldExtension.fieldName = fieldExtensionStringList[0];
        String expression = null;
        for(int i = 1; i < fieldExtensionStringList.length; i++) {
          if(i == 1) {
            expression = fieldExtensionStringList[i];
          } else {
            expression += ":" + fieldExtensionStringList[i];
          }
        }
        fieldExtension.expression = expression;
        fieldList.add(fieldExtension);
      }
    }
    return fieldList;
  }
}
