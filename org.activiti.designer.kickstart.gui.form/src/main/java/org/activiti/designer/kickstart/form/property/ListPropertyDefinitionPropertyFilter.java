package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.definition.form.ListPropertyDefinition;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class ListPropertyDefinitionPropertyFilter extends AbstractKickstartFormPropertyFilter {

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    Object bo = getBusinessObject(pictogramElement);
    return bo instanceof ListPropertyDefinition;
  }
}
