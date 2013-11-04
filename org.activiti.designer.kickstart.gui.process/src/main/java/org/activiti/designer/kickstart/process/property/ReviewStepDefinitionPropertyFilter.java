package org.activiti.designer.kickstart.process.property;

import org.activiti.workflow.simple.alfresco.step.AlfrescoReviewStepDefinition;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * @author Frederik Heremans
 */
public class ReviewStepDefinitionPropertyFilter extends AbstractKickstartProcessPropertyFilter {

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    Object bo = getBusinessObject(pictogramElement);
    return bo instanceof AlfrescoReviewStepDefinition;
  }

}
