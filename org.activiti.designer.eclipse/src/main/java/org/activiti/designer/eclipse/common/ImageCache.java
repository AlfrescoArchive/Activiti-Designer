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
