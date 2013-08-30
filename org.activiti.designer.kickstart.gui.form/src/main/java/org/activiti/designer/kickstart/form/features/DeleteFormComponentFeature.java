package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.diagram.KickstartFormLayouter;
import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DeleteFormComponentFeature extends DefaultDeleteFeature implements ICustomUndoableFeature {

  protected Object deletedObject;
  
  public DeleteFormComponentFeature(KickstartFormFeatureProvider fp) {
    super(fp);
  }
  
  @Override
  public void delete(IDeleteContext context) {
    ContainerShape parent = null;
    if(context.getPictogramElement() instanceof ContainerShape) {
      parent = ((ContainerShape)context.getPictogramElement()).getContainer();
      
      deletedObject = getBusinessObjectForPictogramElement(context.getPictogramElement());
      super.delete(context);
      
      // Call redo, which contains the model-update only
      redo(context);
      
      // When deleting, force a re-layout of the parent container after shape has been removed
      if(context.getPictogramElement() instanceof ContainerShape) {
        getFormLayouter().relayout(parent);
      }
    } else {
      super.delete(context);
    }
  }
  
  protected KickstartFormLayouter getFormLayouter() {
    return ((KickstartFormFeatureProvider)getFeatureProvider()).getFormLayouter(); 
  }

  @Override
  public void undo(IContext context) {
    KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(getDiagram())));
    if(model != null && model.isInitialized() && deletedObject != null) {
      // Just add the definition to the model at the end, relayouting of the container
      // will cause the right order to be restored as it was before the delete 
      if(deletedObject instanceof FormPropertyDefinition) {
        model.getFormDefinition().getFormProperties().add((FormPropertyDefinition) deletedObject);
      } else if(deletedObject instanceof FormPropertyGroup) {
        model.getFormDefinition().getFormGroups().add((FormPropertyGroup) deletedObject);
      }
    }
    
    if(((IDeleteContext)context).getPictogramElement() instanceof ContainerShape) {
      getFormLayouter().relayout((ContainerShape) ((IDeleteContext)context).getPictogramElement());
    }
  }

  @Override
  public boolean canRedo(IContext context) {
    return deletedObject != null;
  }

  @Override
  public void redo(IContext context) {
    KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(getDiagram())));
    if(model != null && model.isInitialized()) {
      if(deletedObject instanceof FormPropertyDefinition) {
        model.getFormDefinition().getFormProperties().remove(deletedObject);
      } else if(deletedObject instanceof FormPropertyGroup) {
        model.getFormDefinition().getFormGroups().remove(deletedObject);
      }
    }
  }
}
