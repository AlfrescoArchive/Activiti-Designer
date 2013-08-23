package org.activiti.designer.kickstart.form.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class FormComponentDeleteFeature extends DefaultDeleteFeature {

  public FormComponentDeleteFeature(IFeatureProvider fp) {
    super(fp);
  }
  
  @Override
  public void delete(IDeleteContext context) {
    super.delete(context);
    
    if(context.getPictogramElement() instanceof Diagram) {
      // TODO: make better
      
    }
  }

}
