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

import org.activiti.bpmn.model.SignalEventDefinition;
import org.activiti.bpmn.model.ThrowEvent;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateSignalThrowingEventFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "signalintermediatethrowevent";

  public CreateSignalThrowingEventFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "SignalThrowingEvent", "Add signal intermediate throwing event");
  }

  public Object[] create(ICreateContext context) {
    ThrowEvent throwEvent = new ThrowEvent();
    SignalEventDefinition eventDef = new SignalEventDefinition();
    throwEvent.getEventDefinitions().add(eventDef);
    addObjectToContainer(context, throwEvent, "SignalThrowEvent");

    // return newly created business object(s)
    return new Object[] { throwEvent };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_THROW_SIGNAL.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
