/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.designer.export.bpmn20.export;

import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.FlowNode;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;


/**
 * @author Tijs Rademakers
 */
public class BpmnDIExport implements ActivitiNamespaceConstants {
  
  private static XMLStreamWriter xtw;
  private static IFeatureProvider featureProvider;

  public static void createDIXML(Process process, IFeatureProvider inputFeatureProvider, XMLStreamWriter inputXtw) throws Exception {
  	featureProvider = inputFeatureProvider;
    xtw = inputXtw;
    xtw.writeStartElement(BPMNDI_PREFIX, "BPMNDiagram", BPMNDI_NAMESPACE);
    xtw.writeAttribute("id", "BPMNDiagram_" + process.getId());

    xtw.writeStartElement(BPMNDI_PREFIX, "BPMNPlane", BPMNDI_NAMESPACE);
    xtw.writeAttribute("bpmnElement", process.getId());
    xtw.writeAttribute("id", "BPMNPlane_" + process.getId());

    loopThroughElements(process.getFlowElements(), null, null);
    
    xtw.writeEndElement();
    xtw.writeEndElement();
  }
  
  private static void loopThroughElements(List<FlowElement> elementList, ContainerShape parent, SubProcess parentSubProcess) throws Exception {
  	for (FlowElement element : elementList) {

      if (element instanceof FlowNode) {
      	FlowNode node = (FlowNode) element;
        writeBpmnElement(node, null);
        if(element instanceof SubProcess) {
        	SubProcess subProcess = (SubProcess) node;
        	ContainerShape newParent = (ContainerShape) featureProvider.getPictogramElementForBusinessObject(node);
        	loopThroughElements(subProcess.getFlowElements(), newParent, subProcess);
        }
      }
    }
  	
  	for (FlowElement element : elementList) {
      if (element instanceof SequenceFlow) {
        writeBpmnEdge((SequenceFlow) element, parent, parentSubProcess);
      } 
    }
  }
  
  private static void writeBpmnElement(FlowNode flowNode, ContainerShape parent) throws Exception {
  	
  	PictogramElement picElement = featureProvider.getPictogramElementForBusinessObject(flowNode);
  	if(picElement instanceof Shape) {
  		Shape shape = (Shape) picElement;
  		xtw.writeStartElement(BPMNDI_PREFIX, "BPMNShape", BPMNDI_NAMESPACE);
      xtw.writeAttribute("bpmnElement", flowNode.getId());
      xtw.writeAttribute("id", "BPMNShape_" + flowNode.getId());
      xtw.writeStartElement(OMGDC_PREFIX, "Bounds", OMGDC_NAMESPACE);
      xtw.writeAttribute("height", "" + shape.getGraphicsAlgorithm().getHeight());
      xtw.writeAttribute("width", "" + shape.getGraphicsAlgorithm().getWidth());
      java.awt.Point location = getLocation(shape);
      xtw.writeAttribute("x", "" + location.x);
      xtw.writeAttribute("y", "" + location.y);
      xtw.writeEndElement();
      xtw.writeEndElement();
  	}
  }
  
  private static java.awt.Point getLocation(Shape shape) {
  	int x = shape.getGraphicsAlgorithm().getX();
  	int y = shape.getGraphicsAlgorithm().getY();
  	if(shape.getContainer() instanceof Diagram) {
  		return new java.awt.Point(x, y);
  	}
  	
  	java.awt.Point parentPoint = getLocation(shape.getContainer());
  	return new java.awt.Point(parentPoint.x + x, parentPoint.y + y);
  }
  
