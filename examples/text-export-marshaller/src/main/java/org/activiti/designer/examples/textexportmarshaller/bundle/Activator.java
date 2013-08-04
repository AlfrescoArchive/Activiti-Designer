package org.activiti.designer.examples.textexportmarshaller.bundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

  public static final String PLUGIN_ID = "org.activiti.designer.examples.textexportmarshaller"; //$NON-NLS-1$

  private static BundleContext context;

  static BundleContext getContext() {
    return context;
  }

  public void start(BundleContext bundleContext) throws Exception {
    Activator.context = bundleContext;
  }

  public void stop(BundleContext bundleContext) throws Exception {
    Activator.context = null;
  }

}
