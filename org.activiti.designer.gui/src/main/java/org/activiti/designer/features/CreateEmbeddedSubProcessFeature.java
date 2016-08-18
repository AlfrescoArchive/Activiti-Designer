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

import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateEmbeddedSubProcessFeature extends AbstractCreateBPMNFeature {

  public static final String FEATURE_ID_KEY = "subprocess";

  public CreateEmbeddedSubProcessFeature(IFeatureProvider fp) {
    super(fp, "SubProcess", "Add sub process");
  }

  @Override
  public Object[] create(ICreateContext context) {
    SubProcess newSubProcess = new SubProcess();
    addObjectToContainer(context, newSubProcess, "Sub Process");

    // return newly created business object(s)
    return new Object[] { newSubProcess };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_SUBPROCESS_COLLAPSED.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
