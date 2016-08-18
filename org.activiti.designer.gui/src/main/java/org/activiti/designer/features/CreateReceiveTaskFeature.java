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
package org.activiti.designer.features;

import org.activiti.bpmn.model.ReceiveTask;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateReceiveTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "receivetask";

  public CreateReceiveTaskFeature(IFeatureProvider fp) {
    super(fp, "ReceiveTask", "Add receive task");
  }

  @Override
  public Object[] create(ICreateContext context) {
    ReceiveTask newReceiveTask = new ReceiveTask();
    addObjectToContainer(context, newReceiveTask, "Receive Task");

    return new Object[] { newReceiveTask };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_RECEIVETASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
