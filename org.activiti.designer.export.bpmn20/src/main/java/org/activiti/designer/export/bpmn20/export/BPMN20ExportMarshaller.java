/**
 * 
 */
package org.activiti.designer.export.bpmn20.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.extension.export.AbstractExportMarshaller;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.bpmn2.AlfrescoScriptTask;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.MailTask;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 2
 * 
 */
public class BPMN20ExportMarshaller extends AbstractExportMarshaller implements ActivitiNamespaceConstants {

  private static final String FILENAME_PATTERN = ExportMarshaller.PLACEHOLDER_ORIGINAL_FILENAME_WITHOUT_EXTENSION + ".bpmn20.xml";

  private static final int WORK_VALIDATION = 40;
  private static final int WORK_EXPORT = 60;
  private static final int WORK_TOTAL = WORK_VALIDATION + WORK_EXPORT;

  private IProgressMonitor monitor;
  private Diagram diagram;
  private IndentingXMLStreamWriter xtw;

  /**
	 * 
	 */
  public BPMN20ExportMarshaller() {
  }

  @Override
  public String getMarshallerName() {
    return ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_NAME;
  }

  @Override
  public String getFormatName() {
    return "Activiti BPMN 2.0";
  }

  @Override
  public void marshallDiagram(Diagram diagram, IProgressMonitor monitor) {
    this.monitor = monitor;
    this.diagram = diagram;

    monitor.beginTask("Exporting to BPMN 2.0", WORK_TOTAL);

    // Clear problems for this diagram first
    clearMarkers(getResource(diagram.eResource().getURI()));

    boolean performMarshalling = true;

    // Validate if the BPMN validator is checked in the preferences
    if (PreferencesUtil.getBooleanPreference(Preferences.VALIDATE_ACTIVITI_BPMN_FORMAT)) {

      boolean validBpmn = invokeValidator(ActivitiBPMNDiagramConstants.BPMN_VALIDATOR_ID, diagram, new SubProgressMonitor(monitor, WORK_VALIDATION));

      if (!validBpmn) {

        // Get the behavior required
        final String behavior = PreferencesUtil.getStringPreference(Preferences.SKIP_BPMN_MARSHALLER_ON_VALIDATION_FAILURE);

        // Flag marshalling to be skipped if the behavior is to skip or not
        // defined (mirrors default behavior)
        if (behavior == null || ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_VALIDATION_SKIP.equals(behavior)) {
          performMarshalling = false;
          // add additional marker for user
          addProblemToDiagram(diagram, "Marshalling to " + getFormatName() + " format was skipped because validation of the diagram failed.", null);
        }
      }
    } else {
      monitor.worked(WORK_VALIDATION);
    }

    if (performMarshalling) {
      marshallBPMNDiagram();
      monitor.worked(WORK_EXPORT);
    }
    monitor.done();
  }

  private void marshallBPMNDiagram() {
    try {

      XMLOutputFactory xof = XMLOutputFactory.newInstance();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamWriter out = new OutputStreamWriter(baos, "UTF-8");

      XMLStreamWriter writer = xof.createXMLStreamWriter(out);
      xtw = new IndentingXMLStreamWriter(writer);

      final EList<EObject> contents = diagram.eResource().getContents();

      Process process = null;

      for (final EObject eObject : contents) {
        if (eObject instanceof Process) {
          process = (Process) eObject;
          break;
        }
      }

      if (process == null) {
        addProblemToDiagram(diagram, "Process cannot be null", null);
      }

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
      xtw.writeAttribute("targetNamespace", PROCESS_NAMESPACE);

      // start process element
      xtw.writeStartElement("process");
      xtw.writeAttribute("id", process.getId());
      xtw.writeAttribute("name", process.getName());
      ExtensionListenerExport.createExtensionListenerXML(process.getExecutionListeners(), true, EXECUTION_LISTENER, xtw);
      if (process.getDocumentation() != null && process.getDocumentation().size() > 0 && process.getDocumentation().get(0) != null
              && process.getDocumentation().get(0).getText() != null && process.getDocumentation().get(0).getText().length() > 0) {

        xtw.writeStartElement("documentation");
        xtw.writeCharacters(process.getDocumentation().get(0).getText());
        xtw.writeEndElement();
      }

      for (EObject object : contents) {
        createXML(object, "");
      }

      // end process element
      xtw.writeEndElement();

      BpmnDIExport.createDIXML(process, diagram, xtw);

      // end definitions root element
      xtw.writeEndElement();
      xtw.writeEndDocument();

      xtw.flush();

      final byte[] bytes = baos.toByteArray();
      final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      saveResource(getRelativeURIForDiagram(diagram, FILENAME_PATTERN), bais, new NullProgressMonitor());

      xtw.close();
    } catch (Exception e) {
      e.printStackTrace();
      addProblemToDiagram(diagram, "An exception occurred while creating the BPMN 2.0 XML: " + e.getMessage(), null);
    }
  }

