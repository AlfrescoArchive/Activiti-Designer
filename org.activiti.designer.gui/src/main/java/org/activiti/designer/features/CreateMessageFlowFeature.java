package org.activiti.designer.features;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.MessageFlow;
import org.activiti.bpmn.model.Process;
import org.activiti.designer.PluginImage;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;

public class CreateMessageFlowFeature extends AbstractCreateBPMNConnectionFeature {

  public static final String FEATURE_ID_KEY = "messageflow";

  public CreateMessageFlowFeature(IFeatureProvider fp) {
    // provide name and description for the UI, e.g. the palette
    super(fp, "MessageFlow", "Create MessageFlow"); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public boolean canCreate(ICreateConnectionContext context) {
    FlowNode source = getFlowNode(context.getSourceAnchor());
    FlowNode target = getFlowNode(context.getTargetAnchor());
    if (source != null && target != null && source != target) {
      
      BpmnModel bpmnModel = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel();
      Process sourceProcess = null;
      Process targetProcess = null;
      for (Process process : bpmnModel.getProcesses()) {
        if (process.getFlowElementRecursive(source.getId()) != null) {
          sourceProcess = process;
        }
        
        if (process.getFlowElementRecursive(target.getId()) != null) {
          targetProcess = process;
        }
      }
      
      if (sourceProcess != null && targetProcess != null && sourceProcess.equals(targetProcess)) {
        return false;
      }
      
      return true;
    }
    return false;
  }

  public boolean canStartConnection(ICreateConnectionContext context) {
    // return true if source anchor isn't undefined
    if (getFlowNode(context.getSourceAnchor()) != null) {
      return true;
    }
    return false;
  }

  public Connection create(ICreateConnectionContext context) {
    Connection newConnection = null;

    FlowNode source = getFlowNode(context.getSourceAnchor());
    FlowNode target = getFlowNode(context.getTargetAnchor());

    if (source != null && target != null) {
      // create new business object
      MessageFlow messageFlow = createMessageFlow(source, target, context);

      // add connection for business object
      AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
      addContext.setNewObject(messageFlow);
      newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
    }
    return newConnection;
  }

  /**
   * Returns the FlowNode belonging to the anchor, or null if not available.
   */
  private FlowNode getFlowNode(Anchor anchor) {
    if (anchor != null) {
      Object obj = getBusinessObjectForPictogramElement(anchor.getParent());
      if (obj instanceof FlowNode) {
        return (FlowNode) obj;
      }
    }
    return null;
  }

  /**
   * Creates a SequenceFlow between two BaseElements.
   */
  private MessageFlow createMessageFlow(FlowNode source, FlowNode target, ICreateConnectionContext context) {
    MessageFlow messageFlow = new MessageFlow();

    messageFlow.setId(getNextId());
    messageFlow.setSourceRef(source.getId());
    messageFlow.setTargetRef(target.getId());

    if (PreferencesUtil.getBooleanPreference(Preferences.EDITOR_ADD_LABELS_TO_NEW_SEQUENCEFLOWS, ActivitiPlugin.getDefault())) {
      messageFlow.setName(String.format("to %s", target.getName()));
    } else {
      messageFlow.setName("");
    }

    ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().addMessageFlow(messageFlow);

    return messageFlow;
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_EREFERENCE.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }

  @Override
  protected Class< ? extends BaseElement> getFeatureClass() {
    return new MessageFlow().getClass();
  }

}
