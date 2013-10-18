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

package org.activiti.designer.kickstart.process;

/**
 * Images in the Kickstart Process plugin.
 * 
 * @author Frederik heremans
 */
public enum KickstartProcessPluginImage {

  FORM_ICON("form.icon", "form-16.png"), 
  PROCESS_ICON("process.icon", "process-16.png"),
  HUMAN_STEP_ICON("humanstep.icon", "human-step-16.png");

  private static final String KEY_PREFIX = Activator.PLUGIN_ID;
  private static final String DEFAULT_IMAGE_DIR = "icons/";

  private final String imageKey;
  private final String imagePath;

  private KickstartProcessPluginImage(final String imageKey, final String imagePath) {
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
