package org.activiti.designer.kickstart.process.diagram.shape;

import org.activiti.workflow.simple.definition.StepDefinition;

/**
 * A shape-controller that wraps children if they are added to the container, if needed.
 * 
 * @author Frederik Heremans
 *
 */
public interface WrappingChildShapeController {

  boolean shouldWrapChild(StepDefinition definition);
  
  StepDefinition wrapChild(StepDefinition definition);

  boolean shouldDeleteWrapper(StepDefinition businessObjectForSource);
}
