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
package org.activiti.designer.eclipse.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * Provides access to the plugin's image resources.
 * 
 * @author Tiese Barrell
 * @since 5.5
 * @version 1
 * 
 */
public abstract class AbstractImageCache {

  private static final Map<ImageDescriptor, Image> imageMap = new HashMap<ImageDescriptor, Image>();

  protected static final Image getImage(ImageDescriptor imageDescriptor) {
    if (imageDescriptor == null)
      return null;
    Image image = (Image) imageMap.get(imageDescriptor);
    if (image == null) {
      image = imageDescriptor.createImage();
      imageMap.put(imageDescriptor, image);
    }
    return image;
  }

  public void dispose() {
    Iterator<Image> iter = imageMap.values().iterator();
    while (iter.hasNext())
      iter.next().dispose();
    imageMap.clear();
  }
}
