package com.alfresco.designer.gui.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoStartEvent;
import org.activiti.designer.features.AbstractCreateBPMNFeature;
import org.activiti.designer.util.editor.ModelHandler;
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
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
  }

  public Object[] create(ICreateContext context) {
    StartEvent startEvent = new AlfrescoStartEvent();

    startEvent.setId(getNextId(startEvent));
    startEvent.setName("Alfresco start");

    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(startEvent);
    } else {
      ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getMainProcess().getFlowElements().add(startEvent);
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
