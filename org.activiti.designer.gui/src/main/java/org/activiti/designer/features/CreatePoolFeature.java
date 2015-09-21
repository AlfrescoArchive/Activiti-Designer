package org.activiti.designer.features;

import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.designer.PluginImage;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.editor.ActivitiDiagramEditor;
import org.activiti.designer.eclipse.editor.ActivitiDiagramEditorInput;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class CreatePoolFeature extends AbstractCreateBPMNFeature {

  public static final String FEATURE_ID_KEY = "pool";

  public CreatePoolFeature(IFeatureProvider fp) {
    super(fp, "Pool", "Add pool");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    if (context.getTargetContainer() instanceof Diagram && !isInSubprocessEditor()) {
      return true;
    } else{
      return false;
    }
  }

  private boolean isInSubprocessEditor() {
    if (PreferencesUtil.getBooleanPreference(Preferences.EDITOR_ENABLE_MULTI_DIAGRAM, ActivitiPlugin.getDefault())) {
      ActivitiDiagramEditor ade = (ActivitiDiagramEditor)getDiagramBehavior().getDiagramContainer();
      ActivitiDiagramEditorInput adei = (ActivitiDiagramEditorInput)ade.getDiagramEditorInput();
      return adei.getParentEditor() != null;
    } else {
      return false;
    }
  }

  @Override
  public Object[] create(ICreateContext context) {
    Pool newPool = new Pool();
    newPool.setId(getNextId(newPool));
    newPool.setName("Pool");

    Process newProcess = new Process();
    newProcess.setId("process_" + newPool.getId());
    newProcess.setName(newProcess.getId());

    newPool.setProcessRef(newProcess.getId());

    BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
    model.getBpmnModel().getPools().add(newPool);
    model.getBpmnModel().addProcess(newProcess);

    PictogramElement poolElement = addGraphicalRepresentation(context, newPool);

    Lane lane = new Lane();
    lane.setId(getNextId(lane, "lane"));
    lane.setParentProcess(newProcess);
    newProcess.getLanes().add(lane);

    AddContext laneContext = new AddContext(new AreaContext(), lane);
    IAddFeature addFeature = getFeatureProvider().getAddFeature(laneContext);
    laneContext.setNewObject(lane);
    laneContext.setSize(poolElement.getGraphicsAlgorithm().getWidth() - 20, poolElement.getGraphicsAlgorithm().getHeight());
    laneContext.setTargetContainer((ContainerShape) poolElement);
    laneContext.setLocation(20, 0);
    if (addFeature.canAdd(laneContext)) {
      PictogramElement laneContainer = addFeature.add(laneContext);
      getFeatureProvider().link(laneContainer, new Object[] { lane });
    }

    // return newly created business object(s)
    return new Object[] { newPool };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_POOL.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
