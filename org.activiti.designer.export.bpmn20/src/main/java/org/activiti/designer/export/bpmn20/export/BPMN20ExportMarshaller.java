/**
 * 
 */
package org.activiti.designer.export.bpmn20.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.BusinessRuleTask;
import org.activiti.designer.bpmn2.model.CallActivity;
import org.activiti.designer.bpmn2.model.EndEvent;
import org.activiti.designer.bpmn2.model.ErrorEventDefinition;
import org.activiti.designer.bpmn2.model.EventSubProcess;
import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.InclusiveGateway;
import org.activiti.designer.bpmn2.model.IntermediateCatchEvent;
import org.activiti.designer.bpmn2.model.MailTask;
import org.activiti.designer.bpmn2.model.ManualTask;
import org.activiti.designer.bpmn2.model.ParallelGateway;
import org.activiti.designer.bpmn2.model.ReceiveTask;
import org.activiti.designer.bpmn2.model.ScriptTask;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.bpmn2.model.Signal;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.ThrowEvent;
import org.activiti.designer.bpmn2.model.TimerEventDefinition;
import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoScriptTask;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 2
 * 
 */
public class BPMN20ExportMarshaller implements ActivitiNamespaceConstants {

  private Bpmn2MemoryModel model;
  private String modelFileName;
  private IFeatureProvider featureProvider;
  private IndentingXMLStreamWriter xtw;

  public void marshallDiagram(Bpmn2MemoryModel model, String modelFileName, IFeatureProvider featureProvider) {
    this.model = model;
    this.modelFileName = modelFileName;
    this.featureProvider = featureProvider;
    marshallBPMNDiagram();
  }

