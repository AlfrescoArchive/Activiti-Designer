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

/**
 * @Autor Yvo Swillens
 *
 * Updates existing (Flow)elements in Activiti Diagram
 * based on elements in BPMN2.0.xml 
 */
package org.activiti.designer.eclipse.editor.sync;

import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.designer.eclipse.bpmn.BpmnParser;
import org.activiti.designer.eclipse.bpmn.SequenceFlowModel;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CandidateGroup;
import org.eclipse.bpmn2.CandidateUser;
import org.eclipse.bpmn2.FieldExtension;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.MailTask;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.core.resources.IStorage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.services.GraphitiUi;

/**
 * @author Yvo Swillens
 */
public class DiagramUpdater extends RecordingCommand {
	
	Diagram diagram;
	IStorage bpmnStorage;
	
	public DiagramUpdater(TransactionalEditingDomain editingDomain, 
			Diagram diagram, IStorage bpmnStorage) {
		
		super(editingDomain);
		this.diagram = diagram;
		this.bpmnStorage = bpmnStorage;
	}
	
	@Override
  protected void doExecute() {
		syncDiagram();
  }

  private void syncDiagram() {

    if (diagram == null) {
      System.out.println("diagram cannot be null");
      return;
    }
    if (bpmnStorage == null) {
      System.out.println("bpmnStorage cannot be null");
      return;
    }

    IDiagramTypeProvider dtp = GraphitiUi.getExtensionManager().createDiagramTypeProvider(diagram,
    		GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId())); //$NON-NLS-1$
    IFeatureProvider featureProvider = dtp.getFeatureProvider();

    BpmnParser bpmnParser = readBpmn(bpmnStorage);

    if (bpmnParser.process != null) {
      updateProcessInDiagram(diagram, bpmnParser.process);
    }

    if (bpmnParser.bpmnList == null || bpmnParser.bpmnList.size() == 0)
      return;

    updateFlowElementsInDiagram(diagram, bpmnParser.bpmnList, featureProvider);

    if (bpmnParser.sequenceFlowList == null || bpmnParser.sequenceFlowList.size() == 0)
      return;

