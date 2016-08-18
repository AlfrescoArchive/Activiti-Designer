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
/**
 * 
 */
package org.activiti.designer.eclipse.common;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * @author Tiese Barrell
 * @since 5.5
 * @version 1
 * 
 */
public class ImageCache extends AbstractImageCache {

  /**
   * Returns an {@link ImageDescriptor} for the image file at the given plug-in
   * relative path
   * 
   * @param image
   *          the {@link PluginImage}
   * @return the image descriptor
   */
  public static final ImageDescriptor getImageDescriptor(PluginImage image) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(ActivitiPlugin.PLUGIN_ID, image.getImagePath());
  }

  /**
   * Returns an {@link Image} for the image file at the given plug-in relative
   * path
   * 
   * @param image
   *          the {@link PluginImage}
   * @return the image
   */
  public final Image getImage(PluginImage image) {
    final ImageDescriptor descriptor = getImageDescriptor(image);
    return getImage(descriptor);
  }

}
