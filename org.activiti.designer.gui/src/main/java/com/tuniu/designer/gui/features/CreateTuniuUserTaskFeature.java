package com.tuniu.designer.gui.features;

import java.util.List;

import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.PluginImage;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.features.AbstractCreateFastBPMNFeature;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import com.tuniu.nfbird.bpm.model.WorkformTask;

public class CreateTuniuUserTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "tuniuUsertask";

  public CreateTuniuUserTaskFeature(IFeatureProvider fp) {
    super(fp, "TuniuUserTask", "Add tuniu user task");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || 
            parentObject instanceof SubProcess || parentObject instanceof Lane);
  }

  @Override
  public Object[] create(ICreateContext context) {
    WorkformTask newUserTask = new WorkformTask();
    
    newUserTask.setId(getNextId(newUserTask));
    newUserTask.setName("Tuniu User Task");
    
    List<String> formTypes = PreferencesUtil.getStringArray(Preferences.TUNIU_FORMTYPES_USERTASK);
    if (formTypes.size() > 0) {
      newUserTask.setFormKey(formTypes.get(0));
    }

    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).addFlowElement(newUserTask);
      
    } else if (parentObject instanceof Lane) {
      final Lane lane = (Lane) parentObject;
      lane.getFlowReferences().add(newUserTask.getId());
      lane.getParentProcess().addFlowElement(newUserTask);
      
    } else {
      BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
      if (model.getBpmnModel().getMainProcess() == null) {
        model.addMainProcess();
      }
      model.getBpmnModel().getMainProcess().addFlowElement(newUserTask);
    }

    addGraphicalContent(context, newUserTask);

    // activate direct editing after object creation
    getFeatureProvider().getDirectEditingInfo().setActive(true);

    return new Object[] { newUserTask };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_TUNIU_LOGO.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
