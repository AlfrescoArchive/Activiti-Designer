package com.alfresco.designer.gui.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoUserTask;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.features.AbstractCreateFastBPMNFeature;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateAlfrescoUserTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "alfrescoUsertask";

  public CreateAlfrescoUserTaskFeature(IFeatureProvider fp) {
    super(fp, "AlfrescoUserTask", "Add Alfresco user task");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
  }

  @Override
  public Object[] create(ICreateContext context) {
    AlfrescoUserTask newUserTask = new AlfrescoUserTask();

    newUserTask.setId(getNextId(newUserTask));
    newUserTask.setName("Alfresco User Task");
    
    String[] formTypes = PreferencesUtil.getStringArray(Preferences.ALFRESCO_FORMTYPES_USERTASK);
    if (formTypes != null && formTypes.length > 0) {
      newUserTask.setFormKey(formTypes[0]);
    }

    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(newUserTask);
    } else {
      ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getMainProcess().getFlowElements().add(newUserTask);
    }

    addGraphicalContent(context, newUserTask);

    // activate direct editing after object creation
    getFeatureProvider().getDirectEditingInfo().setActive(true);

    return new Object[] { newUserTask };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_USERTASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
