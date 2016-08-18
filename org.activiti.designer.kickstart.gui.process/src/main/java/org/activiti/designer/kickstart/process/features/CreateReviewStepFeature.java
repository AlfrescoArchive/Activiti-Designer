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
package org.activiti.designer.kickstart.process.features;

import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.workflow.simple.alfresco.step.AlfrescoReviewStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateReviewStepFeature extends AbstractCreateStepDefinitionFeature {

  public static final String FEATURE_ID_KEY = "review-step";

  public CreateReviewStepFeature(KickstartProcessFeatureProvider fp) {
    super(fp, "Review step", "Add a review step");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected StepDefinition createStepDefinition(ICreateContext context) {
    AlfrescoReviewStepDefinition definition = new AlfrescoReviewStepDefinition();
    definition.setName("Review step");
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartProcessPluginImage.REVIEW_STEP_ICON.getImageKey();
  }
}