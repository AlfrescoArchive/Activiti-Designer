package org.activiti.designer.kickstart.process.command;

import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class StepDefinitionModelUpdater extends KickstartProcessModelUpdater<StepDefinition> {

  public StepDefinitionModelUpdater(StepDefinition businessObject, PictogramElement pictogramElement,
      IFeatureProvider featureProvider) {
    super(businessObject, pictogramElement, featureProvider);
  }

  @Override
  protected StepDefinition cloneBusinessObject(StepDefinition businessObject) {
    return businessObject.clone();
  }

  @Override
  protected void performUpdates(StepDefinition valueObject, StepDefinition targetObject) {
    targetObject.setValues(valueObject);
  }


}
