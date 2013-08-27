package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.FormComponentLayout;
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
    super.delete(context);
    if(context.getPictogramElement() instanceof ContainerShape) {
      ContainerShape layoutParent = getFormLayouter().getLayoutContainerShape((ContainerShape) context.getPictogramElement());
      FormComponentLayout layout = getFormLayouter().getLayoutForContainer(layoutParent);
      if(layout != null) {
        layout.relayout(layoutParent);
      }
    }
  }
  
  protected KickstartFormLayouter getFormLayouter() {
    return ((KickstartFormFeatureProvider)getFeatureProvider()).getFormLayouter(); 
  }
}
