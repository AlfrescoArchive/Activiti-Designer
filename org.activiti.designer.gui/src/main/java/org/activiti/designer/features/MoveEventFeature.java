/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.features;

import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;


public class MoveEventFeature extends DefaultMoveShapeFeature {

	public MoveEventFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
  public boolean canMoveShape(IMoveShapeContext context) {
    return true;
  }

	@Override
	protected void postMoveShape(IMoveShapeContext context) {
		final Shape shape = context.getShape();

		// get the event itself to determine its boundary events
		final Event event = (Event) getBusinessObjectForPictogramElement(shape);
		
		BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
		
		if (context.getSourceContainer() != context.getTargetContainer()) {
		  if (context.getSourceContainer() instanceof Diagram == false) {
		    Object containerBo = getFeatureProvider().getBusinessObjectForPictogramElement(context.getSourceContainer());
		    if (containerBo instanceof SubProcess) {
		      SubProcess subProcess = (SubProcess) containerBo;
		      subProcess.removeFlowElement(event.getId());
		      for (SequenceFlow flow : event.getOutgoingFlows()) {
		        subProcess.removeFlowElement(flow.getId());
		      }
		    } else if (containerBo instanceof Lane) {
		      Lane lane = (Lane) containerBo;
          lane.getFlowReferences().remove(event.getId());
          lane.getParentProcess().removeFlowElement(event.getId());
          for (SequenceFlow flow : event.getOutgoingFlows()) {
            lane.getParentProcess().removeFlowElement(flow.getId());
          }
        }
		  } else {
		    model.getBpmnModel().getMainProcess().removeFlowElement(event.getId());
		    for (SequenceFlow flow : event.getOutgoingFlows()) {
		      model.getBpmnModel().getMainProcess().removeFlowElement(flow.getId());
        }
		  }
		  
		  if (context.getTargetContainer() instanceof Diagram == false) {
        Object containerBo = getFeatureProvider().getBusinessObjectForPictogramElement(context.getTargetContainer());
        if (containerBo instanceof SubProcess) {
          SubProcess subProcess = (SubProcess) containerBo;
          subProcess.addFlowElement(event);
          for (SequenceFlow flow : event.getOutgoingFlows()) {
            subProcess.addFlowElement(flow);
          }
        } else if (containerBo instanceof Lane) {
          Lane lane = (Lane) containerBo;
          lane.getFlowReferences().add(event.getId());
          lane.getParentProcess().addFlowElement(event);
          for (SequenceFlow flow : event.getOutgoingFlows()) {
            lane.getParentProcess().addFlowElement(flow);
          }
        }
      } else {
        model.getBpmnModel().getMainProcess().addFlowElement(event);
        for (SequenceFlow flow : event.getOutgoingFlows()) {
          model.getBpmnModel().getMainProcess().addFlowElement(flow);
        }
      }
		}
	}
}