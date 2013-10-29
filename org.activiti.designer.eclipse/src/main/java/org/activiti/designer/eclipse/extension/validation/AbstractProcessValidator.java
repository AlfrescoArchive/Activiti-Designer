/**
 * 
 */
package org.activiti.designer.eclipse.extension.validation;

import org.activiti.designer.eclipse.extension.AbstractDiagramWorker;

/**
 * Base class for {@link ProcessValidator} implementations.
 * 
 * @author Tiese Barrell
 * 
 */
public abstract class AbstractProcessValidator extends AbstractDiagramWorker implements ProcessValidator {

  @Override
  protected String getMarkerId() {
    return ProcessValidator.MARKER_ID;
  }

}
