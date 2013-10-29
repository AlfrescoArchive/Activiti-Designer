package org.activiti.designer.util.editor;

import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.impl.IIndependenceSolver;

/**
 * @author Tijs Rademakers
 *
 */
public class BpmnIndependenceSolver implements IIndependenceSolver {
	
  private IDiagramTypeProvider diagramTypeProvider;
  
  public BpmnIndependenceSolver(IDiagramTypeProvider diagramTypeProvider) {
    this.diagramTypeProvider = diagramTypeProvider;
  }
    
  @Override
  public String getKeyForBusinessObject(Object bo) {
    return ensureBpmnMemoryModel().getKeyForBusinessObject(bo);
  }

  @Override
  public Object getBusinessObjectForKey(String key) {
    return ensureBpmnMemoryModel().getBusinessObjectForKey(key);
  }

  public Map<String, Object> getObjectMap() {
    return ensureBpmnMemoryModel().getObjectMap();
  }

  public void setObjectMap(Map<String, Object> objectMap) {
    ensureBpmnMemoryModel().setObjectMap(objectMap);
  }
  
  protected BpmnMemoryModel ensureBpmnMemoryModel() {
    if(diagramTypeProvider.getDiagram() == null) {
      throw new IllegalStateException("No diagram is currently active");
    }
    
    BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagramTypeProvider.getDiagram()));
    if(model == null) {
      throw new IllegalStateException("No diagram model is currently available for diagram: " + EcoreUtil.getURI(diagramTypeProvider.getDiagram()));
    }
    return model;
  }
}
