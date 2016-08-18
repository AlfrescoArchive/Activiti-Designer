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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

public class TaskListenerDialog extends AbstractListenerDialog {

  public TaskListenerDialog(Shell parent, TableItem[] fieldList) {
    super(parent, fieldList);
  }

  public TaskListenerDialog(Shell parent, TableItem[] fieldList, ActivitiListener savedListener) {
    super(parent, fieldList, savedListener);
  }

  @Override
  protected String[] getEventList() {
    return new String[] {"create", "assignment", "complete", "all"};
  }

  @Override
  protected String getDefaultEvent() {
    return "create";
  }
  
}
