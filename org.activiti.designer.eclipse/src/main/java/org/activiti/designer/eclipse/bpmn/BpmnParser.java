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

package org.activiti.designer.eclipse.bpmn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.activiti.designer.bpmn2.model.ActivitiListener;
import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.BusinessRuleTask;
import org.activiti.designer.bpmn2.model.CallActivity;
import org.activiti.designer.bpmn2.model.EndEvent;
import org.activiti.designer.bpmn2.model.ErrorEventDefinition;
import org.activiti.designer.bpmn2.model.EventDefinition;
import org.activiti.designer.bpmn2.model.EventSubProcess;
import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.activiti.designer.bpmn2.model.FieldExtension;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.FlowNode;
import org.activiti.designer.bpmn2.model.FormProperty;
import org.activiti.designer.bpmn2.model.FormValue;
import org.activiti.designer.bpmn2.model.IOParameter;
import org.activiti.designer.bpmn2.model.InclusiveGateway;
import org.activiti.designer.bpmn2.model.IntermediateCatchEvent;
import org.activiti.designer.bpmn2.model.MailTask;
import org.activiti.designer.bpmn2.model.ManualTask;
import org.activiti.designer.bpmn2.model.MultiInstanceLoopCharacteristics;
import org.activiti.designer.bpmn2.model.ParallelGateway;
import org.activiti.designer.bpmn2.model.ReceiveTask;
import org.activiti.designer.bpmn2.model.ScriptTask;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.bpmn2.model.Signal;
import org.activiti.designer.bpmn2.model.SignalEventDefinition;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.Task;
import org.activiti.designer.bpmn2.model.ThrowEvent;
import org.activiti.designer.bpmn2.model.ThrowSignalEvent;
import org.activiti.designer.bpmn2.model.TimerEventDefinition;
import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoMailTask;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoScriptTask;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoStartEvent;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoUserTask;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.GraphicInfo;
import org.activiti.designer.util.preferences.Preferences;
import org.apache.commons.lang.StringUtils;

/**
 * @author Tijs Rademakers
 */
public class BpmnParser {

	private static final String ACTIVITI_EXTENSIONS_NAMESPACE = "http://activiti.org/bpmn";
	private static final String CLASS_TYPE = "classType";
	private static final String EXPRESSION_TYPE = "expressionType";
	private static final String DELEGATE_EXPRESSION_TYPE = "delegateExpressionType";
	private static final String ALFRESCO_TYPE = "alfrescoScriptType";

	public boolean bpmdiInfoFound;
	public List<SequenceFlowModel> sequenceFlowList = new ArrayList<SequenceFlowModel>();
	private List<BoundaryEventModel> boundaryList = new ArrayList<BoundaryEventModel>();
	public Map<String, List<GraphicInfo>> flowLocationMap = new HashMap<String, List<GraphicInfo>>();
	public Map<FlowNode, String> defaultFlowMap = new HashMap<FlowNode, String>();

