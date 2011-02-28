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
import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.eclipse.bpmn2.BaseElement;
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
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.services.GraphitiUi;

/**
 * @author Yvo Swillens
 */
public class DiagramUpdater {

  public static void syncDiagram(DiagramEditor diagramEditor, Diagram diagram, IStorage bpmnStorage) {

    if (diagramEditor == null) {
      System.out.println("diagramEditor cannot be null");
      return;
    }
    if (diagram == null) {
      System.out.println("diagram cannot be null");
      return;
    }
    if (bpmnStorage == null) {
      System.out.println("bpmnStorage cannot be null");
      return;
    }

    TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
    IDiagramTypeProvider dtp = GraphitiUi.getExtensionManager().createDiagramTypeProvider(diagram,
            "org.activiti.designer.diagram.ActivitiBPMNDiagramTypeProvider"); //$NON-NLS-1$
    IFeatureProvider featureProvider = dtp.getFeatureProvider();

    BpmnParser bpmnParser = readBpmn(editingDomain, diagram, bpmnStorage);

    if (bpmnParser.process != null) {
      updateProcessInDiagram(editingDomain, diagram, bpmnParser.process, featureProvider);
    }

    if (bpmnParser.bpmnList == null || bpmnParser.bpmnList.size() == 0)
      return;

    updateFlowElementsInDiagram(editingDomain, diagram, bpmnParser.bpmnList, featureProvider);

    if (bpmnParser.sequenceFlowList == null || bpmnParser.sequenceFlowList.size() == 0)
      return;

    updateSequenceFlowsInDiagram(editingDomain, diagram, bpmnParser.sequenceFlowList, featureProvider);
  }

  private static BpmnParser readBpmn(TransactionalEditingDomain editingDomain, Diagram diagram, IStorage bpmnStorage) {

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

  private static void updateFlowElementsInDiagram(TransactionalEditingDomain editingDomain, final Diagram diagram, final List<FlowElement> bpmnFlowElements,
          final IFeatureProvider featureProvider) {

    ActivitiUiUtil.runModelChange(new Runnable() {

      public void run() {

        for (FlowElement bpmnFlowElement : bpmnFlowElements) {
          FlowElement diagramFlowElement = lookupFlowElementInDiagram(bpmnFlowElement, diagram);
          if (diagramFlowElement != null) {

            diagramFlowElement.setName(bpmnFlowElement.getName());

            if (diagramFlowElement instanceof UserTask) {

              UserTask userTask = (UserTask) diagramFlowElement;
              userTask.setAssignee(((UserTask) bpmnFlowElement).getAssignee());

              if (((UserTask) bpmnFlowElement).getCandidateGroups() != null) {
                Iterator<CandidateGroup> itCandidate = userTask.getCandidateGroups().iterator();
                while (itCandidate.hasNext()) {
                  diagram.eResource().getContents().remove(itCandidate.next());
                }
                userTask.getCandidateGroups().clear();
                for (CandidateGroup candidateGroup : ((UserTask) bpmnFlowElement).getCandidateGroups()) {
                  diagram.eResource().getContents().add(candidateGroup);
                  userTask.getCandidateGroups().add(candidateGroup);
                }
              }

              if (((UserTask) bpmnFlowElement).getCandidateUsers() != null) {
                Iterator<CandidateUser> itCandidate = userTask.getCandidateUsers().iterator();
                while (itCandidate.hasNext()) {
                  diagram.eResource().getContents().remove(itCandidate.next());
                }
                userTask.getCandidateUsers().clear();
                for (CandidateUser candidateUser : ((UserTask) bpmnFlowElement).getCandidateUsers()) {
                  diagram.eResource().getContents().add(candidateUser);
                  userTask.getCandidateUsers().add(candidateUser);
                }
              }

            } else if (diagramFlowElement instanceof ScriptTask) {

              ScriptTask scriptTask = (ScriptTask) diagramFlowElement;
              scriptTask.setScriptFormat(((ScriptTask) bpmnFlowElement).getScriptFormat());
              scriptTask.setScript(((ScriptTask) bpmnFlowElement).getScript());

            } else if (diagramFlowElement instanceof ServiceTask) {

              ServiceTask serviceTask = (ServiceTask) diagramFlowElement;
              serviceTask.setImplementationType(((ServiceTask) bpmnFlowElement).getImplementationType());
              serviceTask.setImplementation(((ServiceTask) bpmnFlowElement).getImplementation());
              serviceTask.setResultVariableName(((ServiceTask) bpmnFlowElement).getResultVariableName());
              if (((ServiceTask) bpmnFlowElement).getFieldExtensions() != null) {
                Iterator<FieldExtension> itField = serviceTask.getFieldExtensions().iterator();
                while (itField.hasNext()) {
                  diagram.eResource().getContents().remove(itField.next());
                }
                serviceTask.getFieldExtensions().clear();
                for (FieldExtension fieldExtension : ((ServiceTask) bpmnFlowElement).getFieldExtensions()) {
                  diagram.eResource().getContents().add(fieldExtension);
                  serviceTask.getFieldExtensions().add(fieldExtension);
                }
              }
            } else if (diagramFlowElement instanceof MailTask) {

              MailTask mailTask = (MailTask) diagramFlowElement;
              mailTask.setBcc(((MailTask) bpmnFlowElement).getBcc());
              mailTask.setCc(((MailTask) bpmnFlowElement).getCc());
              mailTask.setFrom(((MailTask) bpmnFlowElement).getFrom());
              mailTask.setHtml(((MailTask) bpmnFlowElement).getHtml());
              mailTask.setSubject(((MailTask) bpmnFlowElement).getSubject());
              mailTask.setText(((MailTask) bpmnFlowElement).getText());
              mailTask.setTo(((MailTask) bpmnFlowElement).getTo());
            }

            if (diagramFlowElement instanceof Task) {
              if (((Task) bpmnFlowElement).getActivitiListeners() != null) {
                ((Task) diagramFlowElement).getActivitiListeners().clear();
                ((Task) diagramFlowElement).getActivitiListeners().addAll(((Task) bpmnFlowElement).getActivitiListeners());
              }
            }

            updatePictogramContext(diagramFlowElement, featureProvider);
          }
        }

      }
    }, editingDomain, "Diagram Models Update");
  }

  private static void updateSequenceFlowsInDiagram(TransactionalEditingDomain editingDomain, final Diagram diagram,
          final List<SequenceFlowModel> sequenceFlowElements, final IFeatureProvider featureProvider) {

    ActivitiUiUtil.runModelChange(new Runnable() {

      public void run() {

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
    }, editingDomain, "Diagram Models Update");
  }

  private static void updateProcessInDiagram(TransactionalEditingDomain editingDomain, final Diagram diagram, final org.eclipse.bpmn2.Process process,
          final IFeatureProvider featureProvider) {

    ActivitiUiUtil.runModelChange(new Runnable() {

      public void run() {

        org.eclipse.bpmn2.Process diagramElement = lookupProcessInDiagram(diagram);
        if (diagramElement != null) {

          if (process.getExecutionListeners() != null) {
            diagramElement.getExecutionListeners().clear();
            diagramElement.getExecutionListeners().addAll(process.getExecutionListeners());
          }
        }
      }
    }, editingDomain, "Diagram Models Update");
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
}
