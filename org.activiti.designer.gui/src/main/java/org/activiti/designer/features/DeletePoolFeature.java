package org.activiti.designer.features;

import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class DeletePoolFeature extends AbstractCustomFeature {

  public DeletePoolFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public String getName() {
    return "Delete pool"; //$NON-NLS-1$
  }

  @Override
  public String getDescription() {
    return "Delete pool"; //$NON-NLS-1$
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    if(context.getPictogramElements() == null) return false;
    for (PictogramElement pictogramElement : context.getPictogramElements()) {
      if(getBusinessObjectForPictogramElement(pictogramElement) == null) continue;
      Object boObject = getBusinessObjectForPictogramElement(pictogramElement);
      if(boObject instanceof Pool == false) {
        return false;
      }
    }
    return true;
  }

  public void execute(ICustomContext context) {
    if(context.getPictogramElements() == null) return;
    
    for (final PictogramElement pictogramElement : context.getPictogramElements()) {
      if(getBusinessObjectForPictogramElement(pictogramElement) == null) continue;
      final Object boObject = getBusinessObjectForPictogramElement(pictogramElement);
      if(boObject instanceof Pool == true) {
        final Pool pool = (Pool) boObject;
        Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
        Process process = model.getProcess(pool.getId());
        model.getProcesses().remove(process);
        model.getPools().remove(pool);
        IRemoveContext rc = new RemoveContext(pictogramElement);
        IFeatureProvider featureProvider = getFeatureProvider();
        IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
        if (removeFeature != null) {
          removeFeature.remove(rc);
        }
      }
    }
  }
}
