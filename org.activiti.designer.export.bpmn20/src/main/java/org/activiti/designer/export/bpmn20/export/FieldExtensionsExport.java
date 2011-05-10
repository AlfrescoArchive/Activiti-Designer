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

import org.eclipse.bpmn2.FieldExtension;


/**
 * @author Tijs Rademakers
 */
public class FieldExtensionsExport implements ActivitiNamespaceConstants {
  
  public static void writeFieldExtensions(XMLStreamWriter xtw, List<FieldExtension> fieldExtensionList, boolean writeExtensionsElement) throws Exception {
    if (fieldExtensionList != null && fieldExtensionList.size() > 0) {

      if (writeExtensionsElement)
        xtw.writeStartElement("extensionElements");

      for (FieldExtension fieldExtension : fieldExtensionList) {
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeAttribute("name", fieldExtension.getFieldname());
        System.out.println("name " + fieldExtension.getFieldname());
        if (fieldExtension.getExpression().contains("${")) {
          xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "expression", ACTIVITI_EXTENSIONS_NAMESPACE);
        } else {
          xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "string", ACTIVITI_EXTENSIONS_NAMESPACE);
        }
        xtw.writeCharacters(fieldExtension.getExpression());
        xtw.writeEndElement();
        xtw.writeEndElement();
      }

      if (writeExtensionsElement)
        xtw.writeEndElement();
    }
  }

}
