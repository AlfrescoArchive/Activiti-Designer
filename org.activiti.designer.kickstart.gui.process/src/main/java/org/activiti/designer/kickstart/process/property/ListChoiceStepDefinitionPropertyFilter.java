package org.activiti.designer.kickstart.process.property;

import org.activiti.workflow.simple.definition.ListConditionStepDefinition;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * @author Frederik Heremans
 */
public class ListChoiceStepDefinitionPropertyFilter extends AbstractKickstartProcessPropertyFilter {

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    Object bo = getBusinessObject(pictogramElement);
    return bo instanceof ListConditionStepDefinition<?>;
  }

}
