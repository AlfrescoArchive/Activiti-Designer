/**
 * 
 */
package org.activiti.designer.util.extension;

import java.io.InputStream;

import org.activiti.designer.integration.servicetask.CustomServiceTask;

/**
 * @author a139923
 * 
 */
public interface CustomServiceTaskContext extends Comparable<CustomServiceTaskContext> {

  String getExtensionName();

  CustomServiceTask getServiceTask();

  String getSmallImageKey();
  String getLargeImageKey();
  String getShapeImageKey();

  InputStream getSmallIconStream();
  InputStream getLargeIconStream();
  InputStream getShapeIconStream();

}
