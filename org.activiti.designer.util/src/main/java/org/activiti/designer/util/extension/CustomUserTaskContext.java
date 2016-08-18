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
