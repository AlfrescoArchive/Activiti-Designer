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

package org.activiti.designer.kickstart.form;

/**
 * Enum containing all available for images in the Kickstart Form GUI plugin.
 * 
 * @author Frederik Heremans
 */
public enum KickstartFormPluginImage {

  PROCESS("process.icon", "process-16.png"),
  NEW_TEXT_INPUT("new.textinput.icon", "textinput-16.png"),
  NEW_NUMBER_INPUT("new.numberinput.icon", "number-16.png"),
  NEW_TEXT_AREA("new.textarea.icon", "textarea-16.png"),
  NEW_DATE_INPUT("new.dateinput.icon", "dateinput-16.png"),
  NEW_LIST_INPUT("new.listinput.icon", "listinput-16.png"),
  NEW_GROUP("new.group.icon", "group-16.png"),
  NEW_DUEDATE("new.duedate.icon", "duedate-16.png"),
  NEW_PRIORITY("new.priority.icon", "priority-16.png"),
  NEW_PACKAGE_ITEMS("new.packageitems.icon", "package-items-16.png"),
  NEW_WORKFLOW_DESCRIPTION("new.workflowdescription.icon", "workflow-description-16.png"),
  NEW_FIELD_REFERENCE("new.reference.icon", "reference-16.png"),
  NEW_CHECKBOX("new.checkbox.icon", "checkbox-16.png"),
  NEW_PEOPLE_SELECT("new.peopleselect.icon", "user-select-16.png"),
  NEW_GROUP_SELECT("new.groupselect.icon", "group-select-16.png")
  ;

  private static final String KEY_PREFIX = KickstartFormActivator.PLUGIN_ID;
  private static final String DEFAULT_IMAGE_DIR = "icons/";

  private final String imageKey;
  private final String imagePath;

  private KickstartFormPluginImage(final String imageKey, final String imagePath) {
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