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

import org.activiti.bpmn.model.StartEvent;
import org.eclipse.graphiti.features.IFeatureProvider;

public class StartEventModelUpdater extends BpmnProcessModelUpdater {

  public StartEventModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof StartEvent) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected StartEvent cloneBusinessObject(Object businessObject) {
    return ((StartEvent) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((StartEvent) targetObject).setValues(((StartEvent) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new StartEventModelUpdater(featureProvider);
  }
}
