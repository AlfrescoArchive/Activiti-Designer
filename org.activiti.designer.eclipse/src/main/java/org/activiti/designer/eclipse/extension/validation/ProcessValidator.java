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
package org.activiti.designer.eclipse.extension.validation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 * 
 */
public interface ProcessValidator {

  /**
   * The identifier for problems created by the Activiti Designer for
   * {@link ProcessValidator}s.
   */
  public static final String MARKER_ID = "org.activiti.designer.eclipse.activitiValidatorMarker";

  /**
   * Gets an identifier for the validator.
   * 
   * @return the validator's id
   */
  String getValidatorId();

  /**
   * Gets a descriptive name for the validator.
   * 
   * @return the validator's name
   */
  String getValidatorName();

  /**
   * Gets a descriptive name for the format the validator validates.
   * 
   * @return the format's name
   */
  String getFormatName();

  /**
   * Validates the contents of the diagram.
   * 
   * <p>
   * The {@link IProgressMonitor} provided should be used to indicate progress
   * made in the validator and will be reported to the user.
   * 
   * @param diagram
   *          the diagram to be validated
   * @param monitor
   *          the monitor used to indicate progress of this validator
   * @return true if validation was successful, false otherwise
   */
  boolean validateDiagram(Diagram diagram, IProgressMonitor monitor);

}
