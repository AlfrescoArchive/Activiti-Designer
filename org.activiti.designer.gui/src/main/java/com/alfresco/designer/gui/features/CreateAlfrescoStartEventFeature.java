package com.alfresco.designer.gui.features;

import java.util.List;

import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.alfresco.AlfrescoStartEvent;
import org.activiti.designer.PluginImage;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.features.AbstractCreateBPMNFeature;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateAlfrescoStartEventFeature extends AbstractCreateBPMNFeature {

  public static final String FEATURE_ID_KEY = "alfrescoStartevent";

  public CreateAlfrescoStartEventFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "AlfrescoStartEvent", "Add Alfresco start event");
  }

  public boolean canCreate(ICreateContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || 
            parentObject instanceof SubProcess || parentObject instanceof Lane);
  }

  public Object[] create(ICreateContext context) {
    StartEvent startEvent = new AlfrescoStartEvent();

    startEvent.setId(getNextId(startEvent));
    startEvent.setName("Alfresco start");
    
    List<String> formTypes = PreferencesUtil.getStringArray(Preferences.ALFRESCO_FORMTYPES_STARTEVENT);
    if (formTypes.size() > 0) {
      startEvent.setFormKey(formTypes.get(0));
    }

    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).addFlowElement(startEvent);
      
    } else if (parentObject instanceof Lane) {
      final Lane lane = (Lane) parentObject;
      lane.getFlowReferences().add(startEvent.getId());
      lane.getParentProcess().addFlowElement(startEvent);
      
    } else {
      BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
      if (model.getBpmnModel().getMainProcess() == null) {
        model.addMainProcess();
      }
      model.getBpmnModel().getMainProcess().addFlowElement(startEvent);
    }

    addGraphicalRepresentation(context, startEvent);

    // return newly created business object(s)
    return new Object[] { startEvent };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_STARTEVENT_NONE.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
