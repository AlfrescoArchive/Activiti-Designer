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

import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.emf.ecore.EObject;


/**
 * @author Tijs Rademakers
 */
public class BusinessRuleTaskExport implements ActivitiNamespaceConstants {

  public static void createBusinessRuleTask(EObject object, XMLStreamWriter xtw) throws Exception {
    BusinessRuleTask businessRuleTask = (BusinessRuleTask) object;
    // start BusinessRuleTask element
    xtw.writeStartElement("businessRuleTask");
    xtw.writeAttribute("id", businessRuleTask.getId());
    if (businessRuleTask.getName() != null) {
      xtw.writeAttribute("name", businessRuleTask.getName());
    }
    DefaultFlowExport.createDefaultFlow(object, xtw);
    AsyncActivityExport.createDefaultFlow(object, xtw);
    if(businessRuleTask.getRuleNames().size() > 0) {
      StringBuilder ruleNameBuilder = new StringBuilder();
      for (String ruleName: businessRuleTask.getRuleNames()) {
        if(ruleNameBuilder.length() > 0) {
          ruleNameBuilder.append(",");
        }
        ruleNameBuilder.append(ruleName);
      }
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, 
              "rules", ruleNameBuilder.toString());
    }
    
    if(businessRuleTask.getInputVariables().size() > 0) {
      StringBuilder inputBuilder = new StringBuilder();
      for (String input: businessRuleTask.getInputVariables()) {
        if(inputBuilder.length() > 0) {
          inputBuilder.append(",");
        }
        inputBuilder.append(input);
      }
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, 
              "ruleVariablesInput", inputBuilder.toString());
    }
    
    if(businessRuleTask.getRuleNames().size() > 0) {
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, 
              "exclude", "" + businessRuleTask.isExclude());
    }
    
    if(businessRuleTask.getResultVariableName() != null && businessRuleTask.getResultVariableName().length() > 0) {
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, 
              "resultVariableName", businessRuleTask.getResultVariableName());
    }
    
    MultiInstanceExport.createMultiInstance(object, xtw);

    // end BusinessRuleTask element
    xtw.writeEndElement();
  }
}
