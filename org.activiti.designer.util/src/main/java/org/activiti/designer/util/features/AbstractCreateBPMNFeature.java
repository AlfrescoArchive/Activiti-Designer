/**
 * 
 */
package org.activiti.designer.util.features;

import org.activiti.designer.bpmn2.model.FlowNode;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Tiese Barrell
 * @version 2
 * @since 0.5.0
 * 
 */
public abstract class AbstractCreateBPMNFeature extends AbstractCreateFeature {

  public AbstractCreateBPMNFeature(IFeatureProvider fp, String name, String description) {
    super(fp, name, description);
  }

  protected abstract String getFeatureIdKey();

  @SuppressWarnings("rawtypes")
  protected abstract Class getFeatureClass();

  protected String getNextId() {
    return ActivitiUiUtil.getNextId(getFeatureClass(), getFeatureIdKey(), getDiagram());
  }
  
  protected void addObjectToContainer(ICreateContext context, FlowNode flowNode, String name) {
  	flowNode.setId(getNextId());
  	flowNode.setName(name);
  	ContainerShape targetContainer = context.getTargetContainer();
		if(targetContainer instanceof Diagram) {
			ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).addFlowElement(flowNode);
		
		} else {
			Object parentObject = getBusinessObjectForPictogramElement(targetContainer);
			if(parentObject instanceof SubProcess) {
				((SubProcess) parentObject).getFlowElements().add(flowNode);
			}
		}
		addGraphicalRepresentation(context, flowNode);
  }

}
