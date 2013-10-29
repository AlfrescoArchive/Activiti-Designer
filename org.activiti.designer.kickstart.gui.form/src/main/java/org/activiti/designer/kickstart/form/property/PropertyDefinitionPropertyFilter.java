package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyDefinitionPropertyFilter extends AbstractKickstartFormPropertyFilter {

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    Object bo = getBusinessObject(pictogramElement);
    return bo instanceof FormPropertyDefinition && !(bo instanceof ReferencePropertyDefinition);
  }
}
