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

package org.activiti.designer.export.bpmn20.export;

import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.CustomProperty;
import org.activiti.designer.bpmn2.model.FieldExtension;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.util.eclipse.ExtensionConstants;

/**
 * @author Tijs Rademakers
 */
public class ServiceTaskExport implements ActivitiNamespaceConstants {

  public static void createServiceTask(Object object, XMLStreamWriter xtw) throws Exception {
    ServiceTask serviceTask = (ServiceTask) object;
    // start ServiceTask element
    xtw.writeStartElement("serviceTask");
    xtw.writeAttribute("id", serviceTask.getId());
    if (serviceTask.getName() != null) {
      xtw.writeAttribute("name", serviceTask.getName());
    }
    DefaultFlowExport.createDefaultFlow(serviceTask, xtw);
    AsyncActivityExport.createAsyncAttribute(serviceTask, xtw);
    ImplementationValueExport.writeImplementationValue(xtw, EXECUTION_LISTENER, serviceTask.getImplementationType(), serviceTask.getImplementation(), true);

    if (serviceTask.getResultVariableName() != null && serviceTask.getResultVariableName().length() > 0) {
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "resultVariableName", serviceTask.getResultVariableName());
    }

    boolean executionListenersAvailable = false;
    if (serviceTask.getExecutionListeners() != null && serviceTask.getExecutionListeners().size() > 0) {
    	executionListenersAvailable = true;
    }
    
    boolean fieldExtensionsAvailable = false;
    if(serviceTask.getFieldExtensions() != null && serviceTask.getFieldExtensions().size() > 0) {
    	
    	for (FieldExtension fieldExtension : serviceTask.getFieldExtensions()) {
        if(fieldExtension.getFieldName() != null && fieldExtension.getFieldName().length() > 0 &&
                fieldExtension.getExpression() != null && fieldExtension.getExpression().length() > 0) {
        	
        	fieldExtensionsAvailable = true;
        	break;
        }
    	}
    }
    
    if(executionListenersAvailable == true || fieldExtensionsAvailable == true) {
    	xtw.writeStartElement("extensionElements");
    }
    
    ExecutionListenerExport.createExecutionListenerXML(serviceTask.getExecutionListeners(), false, xtw);
    
    FieldExtensionsExport.writeFieldExtensions(xtw, serviceTask.getFieldExtensions(), false);
    
    if(executionListenersAvailable == true || fieldExtensionsAvailable == true) {
    	xtw.writeEndElement();
    }

    if (serviceTask.getCustomProperties() != null && serviceTask.getCustomProperties().size() > 0) {
      boolean firstCustomProperty = true;
      for (CustomProperty customProperty : serviceTask.getCustomProperties()) {
        if (ExtensionConstants.PROPERTY_ID_CUSTOM_SERVICE_TASK.equals(customProperty.getName())) {
          continue;
        }
        if (customProperty.getSimpleValue() == null || customProperty.getSimpleValue().length() == 0) {
        	continue;
        }
        if (firstCustomProperty == true) {
          xtw.writeStartElement("extensionElements");
          firstCustomProperty = false;
        }
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeAttribute("name", customProperty.getName());
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "string", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeCharacters(customProperty.getSimpleValue());
        xtw.writeEndElement();
        xtw.writeEndElement();
      }
      if (firstCustomProperty == false) {
        xtw.writeEndElement();
      }
    }
    
    MultiInstanceExport.createMultiInstance(object, xtw);
    
    // end ServiceTask element
    xtw.writeEndElement();
    
    if(serviceTask.getBoundaryEvents().size() > 0) {
    	for(BoundaryEvent event : serviceTask.getBoundaryEvents()) {
    		BoundaryEventExport.createBoundaryEvent(event, xtw);
    	}
    }
  }
}
