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
package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Base filer for {@link ReferencePropertyDefinition} that need to be accepted based on the
 * {@link ReferencePropertyDefinition#getType()}.
 *  
 * @author Frederik Heremans
 */
public abstract class ReferencePropertyDefinitionFilter extends PropertyDefinitionPropertyFilter {

  @Override
  protected final boolean accept(PictogramElement pictogramElement) {
    boolean accept = false;
    Object businessObject = getBusinessObject(pictogramElement);

    if (businessObject instanceof ReferencePropertyDefinition) {
      accept = getAcceptedType().equals(
          ((ReferencePropertyDefinition) businessObject).getType());
    }
    return accept;
  }
  
  /**
   * @return the type of reference the filter accepts
   */
  protected abstract String getAcceptedType();
}