  private void marshallBPMNDiagram() {
    try {

    	File objectsFile = new File(modelFileName);
			
			FileOutputStream fos = new FileOutputStream(objectsFile);
    	
      XMLOutputFactory xof = XMLOutputFactory.newInstance();
      OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");

      XMLStreamWriter writer = xof.createXMLStreamWriter(out);
      xtw = new IndentingXMLStreamWriter(writer);

      xtw.writeStartDocument("UTF-8", "1.0");

      // start definitions root element
      xtw.writeStartElement("definitions");
      xtw.setDefaultNamespace(BPMN2_NAMESPACE);
      xtw.writeDefaultNamespace(BPMN2_NAMESPACE);
      xtw.writeNamespace("xsi", XSI_NAMESPACE);
      xtw.writeNamespace(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeNamespace(BPMNDI_PREFIX, BPMNDI_NAMESPACE);
      xtw.writeNamespace(OMGDC_PREFIX, OMGDC_NAMESPACE);
      xtw.writeNamespace(OMGDI_PREFIX, OMGDI_NAMESPACE);
      xtw.writeAttribute("typeLanguage", SCHEMA_NAMESPACE);
      xtw.writeAttribute("expressionLanguage", XPATH_NAMESPACE);
      if (model.getProcess() != null && StringUtils.isNotEmpty(model.getProcess().getNamespace())) {
        xtw.writeAttribute("targetNamespace", model.getProcess().getNamespace());
      } else {
        xtw.writeAttribute("targetNamespace", PROCESS_NAMESPACE);
      }
      
      if(model.getProcess().getSignals().size() > 0) {
      	for (Signal signal : model.getProcess().getSignals()) {
          xtw.writeStartElement("signal");
          xtw.writeAttribute("id", signal.getId());
          xtw.writeAttribute("name", signal.getName());
          xtw.writeEndElement();
        }
      }

      // start process element
      xtw.writeStartElement("process");
      xtw.writeAttribute("id", model.getProcess().getId());
      xtw.writeAttribute("name", model.getProcess().getName());
      if (StringUtils.isNotEmpty(model.getProcess().getDocumentation())) {

        xtw.writeStartElement("documentation");
        xtw.writeCharacters(model.getProcess().getDocumentation());
        xtw.writeEndElement();
      }
      ExecutionListenerExport.createExecutionListenerXML(model.getProcess().getExecutionListeners(), true, xtw);
      
      for (FlowElement flowElement : model.getProcess().getFlowElements()) {
      	PictogramElement picElement = featureProvider.getPictogramElementForBusinessObject(flowElement);
      	if(picElement == null) continue;
      	
	      createXML(flowElement);
      }

      // end process element
      xtw.writeEndElement();

      BpmnDIExport.createDIXML(model.getProcess(), featureProvider, xtw);

      // end definitions root element
      xtw.writeEndElement();
      xtw.writeEndDocument();

      xtw.flush();

      fos.close();

      xtw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void createXML(FlowElement object) throws Exception {
    if (object instanceof StartEvent) {
      StartEvent startEvent = (StartEvent) object;
      // start StartEvent element
      xtw.writeStartElement("startEvent");
      xtw.writeAttribute("id", startEvent.getId());
      xtw.writeAttribute("name", startEvent.getName());

      if (startEvent.getFormKey() != null && startEvent.getFormKey().length() > 0) {
        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "formKey", startEvent.getFormKey());
      }

      if (startEvent.getInitiator() != null && startEvent.getInitiator().length() > 0) {
        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "initiator", startEvent.getInitiator());
      }

      if (startEvent.getEventDefinitions().size() > 0 && startEvent.getEventDefinitions().get(0) instanceof TimerEventDefinition) {

        TimerEventDefinition timerDef = (TimerEventDefinition) startEvent.getEventDefinitions().get(0);

        xtw.writeStartElement("timerEventDefinition");

        if (StringUtils.isNotEmpty(timerDef.getTimeDuration())) {

          xtw.writeStartElement("timeDuration");
          xtw.writeCharacters(timerDef.getTimeDuration());
          xtw.writeEndElement();

        } else if (timerDef.getTimeDate() != null) {

          xtw.writeStartElement("timeDate");
          xtw.writeCharacters(timerDef.getTimeDate().toString());
          xtw.writeEndElement();

        } else if (StringUtils.isNotEmpty(timerDef.getTimeCycle())) {

          xtw.writeStartElement("timeCycle");
          xtw.writeCharacters(timerDef.getTimeCycle());
          xtw.writeEndElement();
        }

        xtw.writeEndElement();
      }
      
      if (startEvent.getEventDefinitions().size() > 0 && startEvent.getEventDefinitions().get(0) instanceof ErrorEventDefinition) {

      	ErrorEventDefinition errorDef = (ErrorEventDefinition) startEvent.getEventDefinitions().get(0);

        xtw.writeStartElement("errorEventDefinition");

        if(StringUtils.isNotEmpty(errorDef.getErrorCode())) {
        	xtw.writeAttribute("errorRef", errorDef.getErrorCode());
        }

        xtw.writeEndElement();
      }

      if (startEvent.getFormProperties().size() > 0) {
        xtw.writeStartElement("extensionElements");
        FormPropertiesExport.createFormPropertiesXML(startEvent.getFormProperties(), xtw);
        xtw.writeEndElement();
      }

      // end StartEvent element
      xtw.writeEndElement();

    } else if (object instanceof EndEvent) {
      EndEvent endEvent = (EndEvent) object;
      // start EndEvent element
      xtw.writeStartElement("endEvent");
      xtw.writeAttribute("id", endEvent.getId());
      xtw.writeAttribute("name", endEvent.getName());

      if (endEvent.getEventDefinitions().size() > 0) {
        ErrorEventDefinition errorDef = (ErrorEventDefinition) endEvent.getEventDefinitions().get(0);
        xtw.writeStartElement("errorEventDefinition");
        if(StringUtils.isNotEmpty(errorDef.getErrorCode())) {
        	xtw.writeAttribute("errorRef", errorDef.getErrorCode());
        }
        xtw.writeEndElement();
      }

      // end EndEvent element
      xtw.writeEndElement();

    } else if (object instanceof SequenceFlow) {
      SequenceFlowExport.createSequenceFlow(object, xtw);

    } else if (object instanceof UserTask) {
      UserTaskExport.createUserTask(object, xtw);

    } else if (object instanceof ScriptTask) {
      ScriptTaskExport.createScriptTask(object, xtw);

    } else if (object instanceof ServiceTask) {
      ServiceTaskExport.createServiceTask(object, xtw);
    
    } else if (object instanceof AlfrescoScriptTask) {
      AlfrescoScriptTaskExport.createScriptTask(object, xtw);

    } else if (object instanceof MailTask) {
      MailTaskExport.createMailTask(object, xtw);

    } else if (object instanceof ManualTask) {
      ManualTaskExport.createManualTask(object, xtw);

    } else if (object instanceof ReceiveTask) {
      ReceiveTaskExport.createReceiveTask(object, xtw);

    } else if (object instanceof BusinessRuleTask) {
      BusinessRuleTaskExport.createBusinessRuleTask(object, xtw);

    } else if (object instanceof CallActivity) {
      CallActivityExport.createCallActivity(object, xtw);

    } else if (object instanceof ParallelGateway) {
      ParallelGateway parallelGateway = (ParallelGateway) object;
      // start ParallelGateway element
      xtw.writeStartElement("parallelGateway");
      xtw.writeAttribute("id", parallelGateway.getId());
      if (parallelGateway.getName() != null) {
        xtw.writeAttribute("name", parallelGateway.getName());
      }

      // end ParallelGateway element
      xtw.writeEndElement();

    } else if (object instanceof ExclusiveGateway) {
      ExclusiveGateway exclusiveGateway = (ExclusiveGateway) object;
      // start ExclusiveGateway element
      xtw.writeStartElement("exclusiveGateway");
      xtw.writeAttribute("id", exclusiveGateway.getId());
      if (exclusiveGateway.getName() != null) {
        xtw.writeAttribute("name", exclusiveGateway.getName());
      }
      DefaultFlowExport.createDefaultFlow(object, xtw);

      // end ExclusiveGateway element
      xtw.writeEndElement();
      
    } else if (object instanceof InclusiveGateway) {
      InclusiveGateway inclusiveGateway = (InclusiveGateway) object;
      // start InclusiveGateway element
      xtw.writeStartElement("inclusiveGateway");
      xtw.writeAttribute("id", inclusiveGateway.getId());
      if (inclusiveGateway.getName() != null) {
        xtw.writeAttribute("name", inclusiveGateway.getName());
      }
      DefaultFlowExport.createDefaultFlow(object, xtw);

      // end InclusiveGateway element
      xtw.writeEndElement();
    
    } else if (object instanceof IntermediateCatchEvent) {
    	IntermediateCatchEventExport.createIntermediateEvent(object, xtw);
    
    } else if (object instanceof ThrowEvent) {
    	IntermediateThrowEventExport.createIntermediateEvent(object, xtw);

    } else if (object instanceof SubProcess) {
      SubProcess subProcess = (SubProcess) object;
      
      // start SubProcess element
      xtw.writeStartElement("subProcess");
      xtw.writeAttribute("id", subProcess.getId());
      if (subProcess.getName() != null) {
        xtw.writeAttribute("name", subProcess.getName());
      }
      
      if (subProcess instanceof EventSubProcess) {
      	xtw.writeAttribute("triggeredByEvent", "true");
      }
      
      DefaultFlowExport.createDefaultFlow(object, xtw);
      AsyncActivityExport.createAsyncAttribute(object, xtw);

      ExecutionListenerExport.createExecutionListenerXML(subProcess.getExecutionListeners(), true, xtw);
      MultiInstanceExport.createMultiInstance(object, xtw);

      List<FlowElement> flowElementList = subProcess.getFlowElements();
      for (FlowElement flowElement : flowElementList) {
        createXML(flowElement);
      }

      // end SubProcess element
      xtw.writeEndElement();
    }
    
    if (object instanceof Activity) {
    	Activity activity = (Activity) object;
    	for(BoundaryEvent event : activity.getBoundaryEvents()) {
    		BoundaryEventExport.createBoundaryEvent(event, xtw);
    	}
    }
  }

}
