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

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.MultiInstanceLoopCharacteristics;
import org.apache.commons.lang.StringUtils;


/**
 * @author Tijs Rademakers
 */
public class MultiInstanceExport implements ActivitiNamespaceConstants {

  public static void createMultiInstance(Object object, XMLStreamWriter xtw) throws Exception {
    Activity activity = (Activity) object;
    if(activity.getLoopCharacteristics() == null) return;
    
    MultiInstanceLoopCharacteristics multiInstanceDef = (MultiInstanceLoopCharacteristics) activity.getLoopCharacteristics();
    
    if(StringUtils.isNotEmpty(multiInstanceDef.getInputDataItem()) ||
    		StringUtils.isNotEmpty(multiInstanceDef.getLoopCardinality()) ||
    		StringUtils.isNotEmpty(multiInstanceDef.getCompletionCondition())) {
    
    
	    // start multiInstance element
	    xtw.writeStartElement("multiInstanceLoopCharacteristics");
	    xtw.writeAttribute("isSequential", "" + multiInstanceDef.isSequential());
	    if(StringUtils.isNotEmpty(multiInstanceDef.getInputDataItem())) {
	      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "collection", multiInstanceDef.getInputDataItem());
	    }
	    if(StringUtils.isNotEmpty(multiInstanceDef.getElementVariable())) {
	    	xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "elementVariable", multiInstanceDef.getElementVariable());
	    }
	    
	    if (StringUtils.isNotEmpty(multiInstanceDef.getLoopCardinality())) {
	      xtw.writeStartElement("loopCardinality");
	      xtw.writeCharacters(multiInstanceDef.getLoopCardinality());
	      xtw.writeEndElement();
	    }
	    
	    if(StringUtils.isNotEmpty(multiInstanceDef.getCompletionCondition())) {
	      xtw.writeStartElement("completionCondition");
	      xtw.writeCharacters(multiInstanceDef.getCompletionCondition());
	      xtw.writeEndElement();
	    }
	
	    // end multiInstance element
	    xtw.writeEndElement();
    }
  }
}
