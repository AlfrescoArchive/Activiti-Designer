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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ILinkService;


/**
 * @author Tijs Rademakers
 */
public class BpmnDIExport implements ActivitiNamespaceConstants {
  
  private static Map<String, GraphicsAlgorithm> diFlowNodeMap = new HashMap<String, GraphicsAlgorithm>();
  private static Diagram diagram;
  private static XMLStreamWriter xtw;

  public static void createDIXML(Process process, Diagram inputDiagram, XMLStreamWriter inputXtw) throws Exception {
    diagram = inputDiagram;
    xtw = inputXtw;
    ILinkService linkService = Graphiti.getLinkService();
    EList<EObject> contents = diagram.eResource().getContents();
    xtw.writeStartElement(BPMNDI_PREFIX, "BPMNDiagram", BPMNDI_NAMESPACE);
    xtw.writeAttribute("id", "BPMNDiagram_" + process.getId());

    xtw.writeStartElement(BPMNDI_PREFIX, "BPMNPlane", BPMNDI_NAMESPACE);
    xtw.writeAttribute("bpmnElement", process.getId());
    xtw.writeAttribute("id", "BPMNPlane_" + process.getId());

    diFlowNodeMap = new HashMap<String, GraphicsAlgorithm>();

    for (EObject bpmnObject : contents) {

      if (bpmnObject instanceof FlowNode) {
        writeBpmnElement((FlowNode) bpmnObject, diagram, "");
        if(bpmnObject instanceof SubProcess) {
          for (FlowElement subFlowElement : ((SubProcess) bpmnObject).getFlowElements()) {
            if (subFlowElement instanceof FlowNode) {
              List<PictogramElement> pictoList = linkService.getPictogramElements(diagram, bpmnObject);
              if(pictoList != null && pictoList.size() > 0) {
                ContainerShape parent = (ContainerShape) pictoList.get(0);
                writeBpmnElement((FlowNode) subFlowElement, parent, ((SubProcess) bpmnObject).getId());
              }
            }
          }
          for (FlowElement subFlowElement : ((SubProcess) bpmnObject).getFlowElements()) {
            if (subFlowElement instanceof SequenceFlow) {
              List<PictogramElement> pictoList = linkService.getPictogramElements(diagram, bpmnObject);
              if(pictoList != null && pictoList.size() > 0) {
                ContainerShape parent = (ContainerShape) pictoList.get(0);
                writeBpmnEdge((SequenceFlow) subFlowElement, parent, ((SubProcess) bpmnObject).getId());
              }
            }
          }
        }
      }
    }

    for (EObject bpmnObject : contents) {
      if (bpmnObject instanceof SequenceFlow) {
        writeBpmnEdge((SequenceFlow) bpmnObject, diagram, "");
      } 
    }
    xtw.writeEndElement();
    xtw.writeEndElement();
  }
  
