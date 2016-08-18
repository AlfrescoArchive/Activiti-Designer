/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.kickstart.process.property;

import org.activiti.designer.kickstart.process.property.PropertyItemBrowser.PropertyItemFilter;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;

/**
 * Filter that only matches concrete properties (not referencing and not fixed).
 * 
 * @author Frederik Heremans
 */
public class ConcretePropertyItemFilter implements PropertyItemFilter {

  @Override
  public boolean propertySelectable(FormPropertyDefinition definition) {
    boolean matches = true;
    if(definition instanceof ReferencePropertyDefinition) {
      String type = ((ReferencePropertyDefinition) definition).getType();
      matches = !AlfrescoConversionConstants.FORM_REFERENCE_FIELD.equals(type) &&
          !AlfrescoConversionConstants.FORM_REFERENCE_DUEDATE.equals(type) &&
          !AlfrescoConversionConstants.FORM_REFERENCE_PRIORITY.equals(type) &&
          !AlfrescoConversionConstants.FORM_REFERENCE_PACKAGE_ITEMS.equals(type) &&
          !AlfrescoConversionConstants.FORM_REFERENCE_WORKFLOW_DESCRIPTION.equals(type);
    }
    return matches;
  }
}
