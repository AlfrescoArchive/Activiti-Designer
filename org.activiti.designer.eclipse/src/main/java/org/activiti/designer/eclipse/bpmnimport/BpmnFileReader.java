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

package org.activiti.designer.eclipse.bpmnimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.designer.eclipse.bpmn.BpmnParser;
import org.activiti.designer.eclipse.bpmn.GraphicInfo;
import org.activiti.designer.eclipse.bpmn.SequenceFlowModel;
import org.apache.commons.lang.StringUtils;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CandidateGroup;
import org.eclipse.bpmn2.CandidateUser;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FieldExtension;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.MailTask;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ILinkService;


/**
 * @author Tijs Rademakers
 */
public class BpmnFileReader {
  
  private static final int START_X = 30;
  private static final int START_Y = 200;
  private static final int EVENT_WIDTH = 35;
  private static final int EVENT_HEIGHT = 35;
  private static final int TASK_WIDTH = 105;
  private static final int TASK_HEIGHT = 55;
  private static final int GATEWAY_WIDTH = 40;
  private static final int GATEWAY_HEIGHT = 40;
  private static final int SEQUENCEFLOW_WIDTH = 40;
  
  private Diagram diagram;
  private IFeatureProvider featureProvider;
  private String filename;
  private InputStream fileStream;
  private String processName;
  
  private BpmnParser bpmnParser = new BpmnParser();
  private Map<String, GraphicInfo> yMap = new HashMap<String, GraphicInfo>();
  private List<SubProcess> subProcessList = new ArrayList<SubProcess>();
  
  public BpmnFileReader(String filename, Diagram diagram, IFeatureProvider featureProvider) {
    this.filename = filename;
    this.diagram = diagram;
    this.featureProvider = featureProvider;
  }
  
  public BpmnFileReader(InputStream fileStream, String processName, Diagram diagram, IFeatureProvider featureProvider) {
    this.fileStream = fileStream;
    this.processName = processName;
    this.diagram = diagram;
    this.featureProvider = featureProvider;
  }
  
