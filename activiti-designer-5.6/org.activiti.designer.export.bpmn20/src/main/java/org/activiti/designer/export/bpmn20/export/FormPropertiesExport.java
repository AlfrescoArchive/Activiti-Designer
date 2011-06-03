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

import org.eclipse.bpmn2.FormProperty;

/**
 * @author Tijs Rademakers
 */
public class FormPropertiesExport implements ActivitiNamespaceConstants {

  public static void createFormPropertiesXML(List<FormProperty> propertyList, XMLStreamWriter xtw) throws Exception {

    for (FormProperty formProperty : propertyList) {
      xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "formProperty", ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeAttribute("id", formProperty.getId());
      if(formProperty.getName() != null && formProperty.getName().length() > 0) {
        xtw.writeAttribute("name", formProperty.getName());
      }
      if(formProperty.getType() != null && formProperty.getType().length() > 0) {
        xtw.writeAttribute("type", formProperty.getType());
      }
      if(formProperty.getValue() != null && formProperty.getValue().length() > 0) {
        if(formProperty.getValue().contains("#{")) {
          xtw.writeAttribute("expression", formProperty.getValue());
        } else {
          xtw.writeAttribute("variable", formProperty.getValue());
        }
      }
      xtw.writeAttribute("required", "" + formProperty.isRequired());
      xtw.writeAttribute("readable", "" + formProperty.isReadable());
      xtw.writeAttribute("writable", "" + formProperty.isWriteable());
      xtw.writeEndElement();
    }
  }

}