  private static void writeBpmnElement(FlowNode flowNode, ContainerShape parent, String subprocessId) throws Exception {
    ILinkService linkService = Graphiti.getLinkService();
    xtw.writeStartElement(BPMNDI_PREFIX, "BPMNShape", BPMNDI_NAMESPACE);
    xtw.writeAttribute("bpmnElement", subprocessId + flowNode.getId());
    xtw.writeAttribute("id", "BPMNShape_" + flowNode.getId());
    
    if(flowNode instanceof BoundaryEvent) {
      if(((BoundaryEvent) flowNode).getAttachedToRef() == null || ((BoundaryEvent) flowNode).getAttachedToRef().getId() == null) {
        xtw.writeEndElement();
        return;
      }
    }

    for (Shape shape : parent.getChildren()) {
      EObject shapeBO = linkService.getBusinessObjectForLinkedPictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
      if(flowNode instanceof BoundaryEvent && shapeBO instanceof BoundaryEvent &&
              ((BoundaryEvent) shapeBO).getId().equals(flowNode.getId())) {
        
        BoundaryEvent shapeBoundaryEvent = (BoundaryEvent) shapeBO;
        diFlowNodeMap.put(flowNode.getId(), shape.getGraphicsAlgorithm());
        java.awt.Point attachedPoint = findAttachedShape(shapeBoundaryEvent.getAttachedToRef().getId(), parent.getChildren());
        if(attachedPoint != null) {
          xtw.writeStartElement(OMGDC_PREFIX, "Bounds", OMGDC_NAMESPACE);
          xtw.writeAttribute("height", "" + shape.getGraphicsAlgorithm().getHeight());
          xtw.writeAttribute("width", "" + shape.getGraphicsAlgorithm().getWidth());
          if(subprocessId.length() > 0) {
            xtw.writeAttribute("x", "" + (shape.getGraphicsAlgorithm().getX() + attachedPoint.getX()));
            xtw.writeAttribute("y", "" + (shape.getGraphicsAlgorithm().getY() + attachedPoint.getY()));
          } else {
            xtw.writeAttribute("x", "" + shape.getGraphicsAlgorithm().getX());
            xtw.writeAttribute("y", "" + shape.getGraphicsAlgorithm().getY());
          }
          xtw.writeEndElement();
        }
        
      } else {
        
        if (shapeBO instanceof FlowNode) {
          FlowNode shapeFlowNode = (FlowNode) shapeBO;
          if (shapeFlowNode.getId().equals(flowNode.getId())) {
            diFlowNodeMap.put(flowNode.getId(), shape.getGraphicsAlgorithm());
            xtw.writeStartElement(OMGDC_PREFIX, "Bounds", OMGDC_NAMESPACE);
            xtw.writeAttribute("height", "" + shape.getGraphicsAlgorithm().getHeight());
            xtw.writeAttribute("width", "" + shape.getGraphicsAlgorithm().getWidth());
            if(subprocessId.length() > 0) {
              xtw.writeAttribute("x", "" + (shape.getGraphicsAlgorithm().getX() + shape.getContainer().getGraphicsAlgorithm().getX()));
              xtw.writeAttribute("y", "" + (shape.getGraphicsAlgorithm().getY() + shape.getContainer().getGraphicsAlgorithm().getY()));
            } else {
              xtw.writeAttribute("x", "" + shape.getGraphicsAlgorithm().getX());
              xtw.writeAttribute("y", "" + shape.getGraphicsAlgorithm().getY());
            }
            xtw.writeEndElement();
          }
        }
      }
    }
    xtw.writeEndElement();
  }
  
  private static java.awt.Point findAttachedShape(String shapeid, EList<Shape> shapeList) {
    ILinkService linkService = Graphiti.getLinkService();
    for (Shape shape : shapeList) {
      EObject shapeBO = linkService.getBusinessObjectForLinkedPictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
      if(shapeBO instanceof FlowNode) {
        FlowNode shapeFlowNode = (FlowNode) shapeBO;
        if (shapeFlowNode.getId().equals(shapeid)) {
          ContainerShape parentContainerShape = ((ContainerShape) shape).getContainer();
          if(parentContainerShape instanceof Diagram == false) {
            EObject parentShapeBO = linkService.getBusinessObjectForLinkedPictogramElement(
                    parentContainerShape.getGraphicsAlgorithm().getPictogramElement());
            if(parentShapeBO instanceof SubProcess) {
              return new java.awt.Point(parentContainerShape.getGraphicsAlgorithm().getX(), parentContainerShape.getGraphicsAlgorithm().getY());
            } else {
              return new java.awt.Point(shape.getGraphicsAlgorithm().getX(), shape.getGraphicsAlgorithm().getY());
            }
          } else {
            return new java.awt.Point(shape.getGraphicsAlgorithm().getX(), shape.getGraphicsAlgorithm().getY());
          }
        }
      }
    }
    return null;
  }
  
