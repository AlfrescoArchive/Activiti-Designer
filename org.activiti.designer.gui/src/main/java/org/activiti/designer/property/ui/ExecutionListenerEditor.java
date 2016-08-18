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
package org.activiti.designer.property.ui;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.designer.property.ModelUpdater;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;


public class ExecutionListenerEditor extends AbstractListenerEditor {
	
	public ExecutionListenerEditor(String key, Composite parent, ModelUpdater modelUpdater) {
    super(key, parent, EXECUTION_LISTENER, modelUpdater);
  }

  @Override
  protected boolean isTableChangeEnabled() {
    return false;
  }

  @Override
  protected AbstractListenerDialog getDialog(Shell shell, TableItem[] items) {
    return new ExecutionListenerDialog(shell, items, isSequenceFlow);
  }

  @Override
  protected AbstractListenerDialog getDialog(Shell shell, TableItem[] items, ActivitiListener savedListener) {
    return new ExecutionListenerDialog(shell, items, isSequenceFlow, savedListener);
  }
}
