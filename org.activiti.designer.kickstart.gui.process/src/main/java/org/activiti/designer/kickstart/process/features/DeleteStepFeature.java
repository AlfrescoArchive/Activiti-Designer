package org.activiti.designer.kickstart.process.features;

import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.designer.kickstart.process.layout.KickstartProcessLayouter;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.AbstractStepDefinitionContainer;
import org.activiti.workflow.simple.definition.ListStepDefinition;
import org.activiti.workflow.simple.definition.ParallelStepsDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DeleteStepFeature extends DefaultDeleteFeature implements ICustomUndoableFeature {

  protected StepDefinition deletedObject;
  protected StepDefinition definitionContainer;
  protected boolean forceDelete = false;
  
  public DeleteStepFeature(KickstartProcessFeatureProvider fp) {
    super(fp);
  }
  
  @Override
  public boolean canDelete(IDeleteContext context) {
    // Everything can be deleted
   return true;
  }
  
  
  @Override
  public void delete(IDeleteContext context) {
    ContainerShape parent = null;
    if(context.getPictogramElement() instanceof ContainerShape) {
      parent = ((ContainerShape) context.getPictogramElement()).getContainer();
      
      Object parentObject = getBusinessObjectForPictogramElement(parent);
      if(parentObject instanceof StepDefinition) {
        definitionContainer = (StepDefinition) parentObject ;
      }
      Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
      if(bo instanceof StepDefinition) {
        deletedObject = (StepDefinition) bo; 
        
        super.delete(context);
        
        // Call redo, which contains the model-update only
        redo(context);
        
        // When deleting, force a re-layout of the parent container after shape has been removed
        if(context.getPictogramElement() instanceof ContainerShape) {
          getFormLayouter().relayout(parent, (KickstartProcessFeatureProvider) getFeatureProvider());
        }
      }
    } else {
      super.delete(context);
    }
  }
  
  public void setForceDelete(boolean forceDelete) {
    this.forceDelete = forceDelete;
  }
  
  protected KickstartProcessLayouter getFormLayouter() {
    return ((KickstartProcessFeatureProvider)getFeatureProvider()).getProcessLayouter(); 
  }

  @SuppressWarnings("unchecked")
  @Override
  public void undo(IContext context) {
    KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram())));
    if(model != null && model.isInitialized() && deletedObject != null && definitionContainer != null) {
      if(definitionContainer instanceof ParallelStepsDefinition && deletedObject instanceof ListStepDefinition<?>) {
        ((ParallelStepsDefinition) definitionContainer).getStepList().add((ListStepDefinition<ParallelStepsDefinition>) deletedObject);
      } else if(definitionContainer instanceof AbstractStepDefinitionContainer<?>) {
        ((AbstractStepDefinitionContainer<ListStepDefinition<?>>) definitionContainer).addStep(deletedObject);
      }
    }
    
    if(((IDeleteContext)context).getPictogramElement() instanceof ContainerShape) {
      // Perform the re-layout as part of the transaction
      getFormLayouter().relayout((ContainerShape) ((IDeleteContext)context).getPictogramElement(), (KickstartProcessFeatureProvider) getFeatureProvider());
    }
  }

  @Override
  public boolean canRedo(IContext context) {
    return deletedObject != null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void redo(IContext context) {
    KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram())));
    if(model != null && model.isInitialized() && deletedObject != null && definitionContainer != null) {
      if(definitionContainer instanceof ParallelStepsDefinition && deletedObject instanceof ListStepDefinition<?>) {
        ((ParallelStepsDefinition) definitionContainer).getStepList().remove((ListStepDefinition<ParallelStepsDefinition>) deletedObject);
      } else if(definitionContainer instanceof AbstractStepDefinitionContainer<?>) {
        ((AbstractStepDefinitionContainer<ListStepDefinition<?>>) definitionContainer).getSteps().remove(deletedObject);
      }
    }
  }
  
  @Override
  protected boolean getUserDecision(IDeleteContext context) {
    if(forceDelete) {
      return true;
    }
    return super.getUserDecision(context);
  }
  
  @Override
  protected boolean getUserDecision() {
    if(forceDelete) {
      return true;
    }
    return super.getUserDecision();
  }
}
