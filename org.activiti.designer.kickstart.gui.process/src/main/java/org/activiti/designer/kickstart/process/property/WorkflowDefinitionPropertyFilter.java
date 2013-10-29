package org.activiti.designer.kickstart.process.property;

import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Property-filter for the main process-properties.
 * 
 * @author Frederik Heremans
 */
public class WorkflowDefinitionPropertyFilter extends AbstractKickstartProcessPropertyFilter {

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    Object bo = getBusinessObject(pictogramElement);
    return bo instanceof WorkflowDefinition;
  }

}
