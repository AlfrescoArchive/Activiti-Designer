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

import org.activiti.bpmn.model.BusinessRuleTask;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateBusinessRuleTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "businessruletask";

  public CreateBusinessRuleTaskFeature(IFeatureProvider fp) {
    super(fp, "BusinessRuleTask", "Add business rule task");
  }

  @Override
  public Object[] create(ICreateContext context) {
    BusinessRuleTask newBusinessRuleTask = new BusinessRuleTask();
    addObjectToContainer(context, newBusinessRuleTask, "Business rule task");

    return new Object[] { newBusinessRuleTask };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_BUSINESSRULETASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
