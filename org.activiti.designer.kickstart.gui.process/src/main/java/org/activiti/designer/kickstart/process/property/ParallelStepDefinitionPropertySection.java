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

import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section containing all properties in the main {@link WorkflowDefinition}.
 * 
 * @author Frederik Hereman
 */
public class ParallelStepDefinitionPropertySection extends AbstractKickstartProcessPropertySection {

  protected Label infoControl;
  
  @Override
  protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    infoControl = createFullWidthLabel("A step that can contain multiple steps that are executed in parallel.");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    // No value stored in model
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    // Nothing to store
  }
}
