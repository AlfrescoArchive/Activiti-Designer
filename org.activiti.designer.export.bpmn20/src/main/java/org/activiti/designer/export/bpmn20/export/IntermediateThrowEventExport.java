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

import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.bpmn2.model.EventDefinition;
import org.activiti.designer.bpmn2.model.SignalEventDefinition;
import org.activiti.designer.bpmn2.model.ThrowEvent;
import org.apache.commons.lang.StringUtils;


/**
 * @author Tijs Rademakers
 */
public class IntermediateThrowEventExport implements ActivitiNamespaceConstants {

  public static void createIntermediateEvent(Object object, XMLStreamWriter xtw) throws Exception {
  	ThrowEvent throwEvent = (ThrowEvent) object;
  	
  	// start IntermediateThrowEvent element
    xtw.writeStartElement("intermediateThrowEvent");
    xtw.writeAttribute("id", throwEvent.getId());
    if (throwEvent.getName() != null) {
      xtw.writeAttribute("name", throwEvent.getName());
    }
  	
    List<EventDefinition> eventDefinitionList = throwEvent.getEventDefinitions();
    if(eventDefinitionList.size() == 1) {
      if(eventDefinitionList.get(0) instanceof SignalEventDefinition) {
      	SignalEventDefinition signalDef = (SignalEventDefinition) eventDefinitionList.get(0);
        
        xtw.writeStartElement("signalEventDefinition");
        
        if(StringUtils.isNotEmpty(signalDef.getSignalRef())) {
          xtw.writeAttribute("signalRef", signalDef.getSignalRef());
        }
        
        xtw.writeEndElement();
      } 
    }
    
    // end IntermediateThrowEvent element
    xtw.writeEndElement();
  }
}