	public void parseBpmn(XMLStreamReader xtr, Bpmn2MemoryModel model) {
		try {
			boolean processExtensionAvailable = false;
			List<SubProcess> activeSubProcessList = new ArrayList<SubProcess>();
			while (xtr.hasNext()) {
				try {
					xtr.next();
				} catch(Exception e) {
					e.printStackTrace();
					return;
				}

				if (xtr.isEndElement()  && "subProcess".equalsIgnoreCase(xtr.getLocalName())) {
					activeSubProcessList.remove(activeSubProcessList.size() - 1);
				}

				if (xtr.isStartElement() == false)
					continue;

				if (xtr.isStartElement() && "definitions".equalsIgnoreCase(xtr.getLocalName())) {

					if (xtr.getAttributeValue(null, "targetNamespace") != null) {
						model.getProcess().setNamespace(xtr.getAttributeValue(null, "targetNamespace"));
					}
				
				} else if (xtr.isStartElement() && "signal".equalsIgnoreCase(xtr.getLocalName())) {
					
					if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "id"))) {
						Signal signal = new Signal();
						signal.setId(xtr.getAttributeValue(null, "id"));
						signal.setName(xtr.getAttributeValue(null, "name"));
						model.getProcess().getSignals().add(signal);
					}
					if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "name"))) {
						model.getProcess().setName(xtr.getAttributeValue(null, "name"));
					}

				} else if (xtr.isStartElement() && "process".equalsIgnoreCase(xtr.getLocalName())) {
					
					processExtensionAvailable = true;
					if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "id"))) {
						model.getProcess().setId(xtr.getAttributeValue(null, "id"));
					}
					if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "name"))) {
						model.getProcess().setName(xtr.getAttributeValue(null, "name"));
					}
					
				} else if (xtr.isStartElement() && "documentation".equalsIgnoreCase(xtr.getLocalName())) {
					
					String docText = xtr.getElementText();
					if(model.getProcess() != null && StringUtils.isEmpty(docText) == false) {
						
						if(activeSubProcessList.size() > 0) {
							activeSubProcessList.get(activeSubProcessList.size() - 1).setDocumentation(docText);
						} else {
							model.getProcess().setDocumentation(docText);
						}
					}					

				} else if (processExtensionAvailable == true && xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					
					model.getProcess().getExecutionListeners().addAll(parseListeners(xtr));
					processExtensionAvailable = false;

				} else {

					FlowNode currentActivity = null;
					String elementId = xtr.getAttributeValue(null, "id");
					processExtensionAvailable = false;

					if (xtr.isStartElement() && "startEvent".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseStartEvent(xtr);
					
					} else if (xtr.isStartElement() && "subProcess".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseSubProcess(xtr);
						activeSubProcessList.add((SubProcess) currentActivity);
						
					} else if (activeSubProcessList.size() > 0 && xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
						activeSubProcessList.get(activeSubProcessList.size() - 1).getExecutionListeners().addAll(parseListeners(xtr));

					} else if (activeSubProcessList.size() > 0 && xtr.isStartElement() && "multiInstanceLoopCharacteristics".equalsIgnoreCase(xtr.getLocalName())) {
						
						MultiInstanceLoopCharacteristics multiInstanceDef = new MultiInstanceLoopCharacteristics();
						activeSubProcessList.get(activeSubProcessList.size() - 1).setLoopCharacteristics(multiInstanceDef);
						parseMultiInstanceDef(multiInstanceDef, xtr);
	
					} else if (xtr.isStartElement() && "userTask".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseUserTask(xtr);
					
					} else if (xtr.isStartElement() && "serviceTask".equalsIgnoreCase(xtr.getLocalName())) {
						
						if ("mail".equalsIgnoreCase(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "type"))) {
							currentActivity = parseMailTask(xtr, "serviceTask");
						} else if ("org.alfresco.repo.workflow.activiti.script.AlfrescoScriptDelegate".equalsIgnoreCase(
								xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "class"))) {
							currentActivity = parseAlfrescoScriptTask(xtr);
						} else {
							currentActivity = parseServiceTask(xtr);
						}
					
					} else if (xtr.isStartElement() && "sendTask".equalsIgnoreCase(xtr.getLocalName())) {
						
						currentActivity = parseMailTask(xtr, "sendTask");
					
					} else if (xtr.isStartElement() && "task".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseTask(xtr);

					} else if (xtr.isStartElement() && "scriptTask".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseScriptTask(xtr);

					} else if (xtr.isStartElement() && "manualTask".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseManualTask(xtr);

					} else if (xtr.isStartElement() && "receiveTask".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseReceiveTask(xtr);

					} else if (xtr.isStartElement() && "businessRuleTask".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseBusinessRuleTask(xtr);

					} else if (xtr.isStartElement() && "callActivity".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseCallActivity(xtr);

					} else if (xtr.isStartElement() && "endEvent".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseEndEvent(xtr);
					
					} else if (xtr.isStartElement() && "intermediateCatchEvent".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseIntermediateCatchEvent(xtr);
					
					} else if (xtr.isStartElement() && "intermediateThrowEvent".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseIntermediateThrowEvent(xtr);

					} else if (xtr.isStartElement() && "exclusiveGateway".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseExclusiveGateway(xtr);

					} else if (xtr.isStartElement() && "inclusiveGateway".equalsIgnoreCase(xtr.getLocalName())) {
						currentActivity = parseInclusiveGateway(xtr);

          } else if (xtr.isStartElement() && "parallelGateway".equalsIgnoreCase(xtr.getLocalName())) {
          	currentActivity = parseParallelGateway(xtr);

					} else if (xtr.isStartElement() && "boundaryEvent".equalsIgnoreCase(xtr.getLocalName())) {
						String elementid = xtr.getAttributeValue(null, "id");
						BoundaryEventModel event = parseBoundaryEvent(xtr);
						event.boundaryEvent.setId(elementid);
						boundaryList.add(event);

					} else if (xtr.isStartElement() && "sequenceFlow".equalsIgnoreCase(xtr.getLocalName())) {
						SequenceFlowModel sequenceFlow = parseSequenceFlow(xtr);
						sequenceFlowList.add(sequenceFlow);

					} else if (xtr.isStartElement() && "BPMNShape".equalsIgnoreCase(xtr.getLocalName())) {
						bpmdiInfoFound = true;
						String id = xtr.getAttributeValue(null, "bpmnElement");
						while (xtr.hasNext()) {
							xtr.next();
							if (xtr.isStartElement() && "Bounds".equalsIgnoreCase(xtr.getLocalName())) {
								GraphicInfo graphicInfo = new GraphicInfo();
								graphicInfo.x = Double.valueOf(xtr.getAttributeValue(null, "x")).intValue();
								graphicInfo.y = Double.valueOf(xtr.getAttributeValue(null, "y")).intValue();
								graphicInfo.height = Double.valueOf(xtr.getAttributeValue(null, "height")).intValue();
								graphicInfo.width = Double.valueOf(xtr.getAttributeValue(null, "width")).intValue();
								model.addGraphicInfo(id, graphicInfo);
								break;
							}
						}
						
					} else if(xtr.isStartElement() && "BPMNEdge".equalsIgnoreCase(xtr.getLocalName())) {
						String id = xtr.getAttributeValue(null, "bpmnElement");
						List<GraphicInfo> wayPointList = new ArrayList<GraphicInfo>();
						while (xtr.hasNext()) {
							xtr.next();
							if (xtr.isStartElement() && "waypoint".equalsIgnoreCase(xtr.getLocalName())) {
								GraphicInfo graphicInfo = new GraphicInfo();
								graphicInfo.x = Double.valueOf(xtr.getAttributeValue(null, "x")).intValue();
								graphicInfo.y = Double.valueOf(xtr.getAttributeValue(null, "y")).intValue();
								wayPointList.add(graphicInfo);
							} else if(xtr.isEndElement() && "BPMNEdge".equalsIgnoreCase(xtr.getLocalName())) {
								break;
							}
						}
						flowLocationMap.put(id, wayPointList);
					}
					
					if(currentActivity != null) {
						
						currentActivity.setId(elementId);
						
						if(currentActivity instanceof SubProcess) {
							if(isInSubProcess(activeSubProcessList)) {
								activeSubProcessList.get(activeSubProcessList.size() - 2).getFlowElements().add(currentActivity);
								
							} else {
								model.addFlowElement(currentActivity);
							}
							
						} else if (activeSubProcessList.size() > 0) {
							activeSubProcessList.get(activeSubProcessList.size() - 1).getFlowElements().add(currentActivity);
						} else {
							model.addFlowElement(currentActivity);
						}
					}
				}
			}

			for (BoundaryEventModel boundaryModel : boundaryList) {
				FlowNode flowNode = getFlowNode(boundaryModel.attachedRef, model.getProcess().getFlowElements());
				if(flowNode != null) {
					boundaryModel.boundaryEvent.setAttachedToRef((Activity) flowNode);
					((Activity) flowNode).getBoundaryEvents().add(boundaryModel.boundaryEvent);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isInSubProcess(List<SubProcess> subProcessList) {
		if(subProcessList.size() > 1) {
			return true;
		} else {
			return false;
		}
	}
	
	private FlowNode getFlowNode(String elementid, List<FlowElement> elementList) {
    FlowNode flowNode = null;
    for(FlowElement flowElement : elementList) {
      if(flowElement.getId().equalsIgnoreCase(elementid)) {
        flowNode = (FlowNode) flowElement;
        break;
      }
      
      if(flowElement instanceof SubProcess) {
      	flowNode = getFlowNode(elementid, ((SubProcess) flowElement).getFlowElements());
      }
    }
    return flowNode;
  }

	private StartEvent parseStartEvent(XMLStreamReader xtr) {
		StartEvent startEvent = null;
		if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "formKey") != null) {
			String[] formTypes = PreferencesUtil.getStringArray(Preferences.ALFRESCO_FORMTYPES_STARTEVENT);
			for (String form : formTypes) {
				if (form.equals(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "formKey"))) {
					startEvent = new AlfrescoStartEvent();
				}
			}
		}
		if (startEvent == null) {
			startEvent = new StartEvent();
		}
		if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "initiator") != null) {
			startEvent.setInitiator(xtr.getAttributeValue(
			    ACTIVITI_EXTENSIONS_NAMESPACE, "initiator"));
		}
		startEvent.setName("Start");
		if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "formKey") != null) {
			startEvent.setFormKey(xtr.getAttributeValue(
			    ACTIVITI_EXTENSIONS_NAMESPACE, "formKey"));
		}
		boolean readyWithStartEvent = false;
		try {
			while (readyWithStartEvent == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement()
				    && "formProperty".equalsIgnoreCase(xtr.getLocalName())) {
					FormProperty property = new FormProperty();
					startEvent.getFormProperties().add(property);
					parseFormProperty(property, xtr);
				
				} else if (xtr.isStartElement() && "errorEventDefinition".equalsIgnoreCase(xtr.getLocalName())) {
						ErrorEventDefinition errorDef = new ErrorEventDefinition();
						startEvent.getEventDefinitions().add(errorDef);
						if (xtr.getAttributeValue(null, "errorRef") != null) {
							errorDef.setErrorCode(xtr.getAttributeValue(null, "errorRef"));
						}

				} else if (xtr.isStartElement()
				    && "timeDuration".equalsIgnoreCase(xtr.getLocalName())) {
					addTimerEvent(startEvent, xtr.getLocalName(), xtr.getElementText());

				} else if (xtr.isStartElement()
				    && "timeDate".equalsIgnoreCase(xtr.getLocalName())) {
					addTimerEvent(startEvent, xtr.getLocalName(), xtr.getElementText());

				} else if (xtr.isStartElement()
				    && "timeCycle".equalsIgnoreCase(xtr.getLocalName())) {
					addTimerEvent(startEvent, xtr.getLocalName(), xtr.getElementText());

				} else if (xtr.isEndElement()
				    && "startEvent".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithStartEvent = true;
				}
			}
		} catch (Exception e) {
		}
		return startEvent;
	}

	private void addTimerEvent(StartEvent startEvent, String type, String value) {
		TimerEventDefinition eventDef = new TimerEventDefinition();
		if ("timeDuration".equalsIgnoreCase(type)) {
			eventDef.setTimeDuration(value);
		} else if ("timeDate".equalsIgnoreCase(type)) {
			eventDef.setTimeDate(value);
		} else {
			eventDef.setTimeCycle(value);
		}
		if (startEvent.getEventDefinitions().size() == 0) {
			startEvent.getEventDefinitions().add(eventDef);
		} else {
			startEvent.getEventDefinitions().set(0, eventDef);
		}
	}

	private void parseFormProperty(FormProperty property, XMLStreamReader xtr) {
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "id"))) {
			property.setId(xtr.getAttributeValue(null, "id"));
		}
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "name"))) {
			property.setName(xtr.getAttributeValue(null, "name"));
		}
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "type"))) {
			property.setType(xtr.getAttributeValue(null, "type"));
		}
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "value"))) {
			property.setValue(xtr.getAttributeValue(null, "value"));
		}
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "variable"))) {
			property.setVariable(xtr.getAttributeValue(null, "variable"));
		}
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "expression"))) {
			property.setExpression(xtr.getAttributeValue(null, "expression"));
		}
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "datePattern"))) {
			property.setDatePattern(xtr.getAttributeValue(null, "datePattern"));
		}
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "required"))) {
			property.setRequired(Boolean.valueOf(xtr.getAttributeValue(null, "required")));
		}
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "readable"))) {
			property.setReadable(Boolean.valueOf(xtr.getAttributeValue(null, "readable")));
		}
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "writable"))) {
			property.setWriteable(Boolean.valueOf(xtr.getAttributeValue(null, "writable")));
		}
		
		boolean readyWithFormProperty = false;
		try {
			while (readyWithFormProperty == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "value".equalsIgnoreCase(xtr.getLocalName())) {
					FormValue value = new FormValue();
					value.setId(xtr.getAttributeValue(null, "id"));
					value.setName(xtr.getAttributeValue(null, "name"));
					property.getFormValues().add(value);

				} else if (xtr.isEndElement() && "formProperty".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithFormProperty = true;
				}
			}
		} catch (Exception e) {
		}
	}

	private void parseMultiInstanceDef(MultiInstanceLoopCharacteristics multiInstanceDef, XMLStreamReader xtr) {
		if (xtr.getAttributeValue(null, "isSequential") != null) {
			multiInstanceDef.setSequential(Boolean.valueOf(xtr.getAttributeValue(null, "isSequential")));
		}

		if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "collection") != null) {
			multiInstanceDef.setInputDataItem(xtr.getAttributeValue(
			    ACTIVITI_EXTENSIONS_NAMESPACE, "collection"));
		}
		if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "elementVariable") != null) {
			multiInstanceDef.setElementVariable(xtr.getAttributeValue(
			    ACTIVITI_EXTENSIONS_NAMESPACE, "elementVariable"));
		}

		boolean readyWithMultiInstance = false;
		try {
			while (readyWithMultiInstance == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "loopCardinality".equalsIgnoreCase(xtr.getLocalName())) {
					multiInstanceDef.setLoopCardinality(xtr.getElementText());

				} else if (xtr.isStartElement() && "loopDataInputRef".equalsIgnoreCase(xtr.getLocalName())) {
					multiInstanceDef.setInputDataItem(xtr.getElementText());

				} else if (xtr.isStartElement() && "inputDataItem".equalsIgnoreCase(xtr.getLocalName())) {
					if (xtr.getAttributeValue(null, "name") != null) {
						multiInstanceDef.setElementVariable(xtr.getAttributeValue(null, "name"));
					}

				} else if (xtr.isStartElement() && "completionCondition".equalsIgnoreCase(xtr.getLocalName())) {
					multiInstanceDef.setCompletionCondition(xtr.getElementText());

				} else if (xtr.isEndElement() && "multiInstanceLoopCharacteristics".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithMultiInstance = true;
				}
			}
		} catch (Exception e) {
		}
	}

	private EndEvent parseEndEvent(XMLStreamReader xtr) {
		EndEvent endEvent = new EndEvent();
		endEvent.setName("End");
		boolean readyWithEndEvent = false;
		try {
			while (readyWithEndEvent == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "errorEventDefinition".equalsIgnoreCase(xtr.getLocalName())) {
					ErrorEventDefinition errorDef = new ErrorEventDefinition();
					endEvent.getEventDefinitions().add(errorDef);
					if (xtr.getAttributeValue(null, "errorRef") != null) {
						errorDef.setErrorCode(xtr.getAttributeValue(null, "errorRef"));
					}

				} else if (xtr.isEndElement() && "endEvent".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithEndEvent = true;
				}
			}
		} catch (Exception e) {
		}
		return endEvent;
	}

	private SubProcess parseSubProcess(XMLStreamReader xtr) {
		SubProcess subProcess = null;
		if(StringUtils.isNotEmpty(xtr.getAttributeValue(null, "triggeredByEvent")) && 
		        "true".equalsIgnoreCase(xtr.getAttributeValue(null, "triggeredByEvent"))) {
		  
			subProcess = new EventSubProcess();
		} else {
			subProcess = new SubProcess();
		}
		String name = xtr.getAttributeValue(null, "name");
		if (name != null) {
			subProcess.setName(name);
		} else {
			subProcess.setName(xtr.getAttributeValue(null, "id"));
		}
		subProcess.setAsynchronous(parseAsync(xtr));
		return subProcess;
	}

	private ExclusiveGateway parseExclusiveGateway(XMLStreamReader xtr) {
		ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
		String name = xtr.getAttributeValue(null, "name");
		if (name != null) {
			exclusiveGateway.setName(name);
		} else {
			exclusiveGateway.setName(xtr.getAttributeValue(null, "id"));
		}
		if (xtr.getAttributeValue(null, "default") != null) {
			defaultFlowMap.put(exclusiveGateway, xtr.getAttributeValue(null, "default"));
		}
		return exclusiveGateway;
	}
	
	private InclusiveGateway parseInclusiveGateway(XMLStreamReader xtr) {
    InclusiveGateway inclusiveGateway = new InclusiveGateway();
    String name = xtr.getAttributeValue(null, "name");
    if (name != null) {
      inclusiveGateway.setName(name);
    } else {
      inclusiveGateway.setName(xtr.getAttributeValue(null, "id"));
    }
    if (xtr.getAttributeValue(null, "default") != null) {
      defaultFlowMap.put(inclusiveGateway, xtr.getAttributeValue(null, "default"));
    }
    return inclusiveGateway;
  }

	private ParallelGateway parseParallelGateway(XMLStreamReader xtr) {
		ParallelGateway parallelGateway = new ParallelGateway();
		parallelGateway.setName(xtr.getAttributeValue(null, "name"));
		return parallelGateway;
	}

	private SequenceFlowModel parseSequenceFlow(XMLStreamReader xtr) {
		SequenceFlowModel sequenceFlow = new SequenceFlowModel();
		sequenceFlow.sourceRef = xtr.getAttributeValue(null, "sourceRef");
		sequenceFlow.targetRef = xtr.getAttributeValue(null, "targetRef");
		sequenceFlow.id = xtr.getAttributeValue(null, "id");
		sequenceFlow.name = xtr.getAttributeValue(null, "name");
		sequenceFlow.conditionExpression = parseSequenceFlowCondition(xtr, sequenceFlow);;
		return sequenceFlow;
	}

	private static String parseSequenceFlowCondition(XMLStreamReader xtr, SequenceFlowModel sequenceFlow) {
		String condition = null;
		if (xtr.getAttributeValue(null, "name") != null && xtr.getAttributeValue(null, "name").contains("${")) {
			condition = xtr.getAttributeValue(null, "name");
		}
		boolean readyWithSequenceFlow = false;
		try {
			while (readyWithSequenceFlow == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "conditionExpression".equalsIgnoreCase(xtr.getLocalName())) {
					condition = xtr.getElementText();

				} else if (xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					sequenceFlow.listenerList.addAll(parseListeners(xtr));

				} else if (xtr.isEndElement() && "sequenceFlow".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithSequenceFlow = true;
				}
			}
		} catch (Exception e) {
		}
		return condition;
	}

	private UserTask parseUserTask(XMLStreamReader xtr) {
		UserTask userTask = null;
		if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "formKey") != null) {
			String[] formTypes = PreferencesUtil
			    .getStringArray(Preferences.ALFRESCO_FORMTYPES_USERTASK);
			for (String form : formTypes) {
				if (form.equals(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "formKey"))) {
					userTask = new AlfrescoUserTask();
				}
			}
		}
		if (userTask == null) {
			userTask = new UserTask();
		}

		userTask.setName(xtr.getAttributeValue(null, "name"));
		userTask.setAsynchronous(parseAsync(xtr));
		
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "dueDate"))) {
			userTask.setDueDate(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "dueDate"));
		}

		if (StringUtils.isNotEmpty(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "assignee"))) {
			String assignee = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "assignee");
			userTask.setAssignee(assignee);

		} 
		
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "candidateUsers"))) {
			String expression = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "candidateUsers");
			String[] expressionList = null;
			if (expression.contains(";")) {
				expressionList = expression.split(";");
			} else {
				expressionList = new String[] { expression };
			}
			for (String user : expressionList) {
				userTask.getCandidateUsers().add(user);
			}

		} 
		
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "candidateGroups"))) {
			String expression = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "candidateGroups");
			String[] expressionList = null;
			if (expression.contains(";")) {
				expressionList = expression.split(";");
			} else {
				expressionList = new String[] { expression };
			}
			for (String group : expressionList) {
				userTask.getCandidateGroups().add(group);
			}
		}

		if (StringUtils.isNotEmpty(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "formKey"))) {
			userTask.setFormKey(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "formKey"));
		}

		if (StringUtils.isNotEmpty(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "priority"))) {
			Integer priorityValue = null;
			try {
				priorityValue = Integer.valueOf(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "priority"));
			} catch (Exception e) {
			}
			userTask.setPriority(priorityValue);
		}

		if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "default"))) {
			defaultFlowMap.put(userTask, xtr.getAttributeValue(null, "default"));
		}

		boolean readyWithUserTask = false;
		try {
			String assignmentType = null;
			ActivitiListener listener = null;
			while (readyWithUserTask == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "humanPerformer".equalsIgnoreCase(xtr.getLocalName())) {
					assignmentType = "humanPerformer";

				} else if (xtr.isStartElement() && "potentialOwner".equalsIgnoreCase(xtr.getLocalName())) {
					assignmentType = "potentialOwner";

				} else if (xtr.isStartElement() && "formalExpression".equalsIgnoreCase(xtr.getLocalName())) {
					if ("potentialOwner".equals(assignmentType)) {
						List<String> assignmentList = new ArrayList<String>();
						String assignmentText = xtr.getElementText();
						if (assignmentText.contains(",")) {
							String[] assignmentArray = assignmentText.split(",");
							assignmentList = Arrays.asList(assignmentArray);
						} else {
							assignmentList.add(assignmentText);
						}
						for (String assignmentValue : assignmentList) {
							if (assignmentValue == null)
								continue;
							assignmentValue = assignmentValue.trim();
							if (assignmentValue.length() == 0)
								continue;

							if (assignmentValue.trim().startsWith("user(")) {
								userTask.getCandidateUsers().add(assignmentValue);

							} else {
								userTask.getCandidateGroups().add(assignmentValue);
							}
						}

					} else {
						userTask.setAssignee(xtr.getElementText());
					}

				} else if (xtr.isStartElement() && ("taskListener".equalsIgnoreCase(xtr.getLocalName()))) {

					if (xtr.getAttributeValue(null, "class") != null
					    && "org.alfresco.repo.workflow.activiti.listener.ScriptExecutionListener".equals(xtr.getAttributeValue(null, "class"))
					    || "org.alfresco.repo.workflow.activiti.tasklistener.ScriptTaskListener".equals(xtr.getAttributeValue(null, "class"))) {

						listener = new ActivitiListener();
						listener.setEvent(xtr.getAttributeValue(null, "event"));
						listener.setImplementationType(ALFRESCO_TYPE);
						boolean readyWithAlfrescoType = false;
						while (readyWithAlfrescoType == false && xtr.hasNext()) {
							xtr.next();
							if (xtr.isStartElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
								String script = getFieldExtensionValue(xtr);
								if (script != null && script.length() > 0) {
									listener.setImplementation(script);
								}
								readyWithAlfrescoType = true;
							} else if (xtr.isEndElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
								readyWithAlfrescoType = true;
								readyWithUserTask = true;
							}
						}
					} else {
						listener = parseListener(xtr);
					}
					userTask.getTaskListeners().add(listener);

				} else if (xtr.isStartElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
					listener.getFieldExtensions().add(parseFieldExtension(xtr));
				
				} else if (xtr.isStartElement() && "formProperty".equalsIgnoreCase(xtr.getLocalName())) {
					FormProperty property = new FormProperty();
					userTask.getFormProperties().add(property);
					parseFormProperty(property, xtr);
				
				} else if (xtr.isStartElement() && "documentation".equalsIgnoreCase(xtr.getLocalName())) {
					
					String docText = xtr.getElementText();
					if(StringUtils.isEmpty(docText) == false) {
						userTask.setDocumentation(docText);
					}

				} else if (xtr.isStartElement() && "multiInstanceLoopCharacteristics".equalsIgnoreCase(xtr.getLocalName())) {
					MultiInstanceLoopCharacteristics multiInstanceDef = new MultiInstanceLoopCharacteristics();
					userTask.setLoopCharacteristics(multiInstanceDef);
					parseMultiInstanceDef(multiInstanceDef, xtr);

				} else if (xtr.isEndElement() && "userTask".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithUserTask = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userTask;
	}

	private ScriptTask parseScriptTask(XMLStreamReader xtr) {
		ScriptTask scriptTask = new ScriptTask();
		scriptTask.setName(xtr.getAttributeValue(null, "name"));
		scriptTask.setScriptFormat(xtr.getAttributeValue(null, "scriptFormat"));
		if (xtr.getAttributeValue(null, "default") != null) {
			defaultFlowMap.put(scriptTask, xtr.getAttributeValue(null, "default"));
		}
		scriptTask.setAsynchronous(parseAsync(xtr));
		boolean readyWithScriptTask = false;
		try {
			while (readyWithScriptTask == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "script".equalsIgnoreCase(xtr.getLocalName())) {
					scriptTask.setScript(xtr.getElementText());

				} else if (xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					scriptTask.getExecutionListeners().addAll(parseListeners(xtr));

				} else if (xtr.isStartElement() && "multiInstanceLoopCharacteristics".equalsIgnoreCase(xtr.getLocalName())) {
					MultiInstanceLoopCharacteristics multiInstanceDef = new MultiInstanceLoopCharacteristics();
					scriptTask.setLoopCharacteristics(multiInstanceDef);
					parseMultiInstanceDef(multiInstanceDef, xtr);

				} else if (xtr.isEndElement() && "scriptTask".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithScriptTask = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return scriptTask;
	}

	private MailTask parseMailTask(XMLStreamReader xtr, String taskType) {
		MailTask mailTask = new MailTask();
		mailTask.setName(xtr.getAttributeValue(null, "name"));
		if (xtr.getAttributeValue(null, "default") != null) {
			defaultFlowMap.put(mailTask, xtr.getAttributeValue(null, "default"));
		}
		mailTask.setAsynchronous(parseAsync(xtr));
		boolean readyWithMailTask = false;
		try {
			while (readyWithMailTask == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					fillExtensionsForMailTask(xtr, mailTask);

				} else if (xtr.isStartElement() && "multiInstanceLoopCharacteristics".equalsIgnoreCase(xtr.getLocalName())) {
					MultiInstanceLoopCharacteristics multiInstanceDef = new MultiInstanceLoopCharacteristics();
					mailTask.setLoopCharacteristics(multiInstanceDef);
					parseMultiInstanceDef(multiInstanceDef, xtr);

				} else if (xtr.isEndElement() && taskType.equalsIgnoreCase(xtr.getLocalName())) {
					readyWithMailTask = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mailTask;
	}

	private Task parseAlfrescoScriptTask(XMLStreamReader xtr) {
		String name = xtr.getAttributeValue(null, "name");
		String defaultValue = xtr.getAttributeValue(null, "default");
		boolean async = parseAsync(xtr);
		List<FieldModel> fieldList = new ArrayList<FieldModel>();
		boolean readyWithExtensions = false;
		ActivitiListener listener = null;
		Task task = null;
		MultiInstanceLoopCharacteristics multiInstanceDef = null;
		List<ActivitiListener> listenerList = new ArrayList<ActivitiListener>();
		try {
			while (readyWithExtensions == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
					FieldModel field = parseFieldModel(xtr);
					fieldList.add(field);

				} else if (xtr.isStartElement() && "executionListener".equalsIgnoreCase(xtr.getLocalName())) {
					if (fieldList.size() > 0) {
						task = fillAlfrescoScriptTaskElements(fieldList);
						fieldList = new ArrayList<FieldModel>();
					}
					listener = parseListener(xtr);
					listenerList.add(listener);

				} else if (xtr.isEndElement() && "executionListener".equalsIgnoreCase(xtr.getLocalName())) {
					if (fieldList.size() > 0) {
						fillListenerWithFields(listener, fieldList);
						fieldList = new ArrayList<FieldModel>();
					}

				} else if (xtr.isEndElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					if (fieldList.size() > 0) {
						task = fillAlfrescoScriptTaskElements(fieldList);
					}

				} else if (xtr.isStartElement() && "multiInstanceLoopCharacteristics".equalsIgnoreCase(xtr.getLocalName())) {
					multiInstanceDef = new MultiInstanceLoopCharacteristics();
					parseMultiInstanceDef(multiInstanceDef, xtr);

				} else if (xtr.isEndElement() && "serviceTask".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithExtensions = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (task == null) {
			return null;
		}

		task.setName(name);
		task.setAsynchronous(async);

		if (defaultValue != null) {
			defaultFlowMap.put(task, xtr.getAttributeValue(null, "default"));
		}

		if (multiInstanceDef != null) {
			task.setLoopCharacteristics(multiInstanceDef);
		}

		if (listenerList.size() > 0) {
			task.getExecutionListeners().addAll(listenerList);
		}

		return task;
	}

	private static FieldModel parseFieldModel(XMLStreamReader xtr) {
		FieldModel field = new FieldModel();
		field.name = xtr.getAttributeValue(null, "name");
		field.value = getFieldExtensionValue(xtr);
		return field;
	}

	private Task fillAlfrescoScriptTaskElements(List<FieldModel> fieldList) {
		if (fieldList == null || fieldList.size() == 0)
			return null;
		boolean isMailScript = false;
		String mailScript = null;
		for (FieldModel field : fieldList) {
			if ("script".equalsIgnoreCase(field.name) && isMailScript(field.value)) {
				isMailScript = true;
				mailScript = field.value;
			}
		}
		Task task = null;

		if (isMailScript == true) {
			AlfrescoMailTask mailTask = new AlfrescoMailTask();
			String value = getMailParamValue(mailScript, "mail.parameters.to");
			if (StringUtils.isNotEmpty(value)) {
				mailTask.setTo(value);
			}
			value = getMailParamValue(mailScript, "mail.parameters.to_many");
			if (StringUtils.isNotEmpty(value)) {
				mailTask.setToMany(value);
			}
			value = getMailParamValue(mailScript, "mail.parameters.subject");
			if (StringUtils.isNotEmpty(value)) {
				mailTask.setSubject(value);
			}
			value = getMailParamValue(mailScript, "mail.parameters.from");
			if (StringUtils.isNotEmpty(value)) {
				mailTask.setFrom(value);
			}
			value = getMailParamValue(mailScript, "mail.parameters.template");
			if (StringUtils.isNotEmpty(value)) {
				mailTask.setTemplate(value);
			}
			value = getMailParamValue(mailScript, "mail.parameters.template_model");
			if (StringUtils.isNotEmpty(value)) {
				mailTask.setTemplateModel(value);
			}
			value = getMailParamValue(mailScript, "mail.parameters.text");
			if (StringUtils.isNotEmpty(value)) {
				mailTask.setText(value);
			}
			value = getMailParamValue(mailScript, "mail.parameters.html");
			if (StringUtils.isNotEmpty(value)) {
				mailTask.setHtml(value);
			}
			task = mailTask;

		} else {

			AlfrescoScriptTask scriptTask = new AlfrescoScriptTask();
			for (FieldModel field : fieldList) {
				if ("script".equalsIgnoreCase(field.name)) {
					scriptTask.setScript(field.value);
				} else if ("runAs".equalsIgnoreCase(field.name)) {
					scriptTask.setRunAs(field.value);
				} else if ("scriptProcessor".equalsIgnoreCase(field.name)) {
					scriptTask.setScriptProcessor(field.value);
				}
			}
			task = scriptTask;
		}
		return task;
	}

	private String getMailParamValue(String mailScript, String searchString) {
		int index = mailScript.indexOf(searchString);
		if (index > -1) {
			int startIndex = mailScript.indexOf("=", index);
			int endIndex = mailScript.indexOf(";", index);
			return mailScript.substring(startIndex + 1, endIndex).trim();
		} else {
			return null;
		}
	}

	private boolean isMailScript(String script) {
		boolean isMailScript = false;
		if (script != null) {
			if (script.contains("var mail = actions.create(\"mail\");")
			    && script.contains("mail.execute(bpm_package);")) {

				isMailScript = true;
			}
		}
		return isMailScript;
	}

	private void fillListenerWithFields(ActivitiListener listener,
	    List<FieldModel> fieldList) {
		if (fieldList == null || fieldList.size() == 0)
			return;
		for (FieldModel field : fieldList) {
			FieldExtension extension = new FieldExtension();
			extension.setFieldName(field.name);
			extension.setExpression(field.value);
			listener.getFieldExtensions().add(extension);
		}
	}

	private ServiceTask parseServiceTask(XMLStreamReader xtr) {
		ServiceTask serviceTask = new ServiceTask();
		serviceTask.setName(xtr.getAttributeValue(null, "name"));
		serviceTask.setAsynchronous(parseAsync(xtr));
		if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "class") != null) {
			serviceTask.setImplementationType(CLASS_TYPE);
			serviceTask.setImplementation(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "class"));
			
		} else if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "expression") != null) {
			serviceTask.setImplementationType(EXPRESSION_TYPE);
			serviceTask.setImplementation(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "expression"));
		} else if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "delegateExpression") != null) {
			serviceTask.setImplementationType(DELEGATE_EXPRESSION_TYPE);
			serviceTask.setImplementation(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "delegateExpression"));
		}

		if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "resultVariableName") != null) {
			serviceTask.setResultVariableName(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "resultVariableName"));
		}

		if (xtr.getAttributeValue(null, "default") != null) {
			defaultFlowMap.put(serviceTask, xtr.getAttributeValue(null, "default"));
		}

		boolean readyWithServiceTask = false;
		try {
			while (readyWithServiceTask == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					fillExtensionsForServiceTask(xtr, serviceTask);

				} else if (xtr.isStartElement() && "multiInstanceLoopCharacteristics".equalsIgnoreCase(xtr.getLocalName())) {
					MultiInstanceLoopCharacteristics multiInstanceDef = new MultiInstanceLoopCharacteristics();
					serviceTask.setLoopCharacteristics(multiInstanceDef);
					parseMultiInstanceDef(multiInstanceDef, xtr);
			
				} else if (xtr.isStartElement() && "documentation".equalsIgnoreCase(xtr.getLocalName())) {
						
					String docText = xtr.getElementText();
					if(StringUtils.isEmpty(docText) == false) {
						serviceTask.setDocumentation(docText);
					}

				} else if (xtr.isEndElement() && "serviceTask".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithServiceTask = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceTask;
	}

	private ServiceTask parseTask(XMLStreamReader xtr) {
		ServiceTask serviceTask = new ServiceTask();
		serviceTask.setName(xtr.getAttributeValue(null, "name"));
		serviceTask.setAsynchronous(parseAsync(xtr));
		return serviceTask;
	}

	private static void fillExtensionsForServiceTask(XMLStreamReader xtr, ServiceTask serviceTask) {
		List<FieldExtension> extensionList = new ArrayList<FieldExtension>();
		boolean readyWithExtensions = false;
		try {
			ActivitiListener listener = null;
			while (readyWithExtensions == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
					FieldExtension extension = parseFieldExtension(xtr);
					extensionList.add(extension);

				} else if (xtr.isStartElement() && "executionListener".equalsIgnoreCase(xtr.getLocalName())) {
					if (extensionList.size() > 0) {
						serviceTask.getFieldExtensions().addAll(extensionList);
						extensionList = new ArrayList<FieldExtension>();
					}
					listener = parseListener(xtr);
					serviceTask.getExecutionListeners().add(listener);

				} else if (xtr.isEndElement() && "executionListener".equalsIgnoreCase(xtr.getLocalName())) {
					if (extensionList.size() > 0) {
						listener.getFieldExtensions().addAll(extensionList);
						extensionList = new ArrayList<FieldExtension>();
					}

				} else if (xtr.isEndElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					if (extensionList.size() > 0) {
						serviceTask.getFieldExtensions().addAll(extensionList);
					}
					readyWithExtensions = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void fillExtensionsForMailTask(XMLStreamReader xtr, MailTask mailTask) {
		List<FieldExtension> extensionList = new ArrayList<FieldExtension>();
		boolean readyWithExtensions = false;
		try {
			ActivitiListener listener = null;
			while (readyWithExtensions == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
					String name = xtr.getAttributeValue(null, "name");
					if ("to".equalsIgnoreCase(name)) {
						mailTask.setTo(getFieldExtensionValue(xtr));
					} else if ("from".equalsIgnoreCase(name)) {
						mailTask.setFrom(getFieldExtensionValue(xtr));
					} else if ("cc".equalsIgnoreCase(name)) {
						mailTask.setCc(getFieldExtensionValue(xtr));
					} else if ("bcc".equalsIgnoreCase(name)) {
						mailTask.setBcc(getFieldExtensionValue(xtr));
					} else if ("charset".equalsIgnoreCase(name)) {
						mailTask.setCharset(getFieldExtensionValue(xtr));
					} else if ("subject".equalsIgnoreCase(name)) {
						mailTask.setSubject(getFieldExtensionValue(xtr));
					} else if ("html".equalsIgnoreCase(name)) {
						mailTask.setHtml(getFieldExtensionValue(xtr));
					} else if ("text".equalsIgnoreCase(name)) {
						mailTask.setText(getFieldExtensionValue(xtr));
					}
				} else if(xtr.isStartElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
          FieldExtension extension = parseFieldExtension(xtr);
          extensionList.add(extension);
          
        } else if(xtr.isStartElement() && "executionListener".equalsIgnoreCase(xtr.getLocalName())) {
          listener = parseListener(xtr);
          mailTask.getExecutionListeners().add(listener);
          
        } else if(xtr.isEndElement() && "executionListener".equalsIgnoreCase(xtr.getLocalName())) {
          if(extensionList.size() > 0) {
            listener.getFieldExtensions().addAll(extensionList);
            extensionList = new ArrayList<FieldExtension>();
          } 
				} else if (xtr.isEndElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithExtensions = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void fillExtensionsForCallActivity(XMLStreamReader xtr, CallActivity callActivity) {
    List<FieldExtension> extensionList = new ArrayList<FieldExtension>();
    boolean readyWithExtensions = false;
    try {
      ActivitiListener listener = null;
      while(readyWithExtensions == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
          FieldExtension extension = parseFieldExtension(xtr);
          extensionList.add(extension);
          
        } else if(xtr.isStartElement() && "executionListener".equalsIgnoreCase(xtr.getLocalName())) {
          listener = parseListener(xtr);
          callActivity.getExecutionListeners().add(listener);
          
        } else if(xtr.isEndElement() && "executionListener".equalsIgnoreCase(xtr.getLocalName())) {
          if(extensionList.size() > 0) {
            listener.getFieldExtensions().addAll(extensionList);
            extensionList = new ArrayList<FieldExtension>();
          }
        
        } else if(xtr.isStartElement() && "in".equalsIgnoreCase(xtr.getLocalName())) {
        	String source = xtr.getAttributeValue(null, "source");
        	String sourceExpression = xtr.getAttributeValue(null, "sourceExpression");
        	String target = xtr.getAttributeValue(null, "target");
        	String targetExpression = xtr.getAttributeValue(null, "targetExpression");
        	if((StringUtils.isNotEmpty(source) || StringUtils.isNotEmpty(sourceExpression)) && 
        			(StringUtils.isNotEmpty(target) || StringUtils.isNotEmpty(targetExpression))) {
        		
	        	IOParameter parameter = new IOParameter();
	        	if(StringUtils.isNotEmpty(sourceExpression)) {
	        		parameter.setSourceExpression(sourceExpression);
	        	} else {
	        		parameter.setSource(source);
	        	}
	        	
	        	if(StringUtils.isNotEmpty(targetExpression)) {
	        		parameter.setTargetExpression(targetExpression);
	        	} else {
	        		parameter.setTarget(target);
	        	}
	          callActivity.getInParameters().add(parameter);
        	}
        
        } else if(xtr.isStartElement() && "out".equalsIgnoreCase(xtr.getLocalName())) {
        	String source = xtr.getAttributeValue(null, "source");
        	String sourceExpression = xtr.getAttributeValue(null, "sourceExpression");
        	String target = xtr.getAttributeValue(null, "target");
        	String targetExpression = xtr.getAttributeValue(null, "targetExpression");
        	if((StringUtils.isNotEmpty(source) || StringUtils.isNotEmpty(sourceExpression)) && 
        			(StringUtils.isNotEmpty(target) || StringUtils.isNotEmpty(targetExpression))) {
        		
	        	IOParameter parameter = new IOParameter();
	        	if(StringUtils.isNotEmpty(sourceExpression)) {
	        		parameter.setSourceExpression(sourceExpression);
	        	} else {
	        		parameter.setSource(source);
	        	}
	        	
	        	if(StringUtils.isNotEmpty(targetExpression)) {
	        		parameter.setTargetExpression(targetExpression);
	        	} else {
	        		parameter.setTarget(target);
	        	}
	          callActivity.getOutParameters().add(parameter);
        	}  
          
        } else if(xtr.isEndElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithExtensions = true;
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

	private static FieldExtension parseFieldExtension(XMLStreamReader xtr) {
		FieldExtension extension = new FieldExtension();
		extension.setFieldName(xtr.getAttributeValue(null, "name"));
		extension.setExpression(getFieldExtensionValue(xtr));
		return extension;
	}

	private static String getFieldExtensionValue(XMLStreamReader xtr) {
		if (xtr.getAttributeValue(null, "stringValue") != null) {
			return xtr.getAttributeValue(null, "stringValue");

		} else if (xtr.getAttributeValue(null, "expression") != null) {
			return xtr.getAttributeValue(null, "expression");

		} else {
			boolean readyWithFieldExtension = false;
			try {
				while (readyWithFieldExtension == false && xtr.hasNext()) {
					xtr.next();
					if (xtr.isStartElement() && "string".equalsIgnoreCase(xtr.getLocalName())) {
						return xtr.getElementText().trim();

					} else if (xtr.isStartElement() && "expression".equalsIgnoreCase(xtr.getLocalName())) {
						return xtr.getElementText().trim();

					} else if (xtr.isEndElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
						return null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static List<ActivitiListener> parseListeners(XMLStreamReader xtr) {
		List<ActivitiListener> listenerList = new ArrayList<ActivitiListener>();
		boolean readyWithListener = false;
		try {
			ActivitiListener listener = null;
			while (readyWithListener == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement()
				    && ("executionListener".equalsIgnoreCase(xtr.getLocalName()) || "taskListener".equalsIgnoreCase(xtr.getLocalName()))) {

					if (xtr.getAttributeValue(null, "class") != null
					    && "org.alfresco.repo.workflow.activiti.listener.ScriptExecutionListener".equals(xtr.getAttributeValue(null, "class"))
					    || "org.alfresco.repo.workflow.activiti.tasklistener.ScriptTaskListener".equals(xtr.getAttributeValue(null, "class"))) {

						listener = new ActivitiListener();
						listener.setEvent(xtr.getAttributeValue(null, "event"));
						listener.setImplementationType(ALFRESCO_TYPE);
						boolean readyWithAlfrescoType = false;
						while (readyWithAlfrescoType == false && xtr.hasNext()) {
							xtr.next();
							if (xtr.isStartElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
								String script = getFieldExtensionValue(xtr);
								if (script != null && script.length() > 0) {
									listener.setImplementation(script);
								}
								readyWithAlfrescoType = true;
							} else if (xtr.isEndElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
								readyWithAlfrescoType = true;
								readyWithListener = true;
							}
						}
					} else {
						listener = parseListener(xtr);
					}
					listenerList.add(listener);

				} else if (xtr.isStartElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
					listener.getFieldExtensions().add(parseFieldExtension(xtr));
				} else if (xtr.isEndElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithListener = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listenerList;
	}

	private static ActivitiListener parseListener(XMLStreamReader xtr) {
		ActivitiListener listener = new ActivitiListener();
		if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "class"))) {
			listener.setImplementation(xtr.getAttributeValue(null, "class"));
			listener.setImplementationType(CLASS_TYPE);
		} else if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "expression"))) {
			listener.setImplementation(xtr.getAttributeValue(null, "expression"));
			listener.setImplementationType(EXPRESSION_TYPE);
		} else if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "delegateExpression"))) {
			listener.setImplementation(xtr.getAttributeValue(null, "delegateExpression"));
			listener.setImplementationType(DELEGATE_EXPRESSION_TYPE);
		}
		listener.setEvent(xtr.getAttributeValue(null, "event"));
		return listener;
	}

	private ManualTask parseManualTask(XMLStreamReader xtr) {
		ManualTask manualTask = new ManualTask();
		manualTask.setName(xtr.getAttributeValue(null, "name"));
		manualTask.setAsynchronous(parseAsync(xtr));
		if (xtr.getAttributeValue(null, "default") != null) {
			defaultFlowMap.put(manualTask, xtr.getAttributeValue(null, "default"));
		}
		boolean readyWithTask = false;
		try {
			while (readyWithTask == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					manualTask.getExecutionListeners().addAll(parseListeners(xtr));

				} else if (xtr.isStartElement() && "multiInstanceLoopCharacteristics".equalsIgnoreCase(xtr.getLocalName())) {
					MultiInstanceLoopCharacteristics multiInstanceDef = new MultiInstanceLoopCharacteristics();
					manualTask.setLoopCharacteristics(multiInstanceDef);
					parseMultiInstanceDef(multiInstanceDef, xtr);

				} else if (xtr.isEndElement() && "manualTask".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithTask = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return manualTask;
	}

	private CallActivity parseCallActivity(XMLStreamReader xtr) {
		CallActivity callActivity = new CallActivity();
		callActivity.setName(xtr.getAttributeValue(null, "name"));
		callActivity.setAsynchronous(parseAsync(xtr));
		if (xtr.getAttributeValue(null, "calledElement") != null && xtr.getAttributeValue(null, "calledElement").length() > 0) {
			callActivity.setCalledElement(xtr.getAttributeValue(null, "calledElement"));
		}
		if (xtr.getAttributeValue(null, "default") != null) {
			defaultFlowMap.put(callActivity, xtr.getAttributeValue(null, "default"));
		}
		boolean readyWithTask = false;
		try {
			while (readyWithTask == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					
					fillExtensionsForCallActivity(xtr, callActivity);

				} else if (xtr.isStartElement() && "multiInstanceLoopCharacteristics".equalsIgnoreCase(xtr.getLocalName())) {
					MultiInstanceLoopCharacteristics multiInstanceDef = new MultiInstanceLoopCharacteristics();
					callActivity.setLoopCharacteristics(multiInstanceDef);
					parseMultiInstanceDef(multiInstanceDef, xtr);

				} else if (xtr.isEndElement() && "callActivity".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithTask = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return callActivity;
	}

	private ReceiveTask parseReceiveTask(XMLStreamReader xtr) {
		ReceiveTask receiveTask = new ReceiveTask();
		receiveTask.setName(xtr.getAttributeValue(null, "name"));
		receiveTask.setAsynchronous(parseAsync(xtr));
		if (xtr.getAttributeValue(null, "default") != null) {
			defaultFlowMap.put(receiveTask, xtr.getAttributeValue(null, "default"));
		}
		boolean readyWithTask = false;
		try {
			while (readyWithTask == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					receiveTask.getExecutionListeners().addAll(parseListeners(xtr));

				} else if (xtr.isStartElement() && "multiInstanceLoopCharacteristics".equalsIgnoreCase(xtr.getLocalName())) {
					MultiInstanceLoopCharacteristics multiInstanceDef = new MultiInstanceLoopCharacteristics();
					receiveTask.setLoopCharacteristics(multiInstanceDef);
					parseMultiInstanceDef(multiInstanceDef, xtr);

				} else if (xtr.isEndElement() && "receiveTask".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithTask = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return receiveTask;
	}

	private BusinessRuleTask parseBusinessRuleTask(XMLStreamReader xtr) {
		BusinessRuleTask businessRuleTask = new BusinessRuleTask();
		businessRuleTask.setName(xtr.getAttributeValue(null, "name"));
		businessRuleTask.setAsynchronous(parseAsync(xtr));
		if (xtr.getAttributeValue(null, "default") != null) {
			defaultFlowMap.put(businessRuleTask, xtr.getAttributeValue(null, "default"));
		}
		if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "rules") != null) {
			String ruleNames = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "rules");
			if (ruleNames != null && ruleNames.length() > 0) {
				businessRuleTask.getRuleNames().clear();
				if (ruleNames.contains(",") == false) {
					businessRuleTask.getRuleNames().add(ruleNames);
				} else {
					String[] ruleNameList = ruleNames.split(",");
					for (String rule : ruleNameList) {
						businessRuleTask.getRuleNames().add(rule);
					}
				}
			}
		}
		if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "inputVariableNames") != null) {
			String inputNames = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "inputVariableNames");
			if (inputNames != null && inputNames.length() > 0) {
				businessRuleTask.getInputVariables().clear();
				if (inputNames.contains(",") == false) {
					businessRuleTask.getInputVariables().add(inputNames);
				} else {
					String[] inputNamesList = inputNames.split(",");
					for (String input : inputNamesList) {
						businessRuleTask.getInputVariables().add(input);
					}
				}
			}
		}
		if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "exclude") != null) {
			businessRuleTask.setExclude(Boolean.valueOf(xtr.getAttributeValue(
			    ACTIVITI_EXTENSIONS_NAMESPACE, "exclude")));
		}
		if (xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "resultVariableName") != null) {
			businessRuleTask.setResultVariableName(xtr.getAttributeValue(
			    ACTIVITI_EXTENSIONS_NAMESPACE, "resultVariableName"));
		}

		boolean readyWithTask = false;
		try {
			while (readyWithTask == false && xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
					businessRuleTask.getExecutionListeners().addAll(parseListeners(xtr));

				} else if (xtr.isStartElement() && "multiInstanceLoopCharacteristics".equalsIgnoreCase(xtr.getLocalName())) {
					MultiInstanceLoopCharacteristics multiInstanceDef = new MultiInstanceLoopCharacteristics();
					businessRuleTask.setLoopCharacteristics(multiInstanceDef);
					parseMultiInstanceDef(multiInstanceDef, xtr);

				} else if (xtr.isEndElement() && "businessRuleTask".equalsIgnoreCase(xtr.getLocalName())) {
					readyWithTask = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return businessRuleTask;
	}

	private BoundaryEventModel parseBoundaryEvent(XMLStreamReader xtr) {
		BoundaryEvent boundaryEvent = new BoundaryEvent();
		boundaryEvent.setName(xtr.getAttributeValue(null, "name"));
		
		if(xtr.getAttributeValue(null, "cancelActivity") != null) {
			String cancelActivity = xtr.getAttributeValue(null, "cancelActivity");
			if("true".equalsIgnoreCase(cancelActivity)) {
				boundaryEvent.setCancelActivity(true);
			} else {
				boundaryEvent.setCancelActivity(false);
			}
		}
		
		BoundaryEventModel model = new BoundaryEventModel();
		model.boundaryEvent = boundaryEvent;
		model.attachedRef = xtr.getAttributeValue(null, "attachedToRef");
		EventDefinition eventDefinition = null;
		try {
			while (xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "timerEventDefinition".equalsIgnoreCase(xtr.getLocalName())) {
					model.type = BoundaryEventModel.TIMEEVENT;
					eventDefinition = new TimerEventDefinition();
					boundaryEvent.getEventDefinitions().add(eventDefinition);
				} else if (xtr.isStartElement() && "errorEventDefinition".equalsIgnoreCase(xtr.getLocalName())) {
					model.type = BoundaryEventModel.ERROREVENT;
					eventDefinition = new ErrorEventDefinition();
					boundaryEvent.getEventDefinitions().add(eventDefinition);
					if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "errorRef"))) {
						((ErrorEventDefinition) eventDefinition).setErrorCode(xtr.getAttributeValue(null, "errorRef"));
					}
					break;
					
				} else if (xtr.isStartElement() && "signalEventDefinition".equalsIgnoreCase(xtr.getLocalName())) {
					model.type = BoundaryEventModel.SIGNALEVENT;
					eventDefinition = new SignalEventDefinition();
					boundaryEvent.getEventDefinitions().add(eventDefinition);
					if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "signalRef"))) {
						((SignalEventDefinition) eventDefinition).setSignalRef(xtr.getAttributeValue(null, "signalRef"));
					}
					break;

				} else if (xtr.isStartElement() && "timeDuration".equalsIgnoreCase(xtr.getLocalName())) {
					((TimerEventDefinition) eventDefinition).setTimeDuration(xtr.getElementText());
					break;

				} else if (xtr.isStartElement() && "timeDate".equalsIgnoreCase(xtr.getLocalName())) {
					((TimerEventDefinition) eventDefinition).setTimeDate(xtr.getElementText());
					break;

				} else if (xtr.isStartElement() && "timeCycle".equalsIgnoreCase(xtr.getLocalName())) {
					((TimerEventDefinition) eventDefinition).setTimeCycle(xtr.getElementText());
					break;

				} else if (xtr.isEndElement() && "boundaryEvent".equalsIgnoreCase(xtr.getLocalName())) {
					break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}
	
	private boolean parseAsync(XMLStreamReader xtr) {
		boolean async = false;
		String asyncString = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "async");
		if ("true".equalsIgnoreCase(asyncString)) {
			async = true;
		}
		return async;
	}
	
	private IntermediateCatchEvent parseIntermediateCatchEvent(XMLStreamReader xtr) {
		IntermediateCatchEvent catchEvent = new IntermediateCatchEvent();
		catchEvent.setName(xtr.getAttributeValue(null, "name"));
		
		try {
			while (xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "timeDuration".equalsIgnoreCase(xtr.getLocalName())) {
					TimerEventDefinition eventDef = new TimerEventDefinition();
					eventDef.setTimeDuration(xtr.getElementText());
					catchEvent.getEventDefinitions().add(eventDef);
					break;

				} else if (xtr.isStartElement() && "timeDate".equalsIgnoreCase(xtr.getLocalName())) {
					TimerEventDefinition eventDef = new TimerEventDefinition();
					eventDef.setTimeDate(xtr.getElementText());
					catchEvent.getEventDefinitions().add(eventDef);
					break;

				} else if (xtr.isStartElement() && "timeCycle".equalsIgnoreCase(xtr.getLocalName())) {
					TimerEventDefinition eventDef = new TimerEventDefinition();
					eventDef.setTimeCycle(xtr.getElementText());
					catchEvent.getEventDefinitions().add(eventDef);
					break;
					
				} else if (xtr.isStartElement() && "signalEventDefinition".equalsIgnoreCase(xtr.getLocalName())) {
					SignalEventDefinition eventDefinition = new SignalEventDefinition();
					if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "signalRef"))) {
						eventDefinition.setSignalRef(xtr.getAttributeValue(null, "signalRef"));
					}
					catchEvent.getEventDefinitions().add(eventDefinition);
					break;

				} else if (xtr.isEndElement() && "intermediateCatchEvent".equalsIgnoreCase(xtr.getLocalName())) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return catchEvent;
	}
	
	private ThrowEvent parseIntermediateThrowEvent(XMLStreamReader xtr) {
		ThrowEvent throwEvent = new ThrowSignalEvent();
		throwEvent.setName(xtr.getAttributeValue(null, "name"));
		
		try {
			while (xtr.hasNext()) {
				xtr.next();
				if (xtr.isStartElement() && "signalEventDefinition".equalsIgnoreCase(xtr.getLocalName())) {
					SignalEventDefinition eventDefinition = new SignalEventDefinition();
					if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, "signalRef"))) {
						eventDefinition.setSignalRef(xtr.getAttributeValue(null, "signalRef"));
					}
					throwEvent.getEventDefinitions().add(eventDefinition);
					break;

				} else if (xtr.isEndElement() && "intermediateThrowEvent".equalsIgnoreCase(xtr.getLocalName())) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return throwEvent;
	}
}
