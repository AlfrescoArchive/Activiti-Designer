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
package org.activiti.designer.command;

import org.activiti.bpmn.model.Process;
import org.eclipse.graphiti.features.IFeatureProvider;

public class ProcessModelUpdater extends BpmnProcessModelUpdater {

  public ProcessModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof Process) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected Process cloneBusinessObject(Object businessObject) {
    return ((Process) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((Process) targetObject).setValues(((Process) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new ProcessModelUpdater(featureProvider);
  }
}
