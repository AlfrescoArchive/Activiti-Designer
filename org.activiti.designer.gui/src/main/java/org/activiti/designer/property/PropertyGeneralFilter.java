package org.activiti.designer.property;

import org.activiti.bpmn.model.BaseElement;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyGeneralFilter extends ActivitiPropertyFilter {

  @Override
  protected boolean accept(PictogramElement pe) {
  	Object bo = getBusinessObject(pe);
  	if(bo != null && bo instanceof BaseElement) {
      return true;
  	}
    return false;
  }
}
