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
 * @version 1
 */
public enum PluginImage {

  ACTION_UP("action.up.png"), ACTION_DOWN("action.down.png");//$NON-NLS-1$ //$NON-NLS-2$

  private static final String KEY_PREFIX = Activator.PLUGIN_ID;
  private static final String DEFAULT_IMAGE_DIR = "icons/";

  private final String imageKey;
  private final String imagePath;

  private PluginImage(String imageName) {
    this.imageKey = KEY_PREFIX + DEFAULT_IMAGE_DIR + imageName;
    this.imagePath = DEFAULT_IMAGE_DIR + imageName;
  }

  public String getImageKey() {
    return imageKey;
  }

  public String getImagePath() {
    return imagePath;
  }

}
