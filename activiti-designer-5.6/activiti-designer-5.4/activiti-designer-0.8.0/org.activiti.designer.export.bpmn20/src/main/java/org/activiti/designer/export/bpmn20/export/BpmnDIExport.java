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
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
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
        writeBpmnElement((FlowNode) bpmnObject, diagram, false);
        if(bpmnObject instanceof SubProcess) {
          for (FlowElement subFlowElement : ((SubProcess) bpmnObject).getFlowElements()) {
            if (subFlowElement instanceof FlowNode) {
              List<PictogramElement> pictoList = linkService.getPictogramElements(diagram, bpmnObject);
              if(pictoList != null && pictoList.size() > 0) {
                ContainerShape parent = (ContainerShape) pictoList.get(0);
                writeBpmnElement((FlowNode) subFlowElement, parent, true);
              }
            }
          }
          for (FlowElement subFlowElement : ((SubProcess) bpmnObject).getFlowElements()) {
            if (subFlowElement instanceof SequenceFlow) {
              writeBpmnEdge((SequenceFlow) subFlowElement);
            }
          }
        }
      }
    }

    for (EObject bpmnObject : contents) {
      if (bpmnObject instanceof SequenceFlow) {
        writeBpmnEdge((SequenceFlow) bpmnObject);
      } 
    }
    xtw.writeEndElement();
    xtw.writeEndElement();
  }
  
  private static void writeBpmnElement(FlowNode flowNode, ContainerShape parent, boolean subprocess) throws Exception {
    ILinkService linkService = Graphiti.getLinkService();
    xtw.writeStartElement(BPMNDI_PREFIX, "BPMNShape", BPMNDI_NAMESPACE);
    xtw.writeAttribute("bpmnElement", flowNode.getId());
    xtw.writeAttribute("id", "BPMNShape_" + flowNode.getId());

    xtw.writeStartElement(OMGDC_PREFIX, "Bounds", OMGDC_NAMESPACE);
    for (Shape shape : parent.getChildren()) {
      EObject shapeBO = linkService.getBusinessObjectForLinkedPictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
      if(flowNode instanceof BoundaryEvent) {
        if (shapeBO instanceof Task || shapeBO instanceof SubProcess && shape instanceof ContainerShape) {
          for (Shape childShape : ((ContainerShape) shape).getChildren()) {
            EObject childShapeBO = linkService.getBusinessObjectForLinkedPictogramElement(childShape.getGraphicsAlgorithm().getPictogramElement());
            if(childShapeBO instanceof BoundaryEvent) {
              if (((BoundaryEvent) childShapeBO).getId().equals(flowNode.getId())) {
                diFlowNodeMap.put(flowNode.getId(), childShape.getGraphicsAlgorithm());
                xtw.writeAttribute("height", "" + childShape.getGraphicsAlgorithm().getHeight());
                xtw.writeAttribute("width", "" + childShape.getGraphicsAlgorithm().getWidth());
                xtw.writeAttribute("x", "" + (childShape.getGraphicsAlgorithm().getX() + shape.getGraphicsAlgorithm().getX()));
                xtw.writeAttribute("y", "" + (childShape.getGraphicsAlgorithm().getY() + shape.getGraphicsAlgorithm().getY()));
              }
            }
          }
        } else if(shapeBO instanceof BoundaryEvent) {
          if (((BoundaryEvent) shapeBO).getId().equals(flowNode.getId())) {
            diFlowNodeMap.put(flowNode.getId(), shape.getGraphicsAlgorithm());
            xtw.writeAttribute("height", "" + shape.getGraphicsAlgorithm().getHeight());
            xtw.writeAttribute("width", "" + shape.getGraphicsAlgorithm().getWidth());
            xtw.writeAttribute("x", "" + (shape.getGraphicsAlgorithm().getX() + parent.getGraphicsAlgorithm().getX()));
            xtw.writeAttribute("y", "" + (shape.getGraphicsAlgorithm().getY() + parent.getGraphicsAlgorithm().getY()));
          }
        }
        
      } else {
        
        if (shapeBO instanceof FlowNode) {
          FlowNode shapeFlowNode = (FlowNode) shapeBO;
          if (shapeFlowNode.getId().equals(flowNode.getId())) {
            diFlowNodeMap.put(flowNode.getId(), shape.getGraphicsAlgorithm());
            xtw.writeAttribute("height", "" + shape.getGraphicsAlgorithm().getHeight());
            xtw.writeAttribute("width", "" + shape.getGraphicsAlgorithm().getWidth());
            if(subprocess == true) {
              xtw.writeAttribute("x", "" + (shape.getGraphicsAlgorithm().getX() + shape.getContainer().getGraphicsAlgorithm().getX()));
              xtw.writeAttribute("y", "" + (shape.getGraphicsAlgorithm().getY() + shape.getContainer().getGraphicsAlgorithm().getY()));
            } else {
              xtw.writeAttribute("x", "" + shape.getGraphicsAlgorithm().getX());
              xtw.writeAttribute("y", "" + shape.getGraphicsAlgorithm().getY());
            }
          }
        }
      }
    }

    xtw.writeEndElement();
    xtw.writeEndElement();
  }
  
  private static void writeBpmnEdge(SequenceFlow sequenceFlow) throws Exception {
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
      xtw.writeAttribute("bpmnElement", sequenceFlow.getId());
      xtw.writeAttribute("id", "BPMNEdge_" + sequenceFlow.getId());
      
      GraphicsAlgorithm sourceConnection = diFlowNodeMap.get(sequenceFlow.getSourceRef().getId());
      GraphicsAlgorithm targetConnection = diFlowNodeMap.get(sequenceFlow.getTargetRef().getId());
      
      if(sequenceFlow.getSourceRef() instanceof Gateway && getAmountOfOutgoingConnections(
              sequenceFlow.getSourceRef().getId()) > 1) {
        
        int y = 0;
        if(sourceConnection.getY() > targetConnection.getY()) {
          y = sourceConnection.getY();
        } else {
          y = sourceConnection.getY() + sourceConnection.getHeight();
        }
        createWayPoint(sourceConnection.getX() + (sourceConnection.getWidth() / 2), y, xtw);
        
      } else {
        createWayPoint(sourceConnection.getX() + sourceConnection.getWidth(),
                sourceConnection.getY() + (sourceConnection.getHeight() / 2), xtw);
      }
      
      if(bendPointList != null && bendPointList.size() > 0) {
        for (Point point : bendPointList) {
          createWayPoint(point.getX(), point.getY(), xtw);
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
        createWayPoint(targetConnection.getX() + (targetConnection.getWidth() / 2), y, xtw);
        
      } else {
        createWayPoint(targetConnection.getX(),
                targetConnection.getY() + (targetConnection.getHeight() / 2), xtw);
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
  
  private static void createWayPoint(int x, int y, XMLStreamWriter xtw) throws Exception {
    xtw.writeStartElement(OMGDI_PREFIX, "waypoint", OMGDI_NAMESPACE);
    xtw.writeAttribute("x", "" + x);
    xtw.writeAttribute("y", "" + y);
    xtw.writeEndElement();
  }
}
