package org.activiti.designer;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

  // The plug-in ID
  public static final String PLUGIN_ID = "org.activiti.designer.gui"; //$NON-NLS-1$

  // The shared instance
  private static Activator plugin;

  /**
   * The constructor
   */
  public Activator() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
   * )
   */
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
   * )
   */
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static Activator getDefault() {
    return plugin;
  }

  @Override
  protected void initializeImageRegistry(ImageRegistry registry) {
    Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);

    for (final PluginImage pluginImage : PluginImage.values()) {
      final IPath path = new Path(pluginImage.getImagePath());
      final URL url = FileLocator.find(bundle, path, null);
      final ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
      registry.put(pluginImage.getImageKey(), descriptor);
    }
  }

  /**
   * Utility method to get an image from the plugin.
   */
  public static final Image getImage(final PluginImage pluginImage) {
    return plugin.getImageRegistry().get(pluginImage.getImageKey());
  }

  /**
   * Utility method to get an image descriptor from the plugin.
   */
  public static final ImageDescriptor getImageDescriptor(final PluginImage pluginImage) {
    return ImageDescriptor.createFromImage(plugin.getImageRegistry().get(pluginImage.getImageKey()));
  }

}
