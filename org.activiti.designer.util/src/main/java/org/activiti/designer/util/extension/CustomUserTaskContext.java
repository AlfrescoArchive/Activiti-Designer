/**
 * 
 */
package org.activiti.designer.util.extension;

import java.io.InputStream;

import org.activiti.designer.integration.usertask.CustomUserTask;

/**
 * @author Tijs Rademakers
 * 
 */
public interface CustomUserTaskContext extends Comparable<CustomUserTaskContext> {

  String getExtensionName();

  CustomUserTask getUserTask();

  String getSmallImageKey();
  String getLargeImageKey();
  String getShapeImageKey();

  InputStream getSmallIconStream();
  InputStream getLargeIconStream();
  InputStream getShapeIconStream();

}
