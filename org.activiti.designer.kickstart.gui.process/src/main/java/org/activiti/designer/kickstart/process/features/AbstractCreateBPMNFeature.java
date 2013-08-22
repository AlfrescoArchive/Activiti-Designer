 /**
 * 
 */
package org.activiti.designer.kickstart.process.features;

import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.workflow.simple.definition.AbstractNamedStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

/**
 * @author Tijs Rademakers
 */
public abstract class AbstractCreateBPMNFeature extends AbstractCreateFeature {

  public AbstractCreateBPMNFeature(IFeatureProvider fp, String name, String description) {
    super(fp, name, description);
  }

  protected abstract String getFeatureIdKey();

  protected String getNextId(StepDefinition element) {
    return ActivitiUiUtil.getNextStepId(element.getClass(), getFeatureIdKey(), getDiagram());
  }
  
  protected String getNextId(StepDefinition element, String featureIdKey) {
    return ActivitiUiUtil.getNextStepId(element.getClass(), featureIdKey, getDiagram());
  }
  
  public boolean canCreate(ICreateContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (parentObject instanceof WorkflowDefinition);
  }
  
  /**
   * Adds the given base element to the context. At first, a new ID is generated for the new object.
   * Depending on the type of element, it is added as artifact or flow element.
   * 
   * @param context the context to add it
   * @param baseElement the base element to add
   */
  protected void addObjectToContainer(ICreateContext context, StepDefinition step) {
    step.setId(getNextId(step));
    final ContainerShape targetContainer = context.getTargetContainer();
    addBaseElementToContainer(targetContainer, step);
    addGraphicalRepresentation(context, step);
  }
  
  protected void addBaseElementToContainer(ContainerShape targetContainer, StepDefinition step) {
    // find the parent object
    final Object parent = getBusinessObjectForPictogramElement(targetContainer);
    
    if (parent instanceof WorkflowDefinition) {
      ((WorkflowDefinition) parent).addStep(step);
    }
  }
  
  protected void addObjectToContainer(ICreateContext context, StepDefinition step, String name) {
    setName(name, step, context);
    addObjectToContainer(context, step);
  }
  
  protected void setName(String defaultName, StepDefinition targetElement, ICreateContext context) {
  	if(context.getProperty("org.activiti.designer.changetype.name") != null) {
  		((AbstractNamedStepDefinition) targetElement).setName(context.getProperty("org.activiti.designer.changetype.name").toString());
  	} else {
  	  ((AbstractNamedStepDefinition) targetElement).setName(defaultName);
  	}
  }
}
