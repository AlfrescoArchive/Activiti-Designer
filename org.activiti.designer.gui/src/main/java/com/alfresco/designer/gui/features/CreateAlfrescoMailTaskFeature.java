package com.alfresco.designer.gui.features;

import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.alfresco.AlfrescoScriptTask;
import org.activiti.designer.PluginImage;
import org.activiti.designer.features.AbstractCreateFastBPMNFeature;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateAlfrescoMailTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "alfrescoMailtask";

  public CreateAlfrescoMailTaskFeature(IFeatureProvider fp) {
    super(fp, "AlfrescoMailTask", "Add Alfresco mail task");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || 
            parentObject instanceof SubProcess || parentObject instanceof Lane);
  }

  @Override
  public Object[] create(ICreateContext context) {
    ServiceTask newMailTask = new ServiceTask();
    newMailTask.setImplementation(AlfrescoScriptTask.ALFRESCO_SCRIPT_DELEGATE);
    newMailTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
    newMailTask.setId(getNextId(newMailTask));
    newMailTask.setName("Alfresco Mail Task");
    FieldExtension fieldExtension = new FieldExtension();
    fieldExtension.setFieldName("script");
    fieldExtension.setStringValue("var mail = actions.create(\"mail\");\nmail.execute(bpm_package);");
    newMailTask.getFieldExtensions().add(fieldExtension);
    
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).addFlowElement(newMailTask);
      
    } else if (parentObject instanceof Lane) {
      final Lane lane = (Lane) parentObject;
      lane.getFlowReferences().add(newMailTask.getId());
      lane.getParentProcess().addFlowElement(newMailTask);
      
    } else {
      Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
      if (model.getBpmnModel().getMainProcess() == null) {
        model.addMainProcess();
      }
      model.getBpmnModel().getMainProcess().addFlowElement(newMailTask);
    }

    addGraphicalContent(context, newMailTask);

    // activate direct editing after object creation
    getFeatureProvider().getDirectEditingInfo().setActive(true);

    return new Object[] { newMailTask };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_MAILTASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