  public void openStream() {
    File bpmnFile = new File(filename);
    if(bpmnFile.exists() == false) {
      System.out.println("bpmn file does not exist " + filename);
      return;
    }
    processName = filename.substring(filename.lastIndexOf(File.separator) + 1);
    processName = processName.substring(0, processName.indexOf("."));
    try {
      fileStream = new FileInputStream(bpmnFile);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void readBpmn() {
    try {
      XMLInputFactory xif = XMLInputFactory.newInstance();
      InputStreamReader in = new InputStreamReader(fileStream, "UTF-8");
      XMLStreamReader xtr = xif.createXMLStreamReader(in);
      bpmnParser.parseBpmn(xtr);
      
      if(bpmnParser.bpmnList.size() == 0) return;
      
      org.eclipse.bpmn2.Process process = Bpmn2Factory.eINSTANCE.createProcess();
      process.setId(processName);
      if(bpmnParser.process != null && StringUtils.isNotEmpty(bpmnParser.process.getName())) {
      	process.setName(bpmnParser.process.getName());
      } else {
      	process.setName(processName);
      }
      if(bpmnParser.process != null && StringUtils.isNotEmpty(bpmnParser.process.getNamespace())) {
      	process.setNamespace(bpmnParser.process.getNamespace());
      }
      Documentation documentation = Bpmn2Factory.eINSTANCE.createDocumentation();
      documentation.setId("documentation_process");
      documentation.setText("");
      process.getDocumentation().add(documentation);
      if(bpmnParser.process != null && bpmnParser.process.getExecutionListeners().size() > 0) {
        process.getExecutionListeners().addAll(bpmnParser.process.getExecutionListeners());
      }
      diagram.eResource().getContents().add(process);
      
      /*if(bpmnParser.bpmdiInfoFound == true) {
        drawDiagramWithBPMNDI(diagram, featureProvider, bpmnParser.bpmnList, bpmnParser.sequenceFlowList,
                bpmnParser.locationMap);
      } else {*/
      
        List<FlowElement> wrongOrderList = createDiagramElements(bpmnParser.bpmnList);
        if(wrongOrderList.size() > 0) {
        	
        	int counter = 0;
          while(wrongOrderList.size() > 0 && counter < 10) {
            int sizeBefore = wrongOrderList.size();
            wrongOrderList = createDiagramElements(wrongOrderList);
           	
            if(sizeBefore <= wrongOrderList.size()) {
              counter++;
            } else {
            	counter = 0;
            }
          }
        }
        drawSequenceFlows();
      //}
      setFriendlyIds();
      xtr.close();
      in.close();
      fileStream.close();
      
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private List<FlowElement> createDiagramElements(List<FlowElement> flowList) {
    SubProcess activeSubProcess = null;
    List<FlowElement> wrongOrderList = new ArrayList<FlowElement>();
    Set<BoundaryEvent> boundaryEventList = new HashSet<BoundaryEvent>(); 
    
    // look for boundary events
    for (FlowElement flowElement : flowList) {
    	if(flowElement instanceof BoundaryEvent) {
    		boundaryEventList.add((BoundaryEvent) flowElement);
    	}
    }
    
    // first look for start event
    for (FlowElement startElement : flowList) {
    	
    	if(startElement instanceof StartEvent) {
    		if(activeSubProcess == null || containsFlowElementId(activeSubProcess.getFlowElements(), startElement.getId()) == false) {
    			GraphicInfo graphicInfo = getNextGraphicInfo(null, startElement, yMap, boundaryEventList);
          yMap.put(startElement.getId(), graphicInfo);
          addBpmnElementToDiagram(startElement, graphicInfo, diagram);
          
          processDiagramInSequence(startElement, flowList, activeSubProcess, wrongOrderList, boundaryEventList);
          
          break;
          
    		}
    	}
    }
    return wrongOrderList;
  }
  
  private void processDiagramInSequence(FlowElement previousElement, List<FlowElement> flowList, 
  		SubProcess activeSubProcess, List<FlowElement> wrongOrderList, Set<BoundaryEvent> boundaryEventList) {
  	
  	for(SequenceFlowModel sequence : bpmnParser.sequenceFlowList) {
    	if(sequence.sourceRef != null && sequence.sourceRef.equals(previousElement.getId()) &&
    			sequence.targetRef != null && yMap.containsKey(sequence.targetRef) == false) {
    
    		for (FlowElement flowElement : flowList) {
    			if(sequence.targetRef.equals(flowElement.getId())) {
    				
			    	if(multipleIncomingSequenceFlows(flowElement, bpmnParser.sequenceFlowList) > 1) {
			    		try {
			        	getMaxX(flowElement.getId());
			        	calculateDivergingElementY(flowElement, bpmnParser.sequenceFlowList);
			        	
			        } catch(FlowSourceNotFoundException e) {
			        	if(activeSubProcess == null || containsFlowElementId(activeSubProcess.getFlowElements(), flowElement.getId()) == false) {
			        		wrongOrderList.add(flowElement);
			          }
			          continue;
			        }
			    	} 
			      
			      GraphicInfo graphicInfo = getNextGraphicInfo(previousElement, flowElement, yMap, boundaryEventList);
			    
			      yMap.put(flowElement.getId(), graphicInfo);
			      
			      if(flowElement instanceof SubProcess) {
			        activeSubProcess = (SubProcess) flowElement;
			        subProcessList.add(activeSubProcess);
			        
			      } else if(activeSubProcess == null || containsFlowElementId(activeSubProcess.getFlowElements(), 
			      		flowElement.getId()) == false) {
			      	
			      	addBpmnElementToDiagram(flowElement, graphicInfo, diagram); 
			      }
			      
			      processDiagramInSequence(flowElement, flowList, activeSubProcess, wrongOrderList, boundaryEventList);
			      
			      if(boundaryEventList.size() > 0 && 
			      		(flowElement instanceof SubProcess || flowElement instanceof CallActivity 
			      				|| flowElement instanceof Task)) {
			      	
			      	if(flowElement instanceof SubProcess || activeSubProcess == null || 
			      			containsFlowElementId(activeSubProcess.getFlowElements(), flowElement.getId()) == false) {
			      	
				      	for (BoundaryEvent boundaryEvent : boundaryEventList) {
				      		if(boundaryEvent.getAttachedToRef() != null && boundaryEvent.getAttachedToRef().getId().equals(flowElement.getId())) {
				      			
				      			GraphicInfo boundaryGraphicInfo = getNextGraphicInfo(null, boundaryEvent, yMap, boundaryEventList);
							      yMap.put(boundaryEvent.getId(), boundaryGraphicInfo);
							      addBpmnElementToDiagram(boundaryEvent, boundaryGraphicInfo, diagram);
							      
							      processDiagramInSequence(boundaryEvent, flowList, activeSubProcess, wrongOrderList, boundaryEventList);
				      		}
	              }
			      	}
			      }
			      
			      break;
    			}
    		}
    	}
    }
  }
  
 	private boolean containsFlowElementId(List<FlowElement> flowElementList, String id) {
    for (FlowElement flowElement : flowElementList) {
      if(id.equals(flowElement.getId())) {
        return true;
      }
    }
    return false;
  }
  
  private GraphicInfo getNextGraphicInfo(FlowElement sourceFlowElement, FlowElement newFlowElement,
          Map<String, GraphicInfo> yMap, Set<BoundaryEvent> boundaryEventList) {
    
    GraphicInfo graphicInfo = new GraphicInfo();
    GraphicInfo sourceInfo = null;
    if(sourceFlowElement != null && yMap.containsKey(sourceFlowElement.getId())) {
    	sourceInfo = yMap.get(sourceFlowElement.getId());
    	if(sourceFlowElement instanceof BoundaryEvent && newFlowElement instanceof Task) {
    		sourceInfo.x -= 38;
    	}
    }
    
    int x = 0;
    int y = 0;
    if(sourceInfo != null) {
      x = sourceInfo.x;
      y = sourceInfo.y;
      
      if(sourceFlowElement instanceof BoundaryEvent) {
        y += SEQUENCEFLOW_WIDTH + TASK_HEIGHT;
        
      } else {
        
        if(multipleOutgoingSequenceFlows(sourceFlowElement, bpmnParser.sequenceFlowList) > 1) {
          if(sourceFlowElement instanceof Gateway) {
          	x += GATEWAY_WIDTH;
          } else if(sourceFlowElement instanceof Task) {
          	x += TASK_WIDTH;
          } else if(sourceFlowElement instanceof Event) {
          	x += EVENT_WIDTH;
          } else {
          	x += TASK_WIDTH;
          }
          y = calculateDirectElementChildY(sourceFlowElement, newFlowElement.getId(), sourceInfo.y, bpmnParser.sequenceFlowList);
          
        } else if(sourceFlowElement instanceof Gateway) {
        	x += GATEWAY_WIDTH;
          if(newFlowElement instanceof Task) {
            y -= 7;
          } else if(newFlowElement instanceof EndEvent) {
            y += 3;
          }
            
        } else if(sourceFlowElement instanceof Event) {
          x += EVENT_WIDTH;
          if(newFlowElement instanceof Task) {
            y -= 10;
          }  
          
        } else if(sourceFlowElement instanceof SubProcess) {
          x += sourceInfo.width;
          int height = 0;
          if(newFlowElement instanceof Task) {
          	height = TASK_HEIGHT;
          } else if(newFlowElement instanceof Gateway) {
          	height = GATEWAY_HEIGHT;
          } else if(newFlowElement instanceof Event) {
          	height = EVENT_HEIGHT;
          }
          y += ((sourceInfo.height / 2) - (height / 2));
        } else {
          x += TASK_WIDTH;
        }
        x += SEQUENCEFLOW_WIDTH;
        
        if(newFlowElement instanceof EndEvent && sourceFlowElement instanceof Task) {
          y += 10;
        }
        
        if(multipleIncomingSequenceFlows(newFlowElement, bpmnParser.sequenceFlowList) > 1) {
        	
          y = calculateDivergingElementY(newFlowElement, bpmnParser.sequenceFlowList);
          x = getMaxX(newFlowElement.getId());
          
        } else if(newFlowElement instanceof Gateway){
          if(sourceFlowElement instanceof Task) {
            y += 7;
          } else if(sourceFlowElement instanceof StartEvent) {
            y -= 2;
          }
        }
      }
      
    } else {
      x = START_X;
      y = START_Y;
    }
    
    graphicInfo.x = x;
    graphicInfo.y = y;
    
    if(newFlowElement instanceof Event) {
      graphicInfo.height = EVENT_HEIGHT;
      if(newFlowElement instanceof BoundaryEvent) {
        BoundaryEvent boundaryEvent = (BoundaryEvent) newFlowElement;
        if(boundaryEvent.getAttachedToRef() instanceof SubProcess && yMap.containsKey(boundaryEvent.getAttachedToRef().getId())) {
          GraphicInfo attachGraphInfo = yMap.get(boundaryEvent.getAttachedToRef().getId());
          graphicInfo.x = attachGraphInfo.x + attachGraphInfo.width / 2;
          graphicInfo.y = attachGraphInfo.y + attachGraphInfo.height - 15;
        } else if(yMap.containsKey(boundaryEvent.getAttachedToRef().getId())) {
        	GraphicInfo attachGraphInfo = yMap.get(boundaryEvent.getAttachedToRef().getId());
        	graphicInfo.x = attachGraphInfo.x + TASK_WIDTH - 25;
        	graphicInfo.y = attachGraphInfo.y + TASK_HEIGHT - 25;
        }
      }
    } else if(newFlowElement instanceof Gateway) {
      graphicInfo.height = GATEWAY_HEIGHT;
    } else if(newFlowElement instanceof SubProcess) {
      int width = 0;
      int height = 0;
      Map<String, GraphicInfo> subYMap = new HashMap<String, GraphicInfo>();
      List<FlowElement> subFlowElementList = new ArrayList<FlowElement>();
      
      // first go for start event
      for(FlowElement startElement : ((SubProcess) newFlowElement).getFlowElements()) {
        if(startElement instanceof StartEvent) {
        	GraphicInfo subGraphicInfo = new GraphicInfo();
          subGraphicInfo.height = EVENT_HEIGHT;
          subGraphicInfo.x = 20;
          subGraphicInfo.y = 50;
          subYMap.put(startElement.getId(), subGraphicInfo);
          subFlowElementList.add(startElement);
          if(subGraphicInfo.x > width)
            width = subGraphicInfo.x;
          if(subGraphicInfo.y > height)
            height = subGraphicInfo.y;
          
          processSubDiagramInSequence(startElement, (SubProcess) newFlowElement, 
          		subFlowElementList, subYMap, boundaryEventList);
          
          break;
        }
      }
      
      for(String subElementId : subYMap.keySet()) {
      	GraphicInfo subGraphicInfo = subYMap.get(subElementId);
      	if(subGraphicInfo.x > width)
          width = subGraphicInfo.x;
        if(subGraphicInfo.y > height)
          height = subGraphicInfo.y;
      }
      
      
      graphicInfo.width = width + 80;
      graphicInfo.height = height + 40 + TASK_HEIGHT;
      
      if(yMap.containsKey(sourceFlowElement.getId())) {
      	GraphicInfo subSourceInfo = yMap.get(sourceFlowElement.getId());
      	graphicInfo.y = subSourceInfo.y + (subSourceInfo.height / 2) - (graphicInfo.height / 2);
      }
      
      addBpmnElementToDiagram(newFlowElement, graphicInfo, diagram);
      
      int differenceInitialStartEventAndSubProcessHeight = (graphicInfo.height / 2) - 50 - (EVENT_HEIGHT / 2);
      
      ILinkService linkService = Graphiti.getLinkService();
      List<PictogramElement> pictoList = linkService.getPictogramElements(diagram, (SubProcess) newFlowElement);
      if(pictoList != null && pictoList.size() > 0) {
        ContainerShape parent = (ContainerShape) pictoList.get(0);
        for (FlowElement subFlowElement : subFlowElementList) {
        	GraphicInfo subInfoElem = subYMap.get(subFlowElement.getId());
        	subInfoElem.y += differenceInitialStartEventAndSubProcessHeight;
          addBpmnElementToDiagram(subFlowElement, subYMap.get(subFlowElement.getId()), parent);
        }
      }
      
    } else {
      graphicInfo.height = TASK_HEIGHT;
    }
    
    graphicInfo.element = newFlowElement;
    return graphicInfo;
  }
  
  private void processSubDiagramInSequence(FlowElement previousElement, SubProcess subProcess, 
  		List<FlowElement> subFlowElementList, Map<String, GraphicInfo> subYMap, Set<BoundaryEvent> boundaryEventList) {
  	
  	for(SequenceFlowModel sequence : bpmnParser.sequenceFlowList) {
    	if(sequence.sourceRef != null && sequence.sourceRef.equals(previousElement.getId()) &&
    			sequence.targetRef != null && subYMap.containsKey(sequence.targetRef) == false) {
    		
    		for(FlowElement subFlowElement : subProcess.getFlowElements()) {
    			if(sequence.targetRef.equals(subFlowElement.getId())) {
    				FlowElement subSourceElement = sourceRef(subFlowElement.getId(), subYMap);
            GraphicInfo subGraphicInfo = getNextGraphicInfo(subSourceElement, subFlowElement, subYMap, boundaryEventList);
            if(subGraphicInfo.y < 0) {
              subGraphicInfo.y = 0;
            }
            
            subYMap.put(subFlowElement.getId(), subGraphicInfo);
            subFlowElementList.add(subFlowElement);
            
            processSubDiagramInSequence(subFlowElement, subProcess, subFlowElementList, subYMap, boundaryEventList);
            
            if(boundaryEventList.size() > 0 && 
			      		(subFlowElement instanceof CallActivity || subFlowElement instanceof Task)) {
			      	
			      	for (BoundaryEvent boundaryEvent : boundaryEventList) {
			      		if(boundaryEvent.getAttachedToRef() != null && boundaryEvent.getAttachedToRef().getId().equals(subFlowElement.getId())) {
			      			
			      			GraphicInfo boundaryGraphicInfo = getNextGraphicInfo(null, boundaryEvent, subYMap, boundaryEventList);
			      			subYMap.put(boundaryEvent.getId(), boundaryGraphicInfo);
			      			subFlowElementList.add(boundaryEvent);
			      			
						      processSubDiagramInSequence(boundaryEvent, subProcess, subFlowElementList, subYMap, boundaryEventList);
			      		}
              }
			      }
            
    				break;
    			}
    		}
    	}
    }
  }
  
  private FlowElement sourceRef(String id, Map<String, GraphicInfo> graphInfoMap) {
    FlowElement sourceRef = null;
    String sourceRefString = null;
    for (SequenceFlowModel sequenceFlowModel : bpmnParser.sequenceFlowList) {
      if(sequenceFlowModel.targetRef.equals(id) && graphInfoMap.containsKey(sequenceFlowModel.sourceRef)) {
        sourceRefString = sequenceFlowModel.sourceRef;
      }
    }
    if(sourceRefString != null) {
      for (FlowElement flowElement : bpmnParser.bpmnList) {
        if(flowElement.getId().equals(sourceRefString)) {
          sourceRef = flowElement;
        }
      }
    }
    return sourceRef;
  }
  
  private int getMaxX(String id) {
    int maxX = 0;
    String sourceRef = null;
    for (SequenceFlowModel sequenceFlowModel : bpmnParser.sequenceFlowList) {
      if(sequenceFlowModel.targetRef.equals(id)) {
        if(yMap.containsKey(sequenceFlowModel.sourceRef)) {
          int sourceX = yMap.get(sequenceFlowModel.sourceRef).x;
          if(sourceX > maxX) {
            maxX = sourceX;
            sourceRef = sequenceFlowModel.sourceRef;
          }
        } else {
        	throw new FlowSourceNotFoundException();
        }
      }
    }
    if(sourceRef != null) {
      for (FlowElement flowElement : bpmnParser.bpmnList) {
        if(flowElement.getId().equals(sourceRef)) {
          if(flowElement instanceof Event) {
            maxX += EVENT_WIDTH;
          } else if(flowElement instanceof Gateway) {
            maxX += GATEWAY_WIDTH;
          } else if(flowElement instanceof Task) {
            maxX += TASK_WIDTH;
          }
        }
      }
    }
    maxX += SEQUENCEFLOW_WIDTH;
    return maxX;
  }
  
  private int multipleOutgoingSequenceFlows(FlowElement element, List<SequenceFlowModel> sequenceFlowList) {
    int counter = 0;
    for (SequenceFlowModel sequenceFlowModel : sequenceFlowList) {
      if(sequenceFlowModel.sourceRef.equals(element.getId())) {
        counter++;
      }
    }
    return counter;
  }
  
  private int multipleIncomingSequenceFlows(FlowElement element, List<SequenceFlowModel> sequenceFlowList) {
    int counter = 0;
    for (SequenceFlowModel sequenceFlowModel : sequenceFlowList) {
      if(sequenceFlowModel.targetRef.equals(element.getId())) {
        counter++;
      }
    }
    return counter;
  }
  
  private int calculateDivergingElementY(FlowElement element, List<SequenceFlowModel> sequenceFlowList) {
  	
  	if(element instanceof Gateway) {
	  	GraphicInfo previousGateway = getDivergingGateway(element, sequenceFlowList, element.getClass().getName());
	  	if(previousGateway != null) {
	  		return previousGateway.y;
	  	}
  	}
  	
    int y = 0;
    List<SequenceFlowModel> flowList = new ArrayList<SequenceFlowModel>();
    for (SequenceFlowModel sequenceFlowModel : sequenceFlowList) {
      if(sequenceFlowModel.targetRef.equals(element.getId())) {
      	flowList.add(sequenceFlowModel);
      }
    }
    
    if(flowList.size() % 2 == 0) {
      int middleTop = (flowList.size() / 2) - 1;
      int middleBottom = flowList.size() / 2;
      
      if(yMap.containsKey(flowList.get(middleTop).sourceRef) && yMap.containsKey(flowList.get(middleBottom).sourceRef)) {
      	
      	GraphicInfo middleTopInfo = yMap.get(flowList.get(middleTop).sourceRef);
      	GraphicInfo middleBottomInfo = yMap.get(flowList.get(middleBottom).sourceRef);
      	y = middleTopInfo.y + middleTopInfo.height;
      	int middleOfGap = (middleBottomInfo.y - (middleTopInfo.y + middleTopInfo.height)) / 2;
      	if(element instanceof Task) {
      		y += middleOfGap - (TASK_HEIGHT / 2);
      	} else if(element instanceof Gateway) {
      		y += middleOfGap - (GATEWAY_HEIGHT / 2);
      	} else if(element instanceof Event) {
      		y += middleOfGap - (EVENT_HEIGHT / 2);
      	} else {
      		y += middleOfGap - (TASK_HEIGHT / 2);
      	}
      	
      } else {
      	throw new FlowSourceNotFoundException();
      }
    	
    } else {
      int middle = ((flowList.size() - 1) / 2);
      if(yMap.containsKey(flowList.get(middle).sourceRef)) {
      	GraphicInfo middleInfo = yMap.get(flowList.get(middle).sourceRef);
      	if(middleInfo.element instanceof Task) {
      		if(element instanceof Task) {
      			y = middleInfo.y;
      		} else if(element instanceof Gateway) {
      			y = middleInfo.y + 7;
      		} else if(element instanceof Event) {
      			y = middleInfo.y + 10;
      		} else {
      			y = middleInfo.y;
      		}
      	} else if(middleInfo.element instanceof Gateway) {
      		if(element instanceof Task) {
      			y = middleInfo.y - 7;
      		} else if(element instanceof Gateway) {
      			y = middleInfo.y;
      		} else if(element instanceof Event) {
      			y = middleInfo.y + 2;
      		} else {
      			y = middleInfo.y - 7;
      		}
      	} else if(middleInfo.element instanceof Event) {
      		if(element instanceof Task) {
      			y = middleInfo.y - 10;
      		} else if(element instanceof Gateway) {
      			y = middleInfo.y - 2;
      		} else if(element instanceof Event) {
      			y = middleInfo.y;
      		} else {
      			y = middleInfo.y - 10;
      		}
      	} else {
      		y = middleInfo.y;
      	}
      	
      } else {
      	throw new FlowSourceNotFoundException();
      }
    }
    return y;
  }
  
  private GraphicInfo getDivergingGateway(FlowElement element, List<SequenceFlowModel> sequenceFlowList, String type) {
  	for (SequenceFlowModel sequenceFlowModel : sequenceFlowList) {
  		
      if(sequenceFlowModel.targetRef.equals(element.getId())) {
        
      	FlowElement sourceElement = sourceRef(sequenceFlowModel.targetRef, yMap);
      	if(sourceElement == null) {
      		throw new FlowSourceNotFoundException();
      	}
      	
      	if(sourceElement.getClass().getName().equals(type)) {
      		return yMap.get(sourceElement.getId());
      	}
      	
      	GraphicInfo sourceTreeInfo = getDivergingGateway(sourceElement, sequenceFlowList, type);
      	if(sourceTreeInfo != null) {
      		return sourceTreeInfo;
      	}
      }
    }
  	throw null;
  }
  
  private int calculateDirectElementChildY(FlowElement element, String id, int elementY, List<SequenceFlowModel> sequenceFlowList) {
    int y = 0;
    int counter = 0;
    int index = 0;
    for (SequenceFlowModel sequenceFlowModel : sequenceFlowList) {
      if(sequenceFlowModel.sourceRef.equals(element.getId())) {
        counter++;
      }
      if(sequenceFlowModel.targetRef.equals(id)) {
        index = counter;
      }
    }
    
    int childHeight = 20;
    if(element instanceof Gateway) {
    	childHeight += GATEWAY_HEIGHT;
    } else if(element instanceof Task) {
    	childHeight += TASK_HEIGHT;
    } else if(element instanceof Event) {
    	childHeight += EVENT_HEIGHT;
    } else {
    	childHeight += TASK_HEIGHT;
    }
    
    if(counter % 2 == 0) {
      if(index > (counter / 2)) {
        y = elementY + ((index - (counter / 2)) * childHeight);
      } else {
        y = elementY - (((counter / 2) - index + 1) * childHeight);
      }
    } else {
      int middle = ((counter - 1) / 2) + 1;
      if(index > middle) {
        y = elementY + ((index - middle) * childHeight);
      } else if(index == middle) {
        y = elementY;
      } else {
        y = elementY - ((middle - index) * childHeight);
      }
    }
    return y;
  }
  
  private void drawDiagramWithBPMNDI(Diagram diagram, IFeatureProvider featureProvider, List<FlowElement> bpmnList, 
          List<SequenceFlowModel> sequenceFlowList, Map<String, GraphicInfo> locationMap) {
    
    for (FlowElement flowElement : bpmnList) {
      String elementid = flowElement.getId();
      GraphicInfo graphicInfo = locationMap.get(elementid);
      addBpmnElementToDiagram(flowElement, graphicInfo, diagram);
    }
    drawSequenceFlows();
  }
  
  private void setFriendlyIds() {
    Map<String, Integer> idMap = new HashMap<String, Integer>();
    for (FlowElement flowElement : bpmnParser.bpmnList) {
    	if(StringUtils.isEmpty(flowElement.getId()) == false && 
    			flowElement.getId().matches("sid-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}") == false) {
    		
    		continue;
    	}
      if(flowElement instanceof StartEvent) {
        flowElement.setId(getNextId("startevent", idMap));
      } else if(flowElement instanceof EndEvent) {
        if(((EndEvent) flowElement).getEventDefinitions().size() > 0) {
          flowElement.setId(getNextId("errorendevent", idMap));
        } else {
          flowElement.setId(getNextId("endevent", idMap));
        }
      } else if(flowElement instanceof ExclusiveGateway) {
        flowElement.setId(getNextId("exclusivegateway", idMap));
      } else if(flowElement instanceof InclusiveGateway) {
        flowElement.setId(getNextId("inclusivegateway", idMap));
      } else if(flowElement instanceof ParallelGateway) {
        flowElement.setId(getNextId("parallelgateway", idMap));
      } else if(flowElement instanceof UserTask) {
        flowElement.setId(getNextId("usertask", idMap));
      } else if(flowElement instanceof ScriptTask) {
        flowElement.setId(getNextId("scripttask", idMap));
      } else if(flowElement instanceof ServiceTask) {
        flowElement.setId(getNextId("servicetask", idMap));
      } else if(flowElement instanceof ManualTask) {
        flowElement.setId(getNextId("manualtask", idMap));
      } else if(flowElement instanceof ReceiveTask) {
        flowElement.setId(getNextId("receivetask", idMap));
      } else if(flowElement instanceof BusinessRuleTask) {
        flowElement.setId(getNextId("businessruletask", idMap));
      } else if(flowElement instanceof MailTask) {
        flowElement.setId(getNextId("mailtask", idMap));
      } else if(flowElement instanceof BoundaryEvent) {
        if(((BoundaryEvent) flowElement).getEventDefinitions().size() > 0) {
          EventDefinition definition = ((BoundaryEvent) flowElement).getEventDefinitions().get(0);
          if(definition instanceof ErrorEventDefinition) {
            flowElement.setId(getNextId("boundaryerror", idMap));
          } else {
            flowElement.setId(getNextId("boundarytimer", idMap));
          }
        }
      } else if(flowElement instanceof CallActivity) {
        flowElement.setId(getNextId("callactivity", idMap));
      } else if(flowElement instanceof SubProcess) {
        flowElement.setId(getNextId("subprocess", idMap));
      }
    }
  }
  
  private String getNextId(String elementName, Map<String, Integer> idMap) {
    if(idMap.containsKey(elementName)) {
      idMap.put(elementName, idMap.get(elementName) + 1);
    } else {
      idMap.put(elementName, 1);
    }
    return elementName + idMap.get(elementName);
  }
  
  private void drawSequenceFlows() {
    int sequenceCounter = 1;
    for(SequenceFlowModel sequenceFlowModel : bpmnParser.sequenceFlowList) {
      SequenceFlow sequenceFlow = Bpmn2Factory.eINSTANCE.createSequenceFlow();
      if(StringUtils.isEmpty(sequenceFlowModel.id) || sequenceFlowModel.id.matches("sid-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}")) {
      	sequenceFlow.setId("flow" + sequenceCounter);
      	sequenceCounter++;
      } else {
      	sequenceFlow.setId(sequenceFlowModel.id);
      }
      sequenceFlow.setSourceRef(getFlowNode(sequenceFlowModel.sourceRef));
      sequenceFlow.setTargetRef(getFlowNode(sequenceFlowModel.targetRef));
      if(sequenceFlow.getSourceRef() == null || sequenceFlow.getSourceRef().getId() == null || 
              sequenceFlow.getTargetRef() == null || sequenceFlow.getTargetRef().getId() == null) continue;
      if(sequenceFlowModel.conditionExpression != null) {
        sequenceFlow.setConditionExpression(sequenceFlowModel.conditionExpression);
      }
      if(sequenceFlowModel.listenerList.size() > 0) {
        sequenceFlow.getExecutionListeners().addAll(sequenceFlowModel.listenerList);
      }
      
      ContainerShape parent = null;
      SubProcess subProcess = subProcessContains(sequenceFlow.getSourceRef(), subProcessList);
      if(subProcess != null) {
        ILinkService linkService = Graphiti.getLinkService();
        List<PictogramElement> pictoList = linkService.getPictogramElements(diagram, subProcess);
        if(pictoList != null && pictoList.size() > 0) {
          parent = (ContainerShape) pictoList.get(0);
          subProcess.getFlowElements().add(sequenceFlow);
        }
      } 
      
      if(parent == null) {
        diagram.eResource().getContents().add(sequenceFlow);
        parent = diagram;
      }
      
      sequenceFlow.getSourceRef().getOutgoing().add(sequenceFlow);
      sequenceFlow.getTargetRef().getIncoming().add(sequenceFlow);
      
      EList<Shape> shapeList = parent.getChildren();
      ILinkService linkService = Graphiti.getLinkService();
      Anchor sourceAnchor = null;
      Anchor targetAnchor = null;
      for (Shape shape : shapeList) {
        FlowNode flowNode = (FlowNode) linkService.getBusinessObjectForLinkedPictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
        if(flowNode == null || flowNode.getId() == null) continue;
        
        if(sequenceFlow.getSourceRef() instanceof BoundaryEvent) {
          if(flowNode instanceof Task || flowNode instanceof SubProcess) {
            for(Shape subShape : ((ContainerShape) shape).getChildren()) {
              FlowNode subFlowNode = (FlowNode) linkService.getBusinessObjectForLinkedPictogramElement(
                      subShape.getGraphicsAlgorithm().getPictogramElement());
              if(subFlowNode == null || subFlowNode.getId() == null) continue;
              
              if(subFlowNode.getId().equals(sequenceFlow.getSourceRef().getId())) {
                EList<Anchor> anchorList = ((ContainerShape) subShape).getAnchors();
                for (Anchor anchor : anchorList) {
                  if(anchor instanceof ChopboxAnchor) {
                    sourceAnchor = anchor;
                    break;
                  }
                }
              }
            }
          }
          
        } else {
        
          if(flowNode.getId().equals(sequenceFlow.getSourceRef().getId())) {
            EList<Anchor> anchorList = ((ContainerShape) shape).getAnchors();
            for (Anchor anchor : anchorList) {
              if(anchor instanceof ChopboxAnchor) {
                sourceAnchor = anchor;
                break;
              }
            }
          }
        }
        
        if(flowNode.getId().equals(sequenceFlow.getTargetRef().getId())) {
          EList<Anchor> anchorList = ((ContainerShape) shape).getAnchors();
          for (Anchor anchor : anchorList) {
            if(anchor instanceof ChopboxAnchor) {
              targetAnchor = anchor;
              break;
            }
          }
        }
      }
      
      AddConnectionContext addContext = new AddConnectionContext(sourceAnchor, targetAnchor);
      addContext.setNewObject(sequenceFlow);
      featureProvider.addIfPossible(addContext);
      
      if(bpmnParser.defaultFlowMap.containsValue(sequenceFlowModel.id)) {
        Iterator<FlowNode> itDefaultFlow = bpmnParser.defaultFlowMap.keySet().iterator();
        while(itDefaultFlow.hasNext()) {
          FlowNode flowNode = itDefaultFlow.next();
          String defaultId = bpmnParser.defaultFlowMap.get(flowNode);
          if(defaultId.equalsIgnoreCase(sequenceFlowModel.id)) {
            if(flowNode instanceof ExclusiveGateway) {
              ((ExclusiveGateway) flowNode).setDefault(sequenceFlow);
            } else if(flowNode instanceof InclusiveGateway) {
              ((InclusiveGateway) flowNode).setDefault(sequenceFlow);
            } else {
              ((Activity) flowNode).setDefault(sequenceFlow);
            }
          }
        }
      }
    }
  }
  
  private SubProcess subProcessContains(FlowNode sourceRef, List<SubProcess> subProcessList) {
    for (SubProcess subProcess : subProcessList) {
      for (FlowElement flowElement : subProcess.getFlowElements()) {
        if(flowElement.getId().equals(sourceRef.getId())) {
          return subProcess;
        }
      }
    }
    return null;
  }
  
  private FlowNode getFlowNode(String elementid) {
    FlowNode flowNode = null;
    for(FlowElement flowElement : bpmnParser.bpmnList) {
      if(flowElement.getId().equalsIgnoreCase(elementid)) {
        flowNode = (FlowNode) flowElement;
      }
    }
    return flowNode;
  }
  
  private void addBpmnElementToDiagram(FlowElement flowElement, GraphicInfo graphicInfo,
          ContainerShape parent) {
    
    if(flowElement instanceof BoundaryEvent) {
      BoundaryEvent boundaryEvent = (BoundaryEvent) flowElement;
      AddContext addContext = new AddContext();
      boolean parentIsSubProcess = false;
      
      if(boundaryEvent.getAttachedToRef() instanceof SubProcess) {
        parentIsSubProcess = true;
      }
      
      ILinkService linkService = Graphiti.getLinkService();
      List<PictogramElement> pictoList = linkService.getPictogramElements(diagram, boundaryEvent.getAttachedToRef());
      if(pictoList != null && pictoList.size() > 0) {
        addContext.setTargetContainer((ContainerShape) pictoList.get(0));
      }
      
      addContext.setNewObject(boundaryEvent);
      if(parentIsSubProcess == true) {
        addContext.setX(addContext.getTargetContainer().getGraphicsAlgorithm().getWidth() / 2);
        addContext.setY(addContext.getTargetContainer().getGraphicsAlgorithm().getHeight() - 15);
      } else {
        addContext.setX(TASK_WIDTH - 25);
        addContext.setY(TASK_HEIGHT - 25);
      }
      
      IAddFeature addFeature = featureProvider.getAddFeature(addContext);
      if (addFeature.canAdd(addContext)) {
        addFeature.add(addContext);
      }
      return;
    }
    
    if(flowElement instanceof UserTask) {
      UserTask userTask = (UserTask) flowElement;
      if(userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0) {
        for (CandidateUser candidateUser : userTask.getCandidateUsers()) {
          diagram.eResource().getContents().add(candidateUser);
        }
      }
      if(userTask.getCandidateGroups() != null && userTask.getCandidateGroups().size() > 0) {
        for (CandidateGroup candidateGroup : userTask.getCandidateGroups()) {
          diagram.eResource().getContents().add(candidateGroup);
        }
      }
    } else if(flowElement instanceof ServiceTask) {
      ServiceTask serviceTask = (ServiceTask) flowElement;
      if(serviceTask.getFieldExtensions() != null && serviceTask.getFieldExtensions().size() > 0) {
        for (FieldExtension fieldExtension : serviceTask.getFieldExtensions()) {
          diagram.eResource().getContents().add(fieldExtension);
        }
      }
    }
    
    AddContext addContext = new AddContext();
    addContext.setNewObject(flowElement);
    addContext.setTargetContainer(parent);
    addContext.setX(graphicInfo.x);
    if(flowElement instanceof StartEvent || flowElement instanceof EndEvent) {
      if(graphicInfo.height < EVENT_HEIGHT) {
        addContext.setY(graphicInfo.y - 25);
      } else {
        addContext.setY(graphicInfo.y);
      }
    } else if(flowElement instanceof ExclusiveGateway || flowElement instanceof InclusiveGateway || 
        flowElement instanceof ParallelGateway) {
      if(graphicInfo.height < GATEWAY_HEIGHT) {
        addContext.setY(graphicInfo.y - 20);
      } else {
        addContext.setY(graphicInfo.y);
      }
    } else if(flowElement instanceof SubProcess) {
      addContext.setHeight(graphicInfo.height);
      addContext.setWidth(graphicInfo.width);
      addContext.setY(graphicInfo.y);
    } else {
      addContext.setY(graphicInfo.y);
    }
   
    IAddFeature addFeature = featureProvider.getAddFeature(addContext);
    if (addFeature.canAdd(addContext)) {
      addFeature.add(addContext);
    }
  }
}
