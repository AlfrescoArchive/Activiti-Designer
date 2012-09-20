/**
 * 
 */
package org.activiti.designer.export.bpmn20.export;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.Artifact;
import org.activiti.designer.bpmn2.model.Association;
import org.activiti.designer.bpmn2.model.BaseElement;
import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.BusinessRuleTask;
import org.activiti.designer.bpmn2.model.CallActivity;
import org.activiti.designer.bpmn2.model.EndEvent;
import org.activiti.designer.bpmn2.model.ErrorEventDefinition;
import org.activiti.designer.bpmn2.model.EventGateway;
import org.activiti.designer.bpmn2.model.EventSubProcess;
import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.InclusiveGateway;
import org.activiti.designer.bpmn2.model.IntermediateCatchEvent;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.MailTask;
import org.activiti.designer.bpmn2.model.ManualTask;
import org.activiti.designer.bpmn2.model.Message;
import org.activiti.designer.bpmn2.model.MessageEventDefinition;
import org.activiti.designer.bpmn2.model.ParallelGateway;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.ReceiveTask;
import org.activiti.designer.bpmn2.model.ScriptTask;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.bpmn2.model.Signal;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.TextAnnotation;
import org.activiti.designer.bpmn2.model.ThrowEvent;
import org.activiti.designer.bpmn2.model.TimerEventDefinition;
import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoScriptTask;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.apache.commons.lang.StringUtils;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

public class BPMN20ExportMarshaller implements ActivitiNamespaceConstants {

  private Bpmn2MemoryModel model;
  private String modelFileName;
  private IFeatureProvider featureProvider;
  private IndentingXMLStreamWriter xtw;
  private boolean saveImage;

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
      if (StringUtils.isNotEmpty(model.getTargetNamespace())) {
        xtw.writeAttribute("targetNamespace", model.getTargetNamespace());
      } else {
        xtw.writeAttribute("targetNamespace", PROCESS_NAMESPACE);
      }
      
      for (Signal signal : model.getSignals()) {
        xtw.writeStartElement("signal");
        xtw.writeAttribute("id", signal.getId());
        xtw.writeAttribute("name", signal.getName());
        xtw.writeEndElement();
      }
      
      for (Message message : model.getMessages()) {
        xtw.writeStartElement("message");
        xtw.writeAttribute("id", message.getId());
        xtw.writeAttribute("name", message.getName());
        xtw.writeEndElement();
      }

      
      if(model.getPools().size() > 0) {
        xtw.writeStartElement("collaboration");
        xtw.writeAttribute("id", "Collaboration");
        for (Pool pool : model.getPools()) {
          xtw.writeStartElement("participant");
          xtw.writeAttribute("id", pool.getId());
          if(StringUtils.isNotEmpty(pool.getName())) {
            xtw.writeAttribute("name", pool.getName());
          }
          xtw.writeAttribute("processRef", pool.getProcessRef());
          xtw.writeEndElement();
        }
        xtw.writeEndElement();
      }
      
      for (Process process : model.getProcesses()) {
      
        if(process.getFlowElements().size() == 0 && process.getLanes().size() == 0) {
          // empty process, ignore it 
          continue;
        }
        
        // start process element
        xtw.writeStartElement("process");
        xtw.writeAttribute("id", process.getId());
        if(StringUtils.isNotEmpty(process.getName())) {
          xtw.writeAttribute("name", process.getName());
        }
        if (StringUtils.isNotEmpty(process.getDocumentation())) {
  
          xtw.writeStartElement("documentation");
          xtw.writeCharacters(process.getDocumentation());
          xtw.writeEndElement();
        }
        
        if (process.getCandidateStarterUsers().size() > 0) {
            Iterator<String> candidateStarterUserIterator = process.getCandidateStarterUsers().iterator();
            String candidateStarterUsers = candidateStarterUserIterator.next();
            while (candidateStarterUserIterator.hasNext()) {
            	candidateStarterUsers += ", " + candidateStarterUserIterator.next();
            }
            xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "candidateStarterUsers", candidateStarterUsers);
          } 
        