  private void createXML(EObject object, String subProcessId) throws Exception {
    if (object instanceof StartEvent) {
      StartEvent startEvent = (StartEvent) object;
      // start StartEvent element
      xtw.writeStartElement("startEvent");
      xtw.writeAttribute("id", subProcessId + startEvent.getId());
      xtw.writeAttribute("name", startEvent.getName());

      if(startEvent.getFormKey() != null && startEvent.getFormKey().length() > 0) {
        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "formKey", startEvent.getFormKey());
      }
      
      if(startEvent.getInitiator() != null && startEvent.getInitiator().length() > 0) {
        xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "initiator", startEvent.getInitiator());
      }
      
      if(startEvent.getEventDefinitions().size() > 0) {
      	
      	TimerEventDefinition timerDef = (TimerEventDefinition) startEvent.getEventDefinitions().get(0);
      	
      	if(timerDef.getTimeDuration() != null && 
            ((((FormalExpression) timerDef.getTimeDuration()).getBody() != null && 
                    ((FormalExpression) timerDef.getTimeDuration()).getBody().length() > 0) ||
                    
                    (((FormalExpression) timerDef.getTimeDate()).getBody() != null && 
                            ((FormalExpression) timerDef.getTimeDate()).getBody().length() > 0) ||
                            
                            (((FormalExpression) timerDef.getTimeCycle()).getBody() != null && 
                                    ((FormalExpression) timerDef.getTimeCycle()).getBody().length() > 0))) {
      	
	      	xtw.writeStartElement("timerEventDefinition");
	        
	        if(((FormalExpression) timerDef.getTimeDuration()).getBody() != null && 
	                      ((FormalExpression) timerDef.getTimeDuration()).getBody().length() > 0) {
	          
	          xtw.writeStartElement("timeDuration");
	          xtw.writeCharacters(((FormalExpression) timerDef.getTimeDuration()).getBody());
	          xtw.writeEndElement();
	        
	        } else if(((FormalExpression) timerDef.getTimeDate()).getBody() != null && 
	                      ((FormalExpression) timerDef.getTimeDate()).getBody().length() > 0) {
	          
	          xtw.writeStartElement("timeDate");
	          xtw.writeCharacters(((FormalExpression) timerDef.getTimeDate()).getBody());
	          xtw.writeEndElement();
	        
	        } else {
	          
	          xtw.writeStartElement("timeCycle");
	          xtw.writeCharacters(((FormalExpression) timerDef.getTimeCycle()).getBody());
	          xtw.writeEndElement();
	        }
	        
	        xtw.writeEndElement();
      	}
      }
      