  private static void writeBpmnEdge(SequenceFlow sequenceFlow, ContainerShape parent, SubProcess subProcess) throws Exception {
  	Shape sourceElement = null;
  	Shape targetElement = null;
  	if(sequenceFlow.getSourceRef() != null && sequenceFlow.getSourceRef().getId() != null) {
  		sourceElement = (Shape) featureProvider.getPictogramElementForBusinessObject(sequenceFlow.getSourceRef());
  	}
  	if(sequenceFlow.getTargetRef() != null && sequenceFlow.getTargetRef().getId() != null) {
  		targetElement = (Shape) featureProvider.getPictogramElementForBusinessObject(sequenceFlow.getTargetRef());
  	}
  	
  	if(sourceElement == null || targetElement == null) {
  		return;
  	}
  	
  	FreeFormConnection freeFormConnection = (FreeFormConnection) featureProvider.getPictogramElementForBusinessObject(sequenceFlow);
    
    if(freeFormConnection == null) return;
    
    xtw.writeStartElement(BPMNDI_PREFIX, "BPMNEdge", BPMNDI_NAMESPACE);
    xtw.writeAttribute("bpmnElement", sequenceFlow.getId());
    xtw.writeAttribute("id", "BPMNEdge_" + sequenceFlow.getId());
    
    int subProcessX = 0;
    int subProcessY = 0;
    if(subProcess != null) {
      /*GraphicsAlgorithm subProcessGraphics = diFlowNodeMap.get(subProcess.getId());
      if(subProcessGraphics != null) {
        subProcessX = subProcessGraphics.getX();
        subProcessY = subProcessGraphics.getY();
      }*/
    }
    
    int sourceX = subProcessX + sourceElement.getGraphicsAlgorithm().getX();
    int sourceY = subProcessY + sourceElement.getGraphicsAlgorithm().getY();
    int sourceWidth = sourceElement.getGraphicsAlgorithm().getWidth();
    int sourceHeight = sourceElement.getGraphicsAlgorithm().getHeight();
    int sourceMiddleX = sourceX + (sourceWidth / 2);
    int sourceMiddleY = sourceY + (sourceHeight / 2);
    int sourceBottomY = sourceY + sourceHeight;
    
    int targetX = subProcessX + targetElement.getGraphicsAlgorithm().getX();
    int targetY = subProcessY + targetElement.getGraphicsAlgorithm().getY();
    int targetWidth = targetElement.getGraphicsAlgorithm().getWidth();
    int targetHeight = targetElement.getGraphicsAlgorithm().getHeight();
    int targetMiddleX = targetX + (targetWidth / 2);
    int targetMiddleY = targetY + (targetHeight / 2);
    int targetBottomY = targetY + targetHeight;
    
    java.awt.Point lastWayPoint = null;
    
    if (sequenceFlow.getSourceRef() instanceof BoundaryEvent) {
      
      lastWayPoint = createWayPoint(sourceMiddleX, sourceY + sourceHeight, xtw);
    
    } else {
      
    	if((freeFormConnection.getBendpoints() == null || freeFormConnection.getBendpoints().size() == 0)) {
    		
    		if((sourceBottomY + 11) < targetY) {
  				lastWayPoint = createWayPoint(sourceMiddleX, sourceY + sourceHeight, xtw);
  			
  			} else if((sourceY - 11) > (targetY + targetHeight)) {
  				lastWayPoint = createWayPoint(sourceMiddleX, sourceY, xtw);
  			
  			} else if(sourceX > targetX) {
  				lastWayPoint = createWayPoint(sourceX, sourceMiddleY, xtw);
  				
  			} else {
  				lastWayPoint = createWayPoint(sourceX + sourceWidth, sourceMiddleY, xtw);
  			}
    		
    	} else {
    			
  			Point bendPoint = freeFormConnection.getBendpoints().get(0);
  			if((sourceBottomY + 5) < bendPoint.getY()) {
  				lastWayPoint = createWayPoint(sourceMiddleX, sourceY + sourceHeight, xtw);
  			
  			} else if((sourceY - 5) > bendPoint.getY()) {
  				lastWayPoint = createWayPoint(sourceMiddleX, sourceY, xtw);
  			
  			} else if(sourceX > bendPoint.getX()) {
  				lastWayPoint = createWayPoint(sourceX, sourceMiddleY, xtw);
  				
  			} else {
  				lastWayPoint = createWayPoint(sourceX + sourceWidth, sourceMiddleY, xtw);
  			}
    	}
    } 
    
    if(freeFormConnection.getBendpoints() != null && freeFormConnection.getBendpoints().size() > 0) {
      for (Point point : freeFormConnection.getBendpoints()) {
        lastWayPoint = createWayPoint(point.getX(), point.getY(), xtw);
      }
    }
    
    int difference = 5;
  	
  	if((freeFormConnection.getBendpoints() == null || freeFormConnection.getBendpoints().size() == 0)) {
  		difference = 11;
  	}
  	
    if((targetBottomY + difference) < lastWayPoint.getY()) {
			lastWayPoint = createWayPoint(targetMiddleX, targetY + targetHeight, xtw);
		
		} else if((targetY - difference) > lastWayPoint.getY()) {
			lastWayPoint = createWayPoint(targetMiddleX, targetY, xtw);
		
		} else if(targetX > lastWayPoint.getX()) {
			lastWayPoint = createWayPoint(targetX, targetMiddleY, xtw);
			
		} else {
			lastWayPoint = createWayPoint(targetX + targetWidth, targetMiddleY, xtw);
		}
    
    xtw.writeEndElement();
  }
  
  private static java.awt.Point createWayPoint(int x, int y, XMLStreamWriter xtw) throws Exception {
    xtw.writeStartElement(OMGDI_PREFIX, "waypoint", OMGDI_NAMESPACE);
    xtw.writeAttribute("x", "" + x);
    xtw.writeAttribute("y", "" + y);
    xtw.writeEndElement();
    return new java.awt.Point(x, y);
  }
}
