/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.eclipse.extension.icon;

import org.eclipse.swt.graphics.Image;

/**
 * Provides the capability of resolving icons.
 * 
 * @author Tiese Barrell
 * 
 */
public interface IconProvider {

  /**
   * Gets the priority of this provider. A higher number means a higher
   * priority.
   * 
   * @return the priority
   */
  Integer getPriority();

  /**
   * Returns an Image for the provided context. If there is no icon registered
   * to this provider, throws a RuntimeException.
   * 
   * @param context
   *          the context to determine the icon for
   * @return an Image
   */
  Image getIcon(Object context);

}
