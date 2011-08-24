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

import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.emf.ecore.EObject;


/**
 * @author Tijs Rademakers
 */
public class IntermediateCatchEventExport implements ActivitiNamespaceConstants {

  public static void createIntermediateEvent(EObject object, String subProcessId, XMLStreamWriter xtw) throws Exception {
  	IntermediateCatchEvent catchEvent = (IntermediateCatchEvent) object;
    List<EventDefinition> eventDefinitionList = catchEvent.getEventDefinitions();
    if(eventDefinitionList.size() == 1) {
      if(eventDefinitionList.get(0) instanceof TimerEventDefinition) {
        TimerEventDefinition timerDef = (TimerEventDefinition) eventDefinitionList.get(0);
        if(timerDef.getTimeDuration() != null && 
                ((((FormalExpression) timerDef.getTimeDuration()).getBody() != null && 
                        ((FormalExpression) timerDef.getTimeDuration()).getBody().length() > 0) ||
                        
                        (((FormalExpression) timerDef.getTimeDate()).getBody() != null && 
                                ((FormalExpression) timerDef.getTimeDate()).getBody().length() > 0) ||
                                
                                (((FormalExpression) timerDef.getTimeCycle()).getBody() != null && 
                                        ((FormalExpression) timerDef.getTimeCycle()).getBody().length() > 0))) {
          
          // start TimerBoundaryEvent element
          xtw.writeStartElement("intermediateCatchEvent");
          xtw.writeAttribute("id", subProcessId + catchEvent.getId());
          if (catchEvent.getName() != null) {
            xtw.writeAttribute("name", catchEvent.getName());
          }
          
          xtw.writeStartElement("timerEventDefinition");
          
          if(((FormalExpression) timerDef.getTimeDuration()).getBody() != null && 
                        ((FormalExpression) timerDef.getTimeDuration()).getBody().length() > 0) {
            
            xtw.writeStartElement("timeDuration");
            xtw.writeCharacters(((FormalExpression) timerDef.getTimeDuration()).getBody());
            xtw.writeEndElement();
          
          } else if(((FormalExpression) timerDef.getTimeDate()).getBody() != null && 
                        ((FormalExpression) timerDef.getTimeDate()).getBody().length() > 0) {
            
            xtw.writeStartElement("timeDate");
            xtw.writeCharacters(((FormalExpression) timerDef.getTimeDate()).getBody());
            xtw.writeEndElement();
          
          } else {
            
            xtw.writeStartElement("timeCycle");
            xtw.writeCharacters(((FormalExpression) timerDef.getTimeCycle()).getBody());
            xtw.writeEndElement();
          }
          
          xtw.writeEndElement();

          // end TimerIntermediateCatchEvent element
          xtw.writeEndElement();
        }
      }
    }
  }
}
