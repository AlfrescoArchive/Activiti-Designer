package org.activiti.designer.kickstart.process.features;

import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.StepDefinitionContainer;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;

/**
 * Base class for creating new step definitions. Subclasses should override {@link #createStepDefinition(ICreateContext)},
 * returning a {@link StepDefinition}. Adding the graphical representation and adding the property
 * to the model is handled by this class. 
 *  
 * @author Tijs Rademakers
 */
public abstract class AbstractCreateStepDefinitionFeature extends AbstractCreateFeature implements ICustomUndoableFeature {

  protected StepDefinition createdDefinition;
  protected StepDefinitionContainer<?> definitionContainer;
  
  public AbstractCreateStepDefinitionFeature(IFeatureProvider fp, String name, String description) {
    super(fp, name, description);
  }
  
  public final Object[] create(ICreateContext context) {
    // Let subclass create a new property
    createdDefinition = createStepDefinition(context);
    
    // Add the new property-definition to the model
    KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram())));
    if (model != null && model.isInitialized()) {
      Object businessObject = getFeatureProvider().getBusinessObjectForPictogramElement(context.getTargetContainer());
      if (businessObject instanceof StepDefinitionContainer<?>) {
        definitionContainer = (StepDefinitionContainer<?>) businessObject; 
        definitionContainer.addStep(createdDefinition);
      }
    }
    
    // Add graphical information for the definition
    addGraphicalRepresentation(context, createdDefinition);
    return new Object[] { createdDefinition };
  }
  
  @Override
  public boolean canUndo(IContext context) {
    return createdDefinition != null && definitionContainer != null;
  }
  
  @Override
  public boolean canRedo(IContext context) {
    return createdDefinition != null && definitionContainer != null;
  }
  
  @Override
  public void undo(IContext context) {
    KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram())));
    if (model != null && model.isInitialized() && createdDefinition != null) {
      definitionContainer.getSteps().remove(createdDefinition);
    }
  }
  
  @Override
  public void redo(IContext context) {
    KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram())));
    if (model != null && model.isInitialized() && createdDefinition != null) {
      definitionContainer.addStep(createdDefinition);
    }
  }
  
  protected abstract StepDefinition createStepDefinition(ICreateContext context);
}
