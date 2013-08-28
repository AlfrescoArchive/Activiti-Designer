package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.diagram.KickstartFormLayouter;
import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DeleteFormComponentFeature extends DefaultDeleteFeature {

  public DeleteFormComponentFeature(KickstartFormFeatureProvider fp) {
    super(fp);
  }
  
  @Override
  public void delete(IDeleteContext context) {
    ContainerShape parent = null;
    if(context.getPictogramElement() instanceof ContainerShape) {
      parent = ((ContainerShape)context.getPictogramElement()).getContainer();
      
      FormPropertyDefinition definition = (FormPropertyDefinition) getBusinessObjectForPictogramElement(context.getPictogramElement());
      super.delete(context);
      
      // Delete the form-property from the model
      KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(getDiagram())));
      model.getFormDefinition().getFormProperties().remove(definition);
      
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
}
