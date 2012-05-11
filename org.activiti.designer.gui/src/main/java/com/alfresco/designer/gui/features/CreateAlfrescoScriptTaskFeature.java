package com.alfresco.designer.gui.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoScriptTask;
import org.activiti.designer.features.AbstractCreateFastBPMNFeature;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateAlfrescoScriptTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "alfrescoScripttask";

  public CreateAlfrescoScriptTaskFeature(IFeatureProvider fp) {
    super(fp, "AlfrescoScriptTask", "Add Alfresco script task");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
  }

  @Override
  public Object[] create(ICreateContext context) {
    AlfrescoScriptTask newScriptTask = new AlfrescoScriptTask();

    newScriptTask.setId(getNextId(newScriptTask));
    newScriptTask.setName("Alfresco Script Task");

    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(newScriptTask);
    } else {
      ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getMainProcess().getFlowElements().add(newScriptTask);
    }

    addGraphicalContent(context, newScriptTask);

    // activate direct editing after object creation
    getFeatureProvider().getDirectEditingInfo().setActive(true);

    return new Object[] { newScriptTask };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_SCRIPTTASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
