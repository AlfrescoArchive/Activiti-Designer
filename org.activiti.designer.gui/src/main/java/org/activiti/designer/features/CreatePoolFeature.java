package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
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
		if(context.getTargetContainer() instanceof Diagram) return true;
		
    return false;
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
		
	  Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
    model.getPools().add(newPool);
    model.addProcess(newProcess);
    
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
		return ActivitiImageProvider.IMG_POOL;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
