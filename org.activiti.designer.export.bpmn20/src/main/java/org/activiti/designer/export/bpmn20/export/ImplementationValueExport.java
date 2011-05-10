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

/**
 * @author Tijs Rademakers
 */
public class ImplementationValueExport implements ActivitiNamespaceConstants {

  public static void writeImplementationValue(XMLStreamWriter xtw, String implementationType, String implementation, boolean namespace) throws Exception {
    if (implementationType == null || implementationType.length() == 0 || CLASS_TYPE.equals(implementationType)) {
      writeImplementationValueAndType(xtw, "class", implementation, namespace);
    } else if (implementationType.equals(DELEGATE_EXPRESSION_TYPE)){
      writeImplementationValueAndType(xtw, "delegateExpression", implementation, namespace);
    } else {
      writeImplementationValueAndType(xtw, "expression", implementation, namespace);
    }
  }
  
  private static void writeImplementationValueAndType(XMLStreamWriter xtw, String implementationType, String implementation, boolean namespace) throws Exception {
    if (implementation != null && implementation.length() > 0) {
      if(namespace)
        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, implementationType, implementation);
      else
        xtw.writeAttribute(implementationType, implementation);
    }
  }
}