  private static void writeBpmnEdge(SequenceFlow sequenceFlow, ContainerShape parent, String subprocessId) throws Exception {
    if (diFlowNodeMap.containsKey(sequenceFlow.getSourceRef().getId()) && diFlowNodeMap.containsKey(sequenceFlow.getTargetRef().getId())) {
      
      ILinkService linkService = Graphiti.getLinkService();
      FreeFormConnection freeFormConnection = null;
      EList<Point> bendPointList = null;
      
      for(Connection connection : diagram.getConnections()) {
        EObject linkedSequenceFlowObj = linkService.getBusinessObjectForLinkedPictogramElement(
                connection.getGraphicsAlgorithm().getPictogramElement());
        if(linkedSequenceFlowObj instanceof SequenceFlow == false) continue;
        
        SequenceFlow linkedSequenceFlow = (SequenceFlow) linkedSequenceFlowObj;
        if(linkedSequenceFlow.getId().equals(sequenceFlow.getId()) == false) continue;
        
        freeFormConnection = (FreeFormConnection) connection;
        bendPointList = freeFormConnection.getBendpoints();
      }
      
      if(freeFormConnection == null) return;
      
      xtw.writeStartElement(BPMNDI_PREFIX, "BPMNEdge", BPMNDI_NAMESPACE);
      xtw.writeAttribute("bpmnElement", subprocessId + sequenceFlow.getId());
      xtw.writeAttribute("id", "BPMNEdge_" + sequenceFlow.getId());
      
      GraphicsAlgorithm sourceConnection = diFlowNodeMap.get(sequenceFlow.getSourceRef().getId());
      GraphicsAlgorithm targetConnection = diFlowNodeMap.get(sequenceFlow.getTargetRef().getId());
      
      int subProcessX = 0;
      int subProcessY = 0;
      if(subprocessId.length() > 0) {
        GraphicsAlgorithm subProcessGraphics = diFlowNodeMap.get(subprocessId);
        if(subProcessGraphics != null) {
          subProcessX = subProcessGraphics.getX();
          subProcessY = subProcessGraphics.getY();
        }
      }
      
      int sourceX = subProcessX + sourceConnection.getX();
      int sourceY = subProcessY + sourceConnection.getY();
      int sourceWidth = sourceConnection.getWidth();
      int sourceHeight = sourceConnection.getHeight();
      int sourceMiddleX = sourceX + (sourceWidth / 2);
      int sourceMiddleY = sourceY + (sourceHeight / 2);
      java.awt.Point sourcePoint = new java.awt.Point(sourceMiddleX, sourceMiddleY);
      
      int targetX = subProcessX + targetConnection.getX();
      int targetY = subProcessY + targetConnection.getY();
      int targetWidth = targetConnection.getWidth();
      int targetHeight = targetConnection.getHeight();
      int targetMiddleX = targetX + (targetWidth / 2);
      int targetMiddleY = targetY + (targetHeight / 2);
      java.awt.Point targetPoint = new java.awt.Point(targetMiddleX, targetMiddleY);
      
      java.awt.Point lastWayPoint = null;
      
      if(sequenceFlow.getSourceRef() instanceof Gateway && getAmountOfOutgoingConnections(
              sequenceFlow.getSourceRef().getId()) > 1) {
        
        int y = 0;
        if(sourceConnection.getY() > targetConnection.getY()) {
          y = sourceConnection.getY();
        } else {
          y = sourceConnection.getY() + sourceConnection.getHeight();
        }
        lastWayPoint = createWayPoint(sourceConnection.getX() + (sourceConnection.getWidth() / 2) + subProcessX, y + subProcessY, xtw);
      
      } else if (sequenceFlow.getSourceRef() instanceof BoundaryEvent) {
        
        lastWayPoint = createWayPoint(sourceConnection.getX() + (sourceConnection.getWidth() / 2) + subProcessX,
                sourceConnection.getY() + sourceConnection.getHeight() + subProcessY, xtw);
        
      } else {
        lastWayPoint = createWayPoint(sourceConnection.getX() + sourceConnection.getWidth() + subProcessX,
                sourceConnection.getY() + (sourceConnection.getHeight() / 2) + subProcessY, xtw);
      }
      
      if(bendPointList != null && bendPointList.size() > 0) {
        for (Point point : bendPointList) {
          lastWayPoint = createWayPoint(point.getX(), point.getY(), xtw);
        }
      }
      
      if(sequenceFlow.getTargetRef() instanceof Gateway && getAmountOfIncomingConnections(
              sequenceFlow.getTargetRef().getId()) > 1) {
        
        int y = 0;
        if(targetConnection.getY() > sourceConnection.getY()) {
          y = targetConnection.getY();
        } else {
          y = targetConnection.getY() + targetConnection.getHeight();
        }
        createWayPoint(targetConnection.getX() + (targetConnection.getWidth() / 2) + subProcessX, y + subProcessY, xtw);
        
      } else {
        if(lastWayPoint != null) {
          if(lastWayPoint.getX() == targetMiddleX) {
            
            if(lastWayPoint.getY() > targetMiddleY) {
            	
              createWayPoint(targetMiddleX, targetY + targetHeight, xtw);
              
            } else {
            	if(sequenceFlow.getTargetRef() instanceof Event) {
              	targetHeight = 0;
              }
              createWayPoint(targetMiddleX, targetY - targetHeight, xtw);
            }
            
          } else if(lastWayPoint.getY() == targetMiddleY) {
            
            if(lastWayPoint.getX() > targetMiddleX) {
              createWayPoint(targetX + targetWidth, targetMiddleY, xtw);
              
            } else {
              createWayPoint(targetX, targetMiddleY, xtw);
            }
            
          } else {
            createWayPoint(targetConnection.getX() + subProcessX,
                    targetConnection.getY() + (targetConnection.getHeight() / 2) + subProcessY, xtw);
          }
        } else {
          createWayPoint(targetConnection.getX() + subProcessX,
                  targetConnection.getY() + (targetConnection.getHeight() / 2) + subProcessY, xtw);
        }
      }
      xtw.writeEndElement();
    }
  }
  