    updateSequenceFlowsInDiagram(diagram, bpmnParser.sequenceFlowList);
  }

  private static BpmnParser readBpmn(IStorage bpmnStorage) {

    BpmnParser bpmnParser = new BpmnParser();
    try {
      XMLInputFactory xif = XMLInputFactory.newInstance();
      InputStreamReader in = new InputStreamReader(bpmnStorage.getContents(), "UTF-8");
      XMLStreamReader xtr = xif.createXMLStreamReader(in);

      bpmnParser.parseBpmn(xtr);

      xtr.close();
      in.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return bpmnParser;
  }

  private static void updateFlowElementsInDiagram(final Diagram diagram, final List<FlowElement> bpmnFlowElements,
  		IFeatureProvider featureProvider) {

    for (FlowElement bpmnFlowElement : bpmnFlowElements) {
      FlowElement diagramFlowElement = lookupFlowElementInDiagram(bpmnFlowElement, diagram);
      if (diagramFlowElement != null) {

        diagramFlowElement.setName(bpmnFlowElement.getName());

        if (diagramFlowElement instanceof UserTask && bpmnFlowElement instanceof UserTask) {

          UserTask targetUserTask = (UserTask) diagramFlowElement;
          UserTask parsedUserTask = (UserTask) bpmnFlowElement;
          
          targetUserTask.setAssignee(parsedUserTask.getAssignee());
          targetUserTask.setFormKey(parsedUserTask.getFormKey());
          targetUserTask.setPriority(parsedUserTask.getPriority());
          targetUserTask.setDueDate(parsedUserTask.getDueDate());
          
          targetUserTask.getFormProperties().clear();
          targetUserTask.getFormProperties().addAll(parsedUserTask.getFormProperties());

          Iterator<CandidateGroup> itCandidateGroup = targetUserTask.getCandidateGroups().iterator();
          while (itCandidateGroup.hasNext()) {
            diagram.eResource().getContents().remove(itCandidateGroup.next());
          }
          targetUserTask.getCandidateGroups().clear();
          
          if (parsedUserTask.getCandidateGroups() != null) {
            for (CandidateGroup candidateGroup : parsedUserTask.getCandidateGroups()) {
              diagram.eResource().getContents().add(candidateGroup);
              targetUserTask.getCandidateGroups().add(candidateGroup);
            }
          }
          
          Iterator<CandidateUser> itCandidateUser = targetUserTask.getCandidateUsers().iterator();
          while (itCandidateUser.hasNext()) {
            diagram.eResource().getContents().remove(itCandidateUser.next());
          }
          targetUserTask.getCandidateUsers().clear();

          if (parsedUserTask.getCandidateUsers() != null) {
            for (CandidateUser candidateUser : parsedUserTask.getCandidateUsers()) {
              diagram.eResource().getContents().add(candidateUser);
              targetUserTask.getCandidateUsers().add(candidateUser);
            }
          }

        } else if (diagramFlowElement instanceof ScriptTask && bpmnFlowElement instanceof ScriptTask) {

          ScriptTask targetScriptTask = (ScriptTask) diagramFlowElement;
          ScriptTask parsedScriptTask = (ScriptTask) bpmnFlowElement;
          
          targetScriptTask.setScriptFormat(parsedScriptTask.getScriptFormat());
          targetScriptTask.setScript(parsedScriptTask.getScript());

        } else if (diagramFlowElement instanceof ServiceTask && bpmnFlowElement instanceof ServiceTask) {

          ServiceTask targetServiceTask = (ServiceTask) diagramFlowElement;
          ServiceTask parsedServiceTask = (ServiceTask) bpmnFlowElement;
          
          targetServiceTask.setImplementationType(parsedServiceTask.getImplementationType());
          targetServiceTask.setImplementation(parsedServiceTask.getImplementation());
          targetServiceTask.setResultVariableName(parsedServiceTask.getResultVariableName());
          
          Iterator<FieldExtension> itField = targetServiceTask.getFieldExtensions().iterator();
          while (itField.hasNext()) {
            diagram.eResource().getContents().remove(itField.next());
          }
          targetServiceTask.getFieldExtensions().clear();
          
          if (parsedServiceTask.getFieldExtensions() != null) {
            for (FieldExtension fieldExtension : parsedServiceTask.getFieldExtensions()) {
              diagram.eResource().getContents().add(fieldExtension);
              targetServiceTask.getFieldExtensions().add(fieldExtension);
            }
          }
        
        } else if (diagramFlowElement instanceof BusinessRuleTask && bpmnFlowElement instanceof BusinessRuleTask) {

        	BusinessRuleTask targetBusinessRuleTask = (BusinessRuleTask) diagramFlowElement;
          BusinessRuleTask parsedBusinessRuleTask = (BusinessRuleTask) bpmnFlowElement;

          targetBusinessRuleTask.setResultVariableName(parsedBusinessRuleTask.getResultVariableName());
          targetBusinessRuleTask.setExclude(parsedBusinessRuleTask.isExclude());
          
          targetBusinessRuleTask.getRuleNames().clear();
          targetBusinessRuleTask.getRuleNames().addAll(parsedBusinessRuleTask.getRuleNames());
          
          targetBusinessRuleTask.getInputVariables().clear();
          targetBusinessRuleTask.getInputVariables().addAll(parsedBusinessRuleTask.getInputVariables());
          
        } else if (diagramFlowElement instanceof MailTask && bpmnFlowElement instanceof MailTask) {

          MailTask targetMailTask = (MailTask) diagramFlowElement;
          MailTask parsedMailTask = (MailTask) bpmnFlowElement;
          
          targetMailTask.setBcc(parsedMailTask.getBcc());
          targetMailTask.setCc(parsedMailTask.getCc());
          targetMailTask.setFrom(parsedMailTask.getFrom());
          targetMailTask.setHtml(parsedMailTask.getHtml());
          targetMailTask.setSubject(parsedMailTask.getSubject());
          targetMailTask.setText(parsedMailTask.getText());
          targetMailTask.setTo(parsedMailTask.getTo());
        }

        if (diagramFlowElement instanceof Task && bpmnFlowElement instanceof Task) {
        	
        	Task targetTask = (Task) diagramFlowElement;
        	Task parsedTask = (Task) bpmnFlowElement;
        	
        	targetTask.setLoopCharacteristics(parsedTask.getLoopCharacteristics());
        	
        	targetTask.getActivitiListeners().clear();
          if (parsedTask.getActivitiListeners() != null) {
          	targetTask.getActivitiListeners().addAll(parsedTask.getActivitiListeners());
          }
        }

        updatePictogramContext(diagramFlowElement, featureProvider);
      }
    }
  }

  private static void updateSequenceFlowsInDiagram(final Diagram diagram,
          final List<SequenceFlowModel> sequenceFlowElements) {

    for (SequenceFlowModel sequenceFlowModel : sequenceFlowElements) {
      SequenceFlow diagramFlowElement = lookupSequenceFlowInDiagram(sequenceFlowModel, diagram);
      if (diagramFlowElement != null) {

        if (sequenceFlowModel.conditionExpression != null) {
          diagramFlowElement.setConditionExpression(sequenceFlowModel.conditionExpression);
        }
        if (sequenceFlowModel.listenerList != null) {
          diagramFlowElement.getExecutionListeners().clear();
          diagramFlowElement.getExecutionListeners().addAll(sequenceFlowModel.listenerList);
        }
      }
    }
  }

  private static void updateProcessInDiagram(final Diagram diagram, final org.eclipse.bpmn2.Process process) {

    org.eclipse.bpmn2.Process diagramElement = lookupProcessInDiagram(diagram);
    if (diagramElement != null) {

      if (process.getExecutionListeners() != null) {
        diagramElement.getExecutionListeners().clear();
        diagramElement.getExecutionListeners().addAll(process.getExecutionListeners());
      }
    }
  }

  private static void updatePictogramContext(BaseElement source, IFeatureProvider featureProvider) {

    PictogramElement pictoElem = featureProvider.getPictogramElementForBusinessObject(source);
    UpdateContext context = new UpdateContext(pictoElem);
    IUpdateFeature feature = featureProvider.getUpdateFeature(context);
    if (feature.canUpdate(context)) {
      IReason reason = feature.updateNeeded(context);
      if (reason.toBoolean()) {
        feature.update(context);
      }
    }
  }
  
  private static FlowElement lookupFlowElementInDiagram(FlowElement sourceElement, Diagram diagram) {

    FlowElement modifyElement = null;
    for (EObject targetElement : diagram.eResource().getContents()) {

      if ((targetElement instanceof FlowElement) && flowIdIsEqual(sourceElement, (FlowElement) targetElement)) {
        modifyElement = (FlowElement) targetElement;
        break;
      }
    }
    return modifyElement;
  }

  private static boolean flowIdIsEqual(FlowElement f1, FlowElement f2) {

    return f1.getId().equalsIgnoreCase(f2.getId());
  }

  private static SequenceFlow lookupSequenceFlowInDiagram(SequenceFlowModel sequenceModel, Diagram diagram) {

    SequenceFlow modifyElement = null;
    for (EObject targetElement : diagram.eResource().getContents()) {

      if ((targetElement instanceof SequenceFlow)) {
        SequenceFlow sequenceFlow = (SequenceFlow) targetElement;
        if (sequenceModel.sourceRef.equals(sequenceFlow.getSourceRef().getId()) && sequenceModel.targetRef.equals(sequenceFlow.getTargetRef().getId())) {

          modifyElement = sequenceFlow;
          break;
        }
      }
    }
    return modifyElement;
  }

  private static org.eclipse.bpmn2.Process lookupProcessInDiagram(Diagram diagram) {

    org.eclipse.bpmn2.Process process = null;
    for (EObject targetElement : diagram.eResource().getContents()) {

      if ((targetElement instanceof org.eclipse.bpmn2.Process)) {
        process = (org.eclipse.bpmn2.Process) targetElement;
        break;
      }
    }
    return process;
  }
}