      if(startEvent.getFormProperties().size() > 0) {
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
      xtw.writeAttribute("id", subProcessId + endEvent.getId());
      xtw.writeAttribute("name", endEvent.getName());
      
      if(endEvent.getEventDefinitions().size() > 0) {
        ErrorEventDefinition errorDef = (ErrorEventDefinition) endEvent.getEventDefinitions().get(0);
        if(errorDef.getErrorCode() != null && errorDef.getErrorCode().length() > 0) {
          xtw.writeStartElement("errorEventDefinition");
          xtw.writeAttribute("errorRef", errorDef.getErrorCode());
          xtw.writeEndElement();
        }
      }

      // end EndEvent element
      xtw.writeEndElement();

    } else if (object instanceof SequenceFlow) {
      SequenceFlowExport.createSequenceFlow(object, subProcessId, xtw);

    } else if (object instanceof UserTask) {
      UserTaskExport.createUserTask(object, subProcessId, xtw);

    } else if (object instanceof ScriptTask) {
      ScriptTaskExport.createScriptTask(object, subProcessId, xtw);

    } else if (object instanceof ServiceTask) {
      ServiceTaskExport.createServiceTask(object, subProcessId, xtw);

    } else if (object instanceof MailTask) {
      MailTaskExport.createMailTask(object, subProcessId, xtw);

    } else if (object instanceof ManualTask) {
      ManualTaskExport.createManualTask(object, subProcessId, xtw);

    } else if (object instanceof ReceiveTask) {
      ReceiveTaskExport.createReceiveTask(object, subProcessId, xtw);
    
    } else if (object instanceof BusinessRuleTask) {
      BusinessRuleTaskExport.createBusinessRuleTask(object, subProcessId, xtw);
    
    } else if (object instanceof AlfrescoScriptTask) {
      AlfrescoScriptTaskExport.createScriptTask(object, subProcessId, xtw);
      
    } else if (object instanceof CallActivity) {
      CallActivityExport.createCallActivity(object, subProcessId, xtw);

    } else if (object instanceof ParallelGateway) {
      ParallelGateway parallelGateway = (ParallelGateway) object;
      // start ParallelGateway element
      xtw.writeStartElement("parallelGateway");
      xtw.writeAttribute("id", subProcessId + parallelGateway.getId());
      if (parallelGateway.getName() != null) {
        xtw.writeAttribute("name", parallelGateway.getName());
      }

      // end ParallelGateway element
      xtw.writeEndElement();

    } else if (object instanceof ExclusiveGateway) {
      ExclusiveGateway exclusiveGateway = (ExclusiveGateway) object;
      // start ExclusiveGateway element
      xtw.writeStartElement("exclusiveGateway");
      xtw.writeAttribute("id", subProcessId + exclusiveGateway.getId());
      if (exclusiveGateway.getName() != null) {
        xtw.writeAttribute("name", exclusiveGateway.getName());
      }
      DefaultFlowExport.createDefaultFlow(object, subProcessId, xtw);

      // end ExclusiveGateway element
      xtw.writeEndElement();
      
    } else if (object instanceof BoundaryEvent) {
      BoundaryEventExport.createBoundaryEvent(object, subProcessId, xtw);

    } else if (object instanceof SubProcess) {
      SubProcess subProcess = (SubProcess) object;
      List<FlowElement> flowElementList = subProcess.getFlowElements();
      if(flowElementList != null && flowElementList.size() > 0) {
        // start SubProcess element
        xtw.writeStartElement("subProcess");
        xtw.writeAttribute("id", subProcess.getId());
        if (subProcess.getName() != null) {
          xtw.writeAttribute("name", subProcess.getName());
        }
        
        ExtensionListenerExport.createExtensionListenerXML(subProcess.getActivitiListeners(), true, EXECUTION_LISTENER, xtw);
        MultiInstanceExport.createMultiInstance(object, subProcessId, xtw);
        
        for (FlowElement flowElement : flowElementList) {
          createXML(flowElement, subProcess.getId());
        }

        // end SubProcess element
        xtw.writeEndElement();
      }
    }
  }

  /*private void createSubProcessXML(XMLStreamWriter xtw, String subProcessId) throws Exception {

    IResource resource = getResource(Util.getSubProcessURI(diagram, subProcessId));

    ResourceSet set = new ResourceSetImpl();
    final Diagram diagram = GraphitiUiInternal.getEmfService().getDiagramFromFile((IFile) resource, set);

    EList<EObject> contents = diagram.eResource().getContents();

    for (EObject object : contents) {
      createXML(object, xtw, subProcessId + "_");
    }
  }*/

  @Deprecated
  private void createErrorMessage(String message) {
    addProblemToDiagram(diagram, message, null);
  }
}
