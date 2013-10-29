package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * @author Frederik Heremans
 */
public class DatePropertyDefinitionPropertyFilter extends PropertyDefinitionPropertyFilter {

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    return getBusinessObject(pictogramElement) instanceof DatePropertyDefinition;
  }
}
