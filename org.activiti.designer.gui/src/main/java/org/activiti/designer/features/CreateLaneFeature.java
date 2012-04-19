package org.activiti.designer.features;

import java.util.List;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateLaneFeature extends AbstractCreateBPMNFeature {

	public static final String FEATURE_ID_KEY = "lane";

	public CreateLaneFeature(IFeatureProvider fp) {
		super(fp, "Lane", "Add lane");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		if(context.getTargetContainer() instanceof Diagram) return false;
		
		Object parentBo = getFeatureProvider().getBusinessObjectForPictogramElement(context.getTargetContainer());
		if(parentBo instanceof Pool || parentBo instanceof Lane) {
		  return true;
		}
		
    return false;
	}

	@Override
	public Object[] create(ICreateContext context) {
	  
	  Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
	  Object parentBo = getFeatureProvider().getBusinessObjectForPictogramElement(context.getTargetContainer());
	  Pool parentPool = null;
    if (parentBo instanceof Pool) {
      parentPool = (Pool) parentBo;
    } else {
      Lane lane = (Lane) parentBo;
      for (Pool pool : model.getPools()) {
        if (pool.getProcessRef().equals(lane.getParentProcess().getId())) {
          parentPool = pool;
          break;
        }
      }
    }
    
    if (parentPool == null) return null;
	  
    ContainerShape poolShape = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(parentPool);
    Process poolProcess = model.getProcess(parentPool.getId());
    
    if(poolProcess == null) return null;
    
    List<Lane> lanes = poolProcess.getLanes();
    int x = 0;
    int y = 0;
    int width = 0;
    int height = 0;
    if(lanes.size() == 0) {
      x = 20;
      y = 0;
      width = poolShape.getGraphicsAlgorithm().getWidth() - 20;
      height = poolShape.getGraphicsAlgorithm().getHeight();
      
    } else {
      ContainerShape lastLaneShape = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(lanes.get(lanes.size() - 1));
      x = lastLaneShape.getGraphicsAlgorithm().getX();
      y = lastLaneShape.getGraphicsAlgorithm().getY() + lastLaneShape.getGraphicsAlgorithm().getHeight();
      width = lastLaneShape.getGraphicsAlgorithm().getWidth();
      height = lastLaneShape.getGraphicsAlgorithm().getHeight();
    }
    
    Lane newLane = new Lane();
    newLane.setId(getNextId(newLane));
    newLane.setName("New lane");
    newLane.setParentProcess(poolProcess);
    poolProcess.getLanes().add(newLane);
    
    ResizeShapeContext resizeContext = new ResizeShapeContext(poolShape);
    resizeContext.setSize(poolShape.getGraphicsAlgorithm().getWidth(), poolShape.getGraphicsAlgorithm().getHeight() + height);
    resizeContext.setLocation(poolShape.getGraphicsAlgorithm().getX(), poolShape.getGraphicsAlgorithm().getY());
    resizeContext.setDirection(ResizeShapeContext.DIRECTION_SOUTH);
    resizeContext.putProperty("org.activiti.designer.lane.create", true);
    getFeatureProvider().getResizeShapeFeature(resizeContext).execute(resizeContext);
    
    context.putProperty("org.activiti.designer.lane.x", x);
    context.putProperty("org.activiti.designer.lane.y", y);
    context.putProperty("org.activiti.designer.lane.width", width);
    context.putProperty("org.activiti.designer.lane.height", height);
    
    AddContext addContext = new AddContext();
    addContext.setNewObject(newLane);
    addContext.setLocation(x, y);
    addContext.setSize(width, height);
    addContext.setTargetContainer(poolShape);
    getFeatureProvider().addIfPossible(addContext);
    
		// return newly created business object(s)
		return new Object[] { newLane };
	}

	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_LANE;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}
}
