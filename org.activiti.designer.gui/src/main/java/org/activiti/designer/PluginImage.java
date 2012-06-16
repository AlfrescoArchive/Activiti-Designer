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

package org.activiti.designer;

/**
 * Images in the GUI plugin.
 * 
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 3
 */
public enum PluginImage {

  ACTION_UP("action.up", "action.up.png"), //$NON-NLS-1$ 
  ACTION_DOWN("action.down", "action.down.png"), //$NON-NLS-1$ 
  ACTION_GO("action.go", "bullet_go.png"), //$NON-NLS-1$

  // The image identifier for an EReference.
  IMG_EREFERENCE("ereference", "ereference.gif"), //$NON-NLS-1$ 

  // Event image icons
  IMG_STARTEVENT_NONE("startevent.none", "type.startevent.none.png"), //$NON-NLS-1$
  IMG_ENDEVENT_NONE("endevent.none", "type.endevent.none.png"), //$NON-NLS-1$
  IMG_ENDEVENT_ERROR("endevent.error", "error.png"), //$NON-NLS-1$

  // Gateway image icons
  IMG_GATEWAY_PARALLEL("gateway.parallel", "type.gateway.parallel.png"), //$NON-NLS-1$
  IMG_GATEWAY_EXCLUSIVE("gateway.exclusive", "type.gateway.exclusive.png"), //$NON-NLS-1$
  IMG_GATEWAY_INCLUSIVE("gateway.inclusive", "type.gateway.inclusive.png"), //$NON-NLS-1$
  IMG_GATEWAY_EVENT("gateway.event", "type.gateway.event.png"), //$NON-NLS-1$

  // Task image icons
  IMG_USERTASK("usertask", "type.user.png"), //$NON-NLS-1$
  IMG_SCRIPTTASK("scripttask", "type.script.png"), //$NON-NLS-1$
  IMG_SERVICETASK("servicetask", "type.service.png"), //$NON-NLS-1$
  IMG_MAILTASK("mail", "type.send.png"), //$NON-NLS-1$
  IMG_RECEIVETASK("receive", "type.receive.png"), //$NON-NLS-1$
  IMG_MANUALTASK("manual", "type.manual.png"), //$NON-NLS-1$
  IMG_BUSINESSRULETASK("businessrule", "type.business.rule.png"), //$NON-NLS-1$
  IMG_CALLACTIVITY("callactivity", "callactivity.png"), //$NON-NLS-1$

  // Container icons
  IMG_SUBPROCESS_COLLAPSED("subprocess.collapsed", "type.subprocess.collapsed.png"), //$NON-NLS-1$
  IMG_SUBPROCESS_EXPANDED("subprocess.expanded", "type.subprocess.expanded.png"), //$NON-NLS-1$
  IMG_EVENT_SUBPROCESS("event.subprocess", "type.event.subprocess.png"), //$NON-NLS-1$
  IMG_POOL("pool", "pool.png"), //$NON-NLS-1$
  IMG_LANE("lane", "lane.png"), //$NON-NLS-1$

  IMG_ACTION_ZOOM("action.magnifier", "action.magnifier.png"), //$NON-NLS-1$

  IMG_BOUNDARY_TIMER("boundary.timer", "timer.png"), //$NON-NLS-1$
  IMG_BOUNDARY_ERROR("boundary.error", "error.png"), //$NON-NLS-1$
  IMG_BOUNDARY_SIGNAL("boundary.signal", "signal.png"), //$NON-NLS-1$

  IMG_THROW_SIGNAL("throw.signal", "throw.signal.png"), //$NON-NLS-1$
  IMG_THROW_NONE("throw.none", "throw.none.png"), //$NON-NLS-1$
  
  IMG_TEXT_ANNOTATION("textannotation", "textannotation.png"),
  IMG_ASSOCIATION("association", "association.png"),

  IMG_ALFRESCO_LOGO("alfresco.logo", "alfresco.png"), //$NON-NLS-1$

  EDIT_ICON("edit.icon", "edit.png"), //$NON-NLS-1$
  NEW_ICON("new.icon", "new.png"), //$NON-NLS-1$

  ;

  private static final String KEY_PREFIX = Activator.PLUGIN_ID;
  private static final String DEFAULT_IMAGE_DIR = "icons/";

  private final String imageKey;
  private final String imagePath;

  private PluginImage(final String imageKey, final String imagePath) {
    this.imageKey = KEY_PREFIX + imageKey;
    this.imagePath = DEFAULT_IMAGE_DIR + imagePath;
  }

  public String getImageKey() {
    return imageKey;
  }

  public String getImagePath() {
    return imagePath;
  }

}
