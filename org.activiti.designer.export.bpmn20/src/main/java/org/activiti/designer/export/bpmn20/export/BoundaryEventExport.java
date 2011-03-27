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

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.emf.ecore.EObject;


/**
 * @author Tijs Rademakers
 */
public class BoundaryEventExport implements ActivitiNamespaceConstants {

  public static void createBoundaryEvent(EObject object, String subProcessId, XMLStreamWriter xtw) throws Exception {
    BoundaryEvent boundaryEvent = (BoundaryEvent) object;
    List<EventDefinition> eventDefinitionList = boundaryEvent.getEventDefinitions();
    if(eventDefinitionList.size() == 1) {
      if(eventDefinitionList.get(0) instanceof TimerEventDefinition) {
        TimerEventDefinition timerDef = (TimerEventDefinition) eventDefinitionList.get(0);
        if(timerDef.getTimeDuration() != null && ((FormalExpression) timerDef.getTimeDuration()).getBody() != null &&
          ((FormalExpression) timerDef.getTimeDuration()).getBody().length() > 0) {
          
          // start TimerBoundaryEvent element
          xtw.writeStartElement("boundaryEvent");
          xtw.writeAttribute("id", subProcessId + boundaryEvent.getId());
          if (boundaryEvent.getName() != null) {
            xtw.writeAttribute("name", boundaryEvent.getName());
          }
          xtw.writeAttribute("cancelActivity", "true");
          if (boundaryEvent.getAttachedToRef() != null) {
            xtw.writeAttribute("attachedToRef", subProcessId + boundaryEvent.getAttachedToRef().getId());
          }
          
          xtw.writeStartElement("timerEventDefinition");
          
          xtw.writeStartElement("timeDuration");
          
          xtw.writeCharacters(((FormalExpression) timerDef.getTimeDuration()).getBody());
          
          xtw.writeEndElement();
          
          xtw.writeEndElement();

          // end TimerBoundaryEvent element
          xtw.writeEndElement();
        }
      } else if(eventDefinitionList.get(0) instanceof ErrorEventDefinition) {
        ErrorEventDefinition errorDef = (ErrorEventDefinition) eventDefinitionList.get(0);
        
        // start ErrorBoundaryEvent element
        xtw.writeStartElement("boundaryEvent");
        xtw.writeAttribute("id", subProcessId + boundaryEvent.getId());
        if (boundaryEvent.getName() != null) {
          xtw.writeAttribute("name", boundaryEvent.getName());
        }
        if (boundaryEvent.getAttachedToRef() != null) {
          xtw.writeAttribute("attachedToRef", subProcessId + boundaryEvent.getAttachedToRef().getId());
        }
        
        xtw.writeStartElement("errorEventDefinition");
        
        if(errorDef.getErrorCode() != null && errorDef.getErrorCode().length() > 0) {
          xtw.writeAttribute("errorRef", errorDef.getErrorCode());
        }
        
        xtw.writeEndElement();

        // end ErrorBoundaryEvent element
        xtw.writeEndElement();
      }
    }
  }
}
