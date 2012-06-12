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

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.BaseElement;
import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.FlowNode;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;


/**
 * @author Tijs Rademakers
 */
public class BpmnDIExport implements ActivitiNamespaceConstants {
  
  private static XMLStreamWriter xtw;
  private static IFeatureProvider featureProvider;

  public static void createDIXML(Bpmn2MemoryModel model, IFeatureProvider inputFeatureProvider, XMLStreamWriter inputXtw) throws Exception {
  	featureProvider = inputFeatureProvider;
    xtw = inputXtw;
    xtw.writeStartElement(BPMNDI_PREFIX, "BPMNDiagram", BPMNDI_NAMESPACE);
    xtw.writeAttribute("id", "BPMNDiagram_" + model.getMainProcess().getId());

    xtw.writeStartElement(BPMNDI_PREFIX, "BPMNPlane", BPMNDI_NAMESPACE);
    xtw.writeAttribute("bpmnElement", model.getMainProcess().getId());
    xtw.writeAttribute("id", "BPMNPlane_" + model.getMainProcess().getId());

    for (Pool pool : model.getPools()) {
      PictogramElement picElement = featureProvider.getPictogramElementForBusinessObject(pool);
      writeBpmnElement(pool, picElement);
      
      Process process = model.getProcess(pool.getId());
      if(process != null) {
        for (Lane lane : process.getLanes()) {
          PictogramElement laneElement = featureProvider.getPictogramElementForBusinessObject(lane);
          writeBpmnElement(lane, laneElement);
        }
      }
    }
    
    for (Process process : model.getProcesses()) {   
      loopThroughElements(process.getFlowElements());
    }
    
    xtw.writeEndElement();
    xtw.writeEndElement();
  }
  
  
  
  private static void loopThroughElements(List<FlowElement> elementList) throws Exception {
  	for (FlowElement element : elementList) {
      if (element instanceof FlowNode) {
      	PictogramElement picElement = featureProvider.getPictogramElementForBusinessObject(element);
        writeBpmnElement(element, picElement);
        if(element instanceof SubProcess) {
        	SubProcess subProcess = (SubProcess) element;
        	loopThroughElements(subProcess.getFlowElements());
        }
        if(element instanceof Activity) {
        	Activity activity = (Activity) element;
        	for (BoundaryEvent boundaryEvent : activity.getBoundaryEvents()) {
        	  PictogramElement boundaryElement = featureProvider.getPictogramElementForBusinessObject(boundaryEvent);
        		writeBpmnElement(boundaryEvent, boundaryElement);
          }
        }
      }
    }
  	
  	for (FlowElement element : elementList) {
      if (element instanceof SequenceFlow) {
        writeBpmnEdge((SequenceFlow) element);
      } 
    }
  }
  
  private static void writeBpmnElement(BaseElement baseElement, PictogramElement picElement) throws Exception {
  	if(picElement instanceof Shape) {
  		Shape shape = (Shape) picElement;
  		xtw.writeStartElement(BPMNDI_PREFIX, "BPMNShape", BPMNDI_NAMESPACE);
      xtw.writeAttribute("bpmnElement", baseElement.getId());
      xtw.writeAttribute("id", "BPMNShape_" + baseElement.getId());
      if(baseElement instanceof SubProcess) {
      	xtw.writeAttribute("isExpanded", "true");
      } else if(baseElement instanceof Pool || baseElement instanceof Lane) {
        xtw.writeAttribute("isHorizontal", "true");
      }
      
      ILocation shapeLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(shape);
      createBounds(shapeLocation.getX(), shapeLocation.getY(), shape.getGraphicsAlgorithm().getWidth(), 
              shape.getGraphicsAlgorithm().getHeight());
      
      xtw.writeEndElement();
  	}
  }
  
  private static void writeBpmnEdge(SequenceFlow sequenceFlow) throws Exception {
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
    
    EList<ConnectionDecorator> decoratorList = freeFormConnection.getConnectionDecorators();
    for (ConnectionDecorator decorator : decoratorList) {
      if (decorator.getGraphicsAlgorithm() instanceof org.eclipse.graphiti.mm.algorithms.MultiText) {
        org.eclipse.graphiti.mm.algorithms.MultiText text = (org.eclipse.graphiti.mm.algorithms.MultiText) decorator.getGraphicsAlgorithm();
        if(text.getHeight() > 0) {
          xtw.writeStartElement(BPMNDI_PREFIX, "BPMNLabel", BPMNDI_NAMESPACE);
          createBounds(text.getX(), text.getY(), text.getWidth(), text.getHeight());
          xtw.writeEndElement();
          break;
        }
      }
    }
    
    ILocation sourceLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(sourceElement);
    int sourceX = sourceLocation.getX();
    int sourceY = sourceLocation.getY();
    int sourceWidth = sourceElement.getGraphicsAlgorithm().getWidth();
    int sourceHeight = sourceElement.getGraphicsAlgorithm().getHeight();
    int sourceMiddleX = sourceX + (sourceWidth / 2);
    int sourceMiddleY = sourceY + (sourceHeight / 2);
    int sourceBottomY = sourceY + sourceHeight;
    
    ILocation targetLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(targetElement);
    int targetX = targetLocation.getX();
    int targetY = targetLocation.getY();
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
  
  private static void createBounds(int x, int y, int width, int height) throws Exception {
    xtw.writeStartElement(OMGDC_PREFIX, "Bounds", OMGDC_NAMESPACE);
    xtw.writeAttribute("height", "" + height);
    xtw.writeAttribute("width", "" + width);
    xtw.writeAttribute("x", "" + x);
    xtw.writeAttribute("y", "" + y);
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
