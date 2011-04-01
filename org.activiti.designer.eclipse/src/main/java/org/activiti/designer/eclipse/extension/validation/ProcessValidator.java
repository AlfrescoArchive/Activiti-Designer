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