        if (process.getCandidateStarterGroups().size() > 0) {
            Iterator<String> candidateStarterGroupIterator = process.getCandidateStarterGroups().iterator();
            String candidateStarterGroups = candidateStarterGroupIterator.next();
            while (candidateStarterGroupIterator.hasNext()) {
            	candidateStarterGroups += ", " + candidateStarterGroupIterator.next();
            }
            xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "candidateStarterGroups", candidateStarterGroups);
          } 
        
        
        if(process.getLanes().size() > 0) {
          xtw.writeStartElement("laneSet");
          xtw.writeAttribute("id", "laneSet_" + process.getId());
          for (Lane lane : process.getLanes()) {
            xtw.writeStartElement("lane");
            xtw.writeAttribute("id", lane.getId());
            if(StringUtils.isNotEmpty(lane.getName())) {
              xtw.writeAttribute("name", lane.getName());
            }
            
            for (String flowNodeRef : lane.getFlowReferences()) {
              xtw.writeStartElement("flowNodeRef");
              xtw.writeCharacters(flowNodeRef);
              xtw.writeEndElement();
            }
            
            xtw.writeEndElement();
          }
          xtw.writeEndElement();
        }
        
        ExecutionListenerExport.createExecutionListenerXML(process.getExecutionListeners(), true, xtw);
        
        for (FlowElement flowElement : process.getFlowElements()) {
        	PictogramElement picElement = featureProvider.getPictogramElementForBusinessObject(flowElement);
        	if(picElement == null) continue;
        	
  	      createXML(flowElement);
        }
        
        for (Artifact artifact : process.getArtifacts()) {
          final PictogramElement pe = featureProvider.getPictogramElementForBusinessObject(artifact);
          if (pe == null) {
            continue;
          }
          createXML(artifact);
        }
  
        // end process element
        xtw.writeEndElement();
      }

      BpmnDIExport.createDIXML(model, featureProvider, xtw);

      // end definitions root element
      xtw.writeEndElement();
      xtw.writeEndDocument();

      xtw.flush();

      fos.close();

      xtw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    if(saveImage) {
      marshallImage(model, modelFileName);
    }
  }

  private void createXML(BaseElement object) throws Exception {
    if (object instanceof StartEvent) {
      StartEvent startEvent = (StartEvent) object;
      // start StartEvent element
      xtw.writeStartElement("startEvent");
      xtw.writeAttribute("id", startEvent.getId());
      
      if(StringUtils.isNotEmpty(startEvent.getName())) {
        xtw.writeAttribute("name", startEvent.getName());
      }

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

        } else if (StringUtils.isNotEmpty(timerDef.getTimeDate())) {

          xtw.writeStartElement("timeDate");
          xtw.writeCharacters(timerDef.getTimeDate());
          xtw.writeEndElement();

        } else if (StringUtils.isNotEmpty(timerDef.getTimeCycle())) {

          xtw.writeStartElement("timeCycle");
          xtw.writeCharacters(timerDef.getTimeCycle());
          xtw.writeEndElement();
        }

        xtw.writeEndElement();
      }

      if (startEvent.getEventDefinitions().size() > 0 && startEvent.getEventDefinitions().get(0) instanceof MessageEventDefinition) {

        MessageEventDefinition messageDef = (MessageEventDefinition) startEvent.getEventDefinitions().get(0);

        xtw.writeStartElement("messageEventDefinition ");

        if (StringUtils.isNotEmpty(messageDef.getMessageRef())) {

          xtw.writeStartElement("messageRef");
          xtw.writeCharacters(messageDef.getMessageRef());
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
      if(StringUtils.isNotEmpty(endEvent.getName())) {
        xtw.writeAttribute("name", endEvent.getName());
      }

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

    } else if (object instanceof Association) {
      AssociationExport.createAssociation(object, xtw);
      
    } else if (object instanceof UserTask) {
      UserTaskExport.createUserTask(object, xtw);

    } else if (object instanceof AlfrescoScriptTask) {
      AlfrescoScriptTaskExport.createScriptTask(object, xtw);
      
    } else if (object instanceof ScriptTask) {
      ScriptTaskExport.createScriptTask(object, xtw);

    } else if (object instanceof ServiceTask) {
      ServiceTaskExport.createServiceTask(object, xtw);

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
      DefaultFlowExport.createDefaultFlow(exclusiveGateway, xtw);

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
      DefaultFlowExport.createDefaultFlow(inclusiveGateway, xtw);

      // end InclusiveGateway element
      xtw.writeEndElement();
      
    } else if (object instanceof EventGateway) {
      EventGateway eventGateway = (EventGateway) object;
      // start EventGateway element
      xtw.writeStartElement("eventBasedGateway");
      xtw.writeAttribute("id", eventGateway.getId());
      if (eventGateway.getName() != null) {
        xtw.writeAttribute("name", eventGateway.getName());
      }
      
      // end EventGateway element
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
      
      DefaultFlowExport.createDefaultFlow(subProcess, xtw);
      AsyncActivityExport.createAsyncAttribute(object, xtw);

      ExecutionListenerExport.createExecutionListenerXML(subProcess.getExecutionListeners(), true, xtw);
      MultiInstanceExport.createMultiInstance(object, xtw);

      List<FlowElement> flowElementList = subProcess.getFlowElements();
      for (FlowElement flowElement : flowElementList) {
        createXML(flowElement);
      }

      // end SubProcess element
      xtw.writeEndElement();
      
    } else if (object instanceof TextAnnotation) {
      TextAnnotationExport.createTextAnnotation(object, xtw); 
    }
    
    if (object instanceof Activity) {
    	Activity activity = (Activity) object;
    	for(BoundaryEvent event : activity.getBoundaryEvents()) {
    		BoundaryEventExport.createBoundaryEvent(event, xtw);
    	}
    }
  }
  
  private void marshallImage(Bpmn2MemoryModel model, String modelFileName) {
    try {
      final GraphicalViewer graphicalViewer =  (GraphicalViewer) ((DiagramEditor) model.getFeatureProvider()
              .getDiagramTypeProvider().getDiagramEditor()).getAdapter(GraphicalViewer.class);

      if (graphicalViewer == null || graphicalViewer.getEditPartRegistry() == null)
        return;
      final ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) graphicalViewer.getEditPartRegistry().get(LayerManager.ID);
      final IFigure rootFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.PRINTABLE_LAYERS);
      final IFigure gridFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.GRID_LAYER);
      final Rectangle rootFigureBounds = rootFigure.getBounds();

      final boolean toggleRequired = gridFigure.isShowing();

      final Display display = Display.getDefault();

      final Image img = new Image(display, rootFigureBounds.width, rootFigureBounds.height);
      final GC imageGC = new GC(img);
      final SWTGraphics grap = new SWTGraphics(imageGC);

      // Access UI thread from runnable to print the canvas to the image
      display.syncExec(new Runnable() {

        public void run() {
          if (toggleRequired) {
            // Disable any grids temporarily
            gridFigure.setVisible(false);
          }
          // Deselect any selections
          graphicalViewer.deselectAll();
          rootFigure.paint(grap);
        }
      });

      ImageLoader imgLoader = new ImageLoader();
      imgLoader.data = new ImageData[] { img.getImageData() };

      ByteArrayOutputStream baos = new ByteArrayOutputStream(imgLoader.data.length);

      imgLoader.save(baos, SWT.IMAGE_PNG);

      imageGC.dispose();
      img.dispose();

      // Access UI thread from runnable
      display.syncExec(new Runnable() {

        public void run() {
          if (toggleRequired) {
            // Re-enable any grids
            gridFigure.setVisible(true);
          }
        }
      });

      String imageFileName = modelFileName.substring(0, modelFileName.lastIndexOf(".")) + ".png";
      File imageFile = new File(imageFileName);
      FileOutputStream outStream = new FileOutputStream(imageFile);
      baos.writeTo(outStream);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public boolean isSaveImage() {
    return saveImage;
  }

  public void setSaveImage(boolean saveImage) {
    this.saveImage = saveImage;
  }
}