  private static int getAmountOfIncomingConnections(String id) {
    int amount = 0;
    ILinkService linkService = Graphiti.getLinkService();
    for(Connection connection : diagram.getConnections()) {
      EObject linkedSequenceFlowObj = linkService.getBusinessObjectForLinkedPictogramElement(
              connection.getGraphicsAlgorithm().getPictogramElement());
      if(linkedSequenceFlowObj instanceof SequenceFlow == false) continue;
      
      SequenceFlow linkedSequenceFlow = (SequenceFlow) linkedSequenceFlowObj;
      if(linkedSequenceFlow.getTargetRef().getId().equals(id)) {
        amount++;
      }
    }
    return amount;
  }
  
  private static int getAmountOfOutgoingConnections(String id) {
    int amount = 0;
    ILinkService linkService = Graphiti.getLinkService();
    for(Connection connection : diagram.getConnections()) {
      EObject linkedSequenceFlowObj = linkService.getBusinessObjectForLinkedPictogramElement(
              connection.getGraphicsAlgorithm().getPictogramElement());
      if(linkedSequenceFlowObj instanceof SequenceFlow == false) continue;
      
      SequenceFlow linkedSequenceFlow = (SequenceFlow) linkedSequenceFlowObj;
      if(linkedSequenceFlow.getSourceRef().getId().equals(id)) {
        amount++;
      }
    }
    return amount;
  }
  
  private static java.awt.Point createWayPoint(int x, int y, XMLStreamWriter xtw) throws Exception {
    xtw.writeStartElement(OMGDI_PREFIX, "waypoint", OMGDI_NAMESPACE);
    xtw.writeAttribute("x", "" + x);
    xtw.writeAttribute("y", "" + y);
    xtw.writeEndElement();
    return new java.awt.Point(x, y);
  }
}
