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

public class ExecutionListenerDialog extends AbstractListenerDialog {
	
	private boolean isSequenceFlow;

	public ExecutionListenerDialog(Shell parent, TableItem[] fieldList, boolean isSequenceFlow) {
    super(parent, fieldList);
    this.isSequenceFlow = isSequenceFlow;
  }

  public ExecutionListenerDialog(Shell parent, TableItem[] fieldList, boolean isSequenceFlow, ActivitiListener savedListener) {
    
    super(parent, fieldList, savedListener);
    this.isSequenceFlow = isSequenceFlow;
  }

  @Override
  protected String[] getEventList() {
    if(isSequenceFlow) {
      return new String[] {"take"};
    } else {
      return new String[] {"start", "end"};
    }
  }

  @Override
  protected String getDefaultEvent() {
    if(isSequenceFlow) {
      return "take";
    } else {
      return "start";
    }
  }

}
