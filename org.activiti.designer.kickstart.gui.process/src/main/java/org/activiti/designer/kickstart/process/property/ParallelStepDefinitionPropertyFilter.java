package org.activiti.designer.kickstart.process.property;

import org.activiti.workflow.simple.definition.ParallelStepsDefinition;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * @author Frederik Heremans
 */
public class ParallelStepDefinitionPropertyFilter extends AbstractKickstartProcessPropertyFilter {

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    Object bo = getBusinessObject(pictogramElement);
    return bo instanceof ParallelStepsDefinition;
  }

}
