package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Base filer for {@link ReferencePropertyDefinition} that need to be accepted based on the
 * {@link ReferencePropertyDefinition#getType()}.
 *  
 * @author Frederik Heremans
 */
public abstract class ReferencePropertyDefinitionFilter extends PropertyDefinitionPropertyFilter {

  @Override
  protected final boolean accept(PictogramElement pictogramElement) {
    boolean accept = false;
    Object businessObject = getBusinessObject(pictogramElement);

    if (businessObject instanceof ReferencePropertyDefinition) {
      accept = getAcceptedType().equals(
          ((ReferencePropertyDefinition) businessObject).getType());
    }
    return accept;
  }
  
  /**
   * @return the type of reference the filter accepts
   */
  protected abstract String getAcceptedType();
}
