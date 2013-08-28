package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.diagram.KickstartFormLayouter;
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
      
      super.delete(context);
      
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
