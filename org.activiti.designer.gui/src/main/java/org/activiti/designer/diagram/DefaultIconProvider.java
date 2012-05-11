/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.diagram;

import org.activiti.designer.Activator;
import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.BusinessRuleTask;
import org.activiti.designer.bpmn2.model.CallActivity;
import org.activiti.designer.bpmn2.model.EndEvent;
import org.activiti.designer.bpmn2.model.EventGateway;
import org.activiti.designer.bpmn2.model.EventSubProcess;
import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.activiti.designer.bpmn2.model.InclusiveGateway;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.MailTask;
import org.activiti.designer.bpmn2.model.ManualTask;
import org.activiti.designer.bpmn2.model.ParallelGateway;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.ReceiveTask;
import org.activiti.designer.bpmn2.model.ScriptTask;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.eclipse.extension.icon.IconProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Tiese Barrell
 * 
 */
public class DefaultIconProvider implements IconProvider {

  private static final int PRIORITY = 100;

  /**
   * 
   */
  public DefaultIconProvider() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.activiti.designer.eclipse.extension.icon.IconProvider#getPriority()
   */
  @Override
  public Integer getPriority() {
    return PRIORITY;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.activiti.designer.eclipse.extension.icon.IconProvider#getIcon(java.
   * lang.Object)
   */
  @Override
  public Image getIcon(final Object context) {
    Image result = null;

    if (context instanceof Process) {
      result = Activator.getImage(PluginImage.IMG_SUBPROCESS_EXPANDED);
    } else if (context instanceof EventSubProcess) {
      result = Activator.getImage(PluginImage.IMG_EVENT_SUBPROCESS);
    } else if (context instanceof SubProcess) {
      result = Activator.getImage(PluginImage.IMG_SUBPROCESS_COLLAPSED);
    } else if (context instanceof Pool) {
      result = Activator.getImage(PluginImage.IMG_POOL);
    } else if (context instanceof ParallelGateway) {
      result = Activator.getImage(PluginImage.IMG_GATEWAY_PARALLEL);
    } else if (context instanceof ExclusiveGateway) {
      result = Activator.getImage(PluginImage.IMG_GATEWAY_EXCLUSIVE);
    } else if (context instanceof InclusiveGateway) {
      result = Activator.getImage(PluginImage.IMG_GATEWAY_INCLUSIVE);
    } else if (context instanceof EventGateway) {
      result = Activator.getImage(PluginImage.IMG_GATEWAY_EVENT);
    } else if (context instanceof Lane) {
      result = Activator.getImage(PluginImage.IMG_LANE);
    } else if (context instanceof ManualTask) {
      result = Activator.getImage(PluginImage.IMG_MANUALTASK);
    } else if (context instanceof UserTask) {
      result = Activator.getImage(PluginImage.IMG_USERTASK);
    } else if (context instanceof ServiceTask) {
      result = Activator.getImage(PluginImage.IMG_SERVICETASK);
    } else if (context instanceof ScriptTask) {
      result = Activator.getImage(PluginImage.IMG_SCRIPTTASK);
    } else if (context instanceof MailTask) {
      result = Activator.getImage(PluginImage.IMG_MAILTASK);
    } else if (context instanceof ReceiveTask) {
      result = Activator.getImage(PluginImage.IMG_RECEIVETASK);
    } else if (context instanceof BusinessRuleTask) {
      result = Activator.getImage(PluginImage.IMG_BUSINESSRULETASK);
    } else if (context instanceof CallActivity) {
      result = Activator.getImage(PluginImage.IMG_CALLACTIVITY);
    } else if (context instanceof StartEvent) {
      result = Activator.getImage(PluginImage.IMG_STARTEVENT_NONE);
    } else if (context instanceof EndEvent) {
      result = Activator.getImage(PluginImage.IMG_ENDEVENT_NONE);
    } else {
      throw new IllegalArgumentException("This provider has no Icon for the provided context");
    }

    return result;
  }
}
