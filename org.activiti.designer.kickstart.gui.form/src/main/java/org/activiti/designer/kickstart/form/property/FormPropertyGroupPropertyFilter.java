package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class FormPropertyGroupPropertyFilter extends AbstractKickstartFormPropertyFilter {

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    Object bo = getBusinessObject(pictogramElement);
    return bo instanceof FormPropertyGroup;
  }
}
