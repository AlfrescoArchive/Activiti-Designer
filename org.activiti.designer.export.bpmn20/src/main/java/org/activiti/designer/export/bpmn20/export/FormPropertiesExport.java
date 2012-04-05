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

import org.activiti.designer.bpmn2.model.FormProperty;
import org.activiti.designer.bpmn2.model.FormValue;
import org.apache.commons.lang.StringUtils;

/**
 * @author Tijs Rademakers
 */
public class FormPropertiesExport implements ActivitiNamespaceConstants {

  public static void createFormPropertiesXML(List<FormProperty> propertyList, XMLStreamWriter xtw) throws Exception {

    for (FormProperty formProperty : propertyList) {
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "formProperty", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeAttribute("id", formProperty.getId());
      if(StringUtils.isNotEmpty(formProperty.getName())) {
        xtw.writeAttribute("name", formProperty.getName());
      }
      if(StringUtils.isNotEmpty(formProperty.getType())) {
        xtw.writeAttribute("type", formProperty.getType());
      }
      if(StringUtils.isNotEmpty(formProperty.getValue())) {
      	xtw.writeAttribute("value", formProperty.getValue());
      }
      if(StringUtils.isNotEmpty(formProperty.getExpression())) {
      	xtw.writeAttribute("expression", formProperty.getExpression());
      }
      if(StringUtils.isNotEmpty(formProperty.getVariable())) {
      	xtw.writeAttribute("variable", formProperty.getVariable());
      }
      if(StringUtils.isNotEmpty(formProperty.getDefaultExpression())) {
        xtw.writeAttribute("default", formProperty.getDefaultExpression());
      }
      if(StringUtils.isNotEmpty(formProperty.getDatePattern())) {
      	xtw.writeAttribute("datePattern", formProperty.getDatePattern());
      }
      if(formProperty.getRequired() != null) {
      	xtw.writeAttribute("required", formProperty.getRequired().toString().toLowerCase());
      }
      if(formProperty.getReadable() != null) {
      	xtw.writeAttribute("readable", formProperty.getReadable().toString().toLowerCase());
      }
      if(formProperty.getWriteable() != null) {
      	xtw.writeAttribute("writable", formProperty.getWriteable().toString().toLowerCase());
      }
      
      if(formProperty.getFormValues().size() > 0) {
      	for (FormValue formValue : formProperty.getFormValues()) {
      		xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "value", ACTIVITI_EXTENSIONS_NAMESPACE);
        	xtw.writeAttribute("id", formValue.getId());
        	xtw.writeAttribute("name", formValue.getName());
        	xtw.writeEndElement();
        }
      }
      
      xtw.writeEndElement();
    }
  }

}
