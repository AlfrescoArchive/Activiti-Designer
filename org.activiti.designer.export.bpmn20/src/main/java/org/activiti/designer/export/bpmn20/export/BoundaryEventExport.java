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

import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.ErrorEventDefinition;
import org.activiti.designer.bpmn2.model.EventDefinition;
import org.activiti.designer.bpmn2.model.SignalEventDefinition;
import org.activiti.designer.bpmn2.model.TimerEventDefinition;
import org.apache.commons.lang.StringUtils;


/**
 * @author Tijs Rademakers
 */
public class BoundaryEventExport implements ActivitiNamespaceConstants {

  public static void createBoundaryEvent(Object object, XMLStreamWriter xtw) throws Exception {
    BoundaryEvent boundaryEvent = (BoundaryEvent) object;
    List<EventDefinition> eventDefinitionList = boundaryEvent.getEventDefinitions();
    if(eventDefinitionList.size() == 1) {
    	
    	xtw.writeStartElement("boundaryEvent");
      xtw.writeAttribute("id", boundaryEvent.getId());
      if (boundaryEvent.getName() != null) {
        xtw.writeAttribute("name", boundaryEvent.getName());
      }
      if (boundaryEvent.isCancelActivity()) {
      	xtw.writeAttribute("cancelActivity", "true");
      } else {
      	xtw.writeAttribute("cancelActivity", "false");
      }
      
      if (boundaryEvent.getAttachedToRef() != null) {
        xtw.writeAttribute("attachedToRef", boundaryEvent.getAttachedToRef().getId());
      }
    	
      if(eventDefinitionList.get(0) instanceof TimerEventDefinition) {
        TimerEventDefinition timerDef = (TimerEventDefinition) eventDefinitionList.get(0);
          
        xtw.writeStartElement("timerEventDefinition");
        
        if(StringUtils.isNotEmpty(timerDef.getTimeDuration())) {
          
          xtw.writeStartElement("timeDuration");
          xtw.writeCharacters(timerDef.getTimeDuration());
          xtw.writeEndElement();
        
        } else if(StringUtils.isNotEmpty(timerDef.getTimeDate())) {
          
          xtw.writeStartElement("timeDate");
          xtw.writeCharacters(timerDef.getTimeDate());
          xtw.writeEndElement();
        
        } else if(StringUtils.isNotEmpty(timerDef.getTimeCycle())) {
          
          xtw.writeStartElement("timeCycle");
          xtw.writeCharacters(timerDef.getTimeCycle());
          xtw.writeEndElement();
        }
        
        xtw.writeEndElement();
        
      } else if(eventDefinitionList.get(0) instanceof ErrorEventDefinition) {
        ErrorEventDefinition errorDef = (ErrorEventDefinition) eventDefinitionList.get(0);
        
        xtw.writeStartElement("errorEventDefinition");
        
        if(StringUtils.isNotEmpty(errorDef.getErrorCode())) {
          xtw.writeAttribute("errorRef", errorDef.getErrorCode());
        }
        
        xtw.writeEndElement();
        
      } else if(eventDefinitionList.get(0) instanceof SignalEventDefinition) {
      	SignalEventDefinition signalDef = (SignalEventDefinition) eventDefinitionList.get(0);
        
        xtw.writeStartElement("signalEventDefinition");
        
        if(StringUtils.isNotEmpty(signalDef.getSignalRef())) {
          xtw.writeAttribute("signalRef", signalDef.getSignalRef());
        }
        
        xtw.writeEndElement();
      }
      
      xtw.writeEndElement();
    }
  }
}
