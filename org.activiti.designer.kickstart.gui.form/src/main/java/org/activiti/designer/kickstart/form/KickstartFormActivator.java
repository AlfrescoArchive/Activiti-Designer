package org.activiti.designer.kickstart.form;

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
 * The activator class controls the plug-in life cycle for Kickstart Forms.
 */
public class KickstartFormActivator extends AbstractUIPlugin {

  /**
   * Plugin Id
   */
  public static final String PLUGIN_ID = "org.activiti.designer.kickstart.gui.form";

  private static KickstartFormActivator plugin;

  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  /**
   * @return the shared instance
   */
  public static KickstartFormActivator getDefault() {
    return plugin;
  }

  @Override
  protected void initializeImageRegistry(ImageRegistry registry) {
    Bundle bundle = Platform.getBundle(KickstartFormActivator.PLUGIN_ID);

    // Add descriptors for all images used 
    for (final KickstartFormPluginImage pluginImage : KickstartFormPluginImage.values()) {
      final IPath path = new Path(pluginImage.getImagePath());
      final URL url = FileLocator.find(bundle, path, null);
      final ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
      registry.put(pluginImage.getImageKey(), descriptor);
    }
  }

  /**
   * Utility method to get an image from the plugin.
   */
  public static final Image getImage(final KickstartFormPluginImage pluginImage) {
    return plugin.getImageRegistry().get(pluginImage.getImageKey());
  }

  /**
   * Utility method to get an image descriptor from the plugin.
   */
  public static final ImageDescriptor getImageDescriptor(final KickstartFormPluginImage pluginImage) {
    return ImageDescriptor.createFromImage(plugin.getImageRegistry().get(pluginImage.getImageKey()));
  }

}
