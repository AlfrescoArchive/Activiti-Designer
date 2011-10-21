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
