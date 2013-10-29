/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.property;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyGeneralSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Text idText;
  private Text nameText;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    idText = createTextControl(false);
    createLabel("Id", idText);
    nameText = createTextControl(false);
    createLabel("Name", nameText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    BaseElement element = (BaseElement) businessObject;
    if (control == idText) {
      return element.getId();
      
    } else if (control == nameText) {
      return getName(businessObject);
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, final Object businessObject) {
    BaseElement element = (BaseElement) businessObject;
    if (control == idText) {
      updateParentLane(element.getId(), idText.getText());
      updateFlows(element, idText.getText());
      element.setId(idText.getText());
      
    } else if (control == nameText) {
      final PictogramElement pe = getSelectedPictogramElement();
      setName(businessObject, nameText.getText());
          
      /*UpdateContext updateContext = new UpdateContext(pe);
      IUpdateFeature updateFeature = getDiagramTypeProvider().getFeatureProvider().getUpdateFeature(updateContext);
      if(updateFeature != null) {
        updateFeature.update(updateContext);
      }

      if (pe instanceof ContainerShape) {
        ContainerShape cs = (ContainerShape) pe;
        for (Shape shape : cs.getChildren()) {
          if (shape.getGraphicsAlgorithm() instanceof Text) {
            org.eclipse.graphiti.mm.algorithms.Text text = (org.eclipse.graphiti.mm.algorithms.Text) shape.getGraphicsAlgorithm();
            text.setValue(nameText.getText());
          }
          if (shape.getGraphicsAlgorithm() instanceof MultiText) {
            MultiText text = (MultiText) shape.getGraphicsAlgorithm();
            text.setValue(nameText.getText());
          }
        }
      }

      if (!(getSelectedPictogramElement() instanceof FreeFormConnection)) {
        return;
      }
      EList<ConnectionDecorator> decoratorList = ((FreeFormConnection) getSelectedPictogramElement()).getConnectionDecorators();
      for (ConnectionDecorator decorator : decoratorList) {
        if (decorator.getGraphicsAlgorithm() instanceof org.eclipse.graphiti.mm.algorithms.MultiText) {
          org.eclipse.graphiti.mm.algorithms.MultiText text = (org.eclipse.graphiti.mm.algorithms.MultiText) decorator.getGraphicsAlgorithm();
          text.setValue(nameText.getText());
          TextUtil.setTextSize(text);
        }
      }*/
    }
  }

  protected void updateParentLane(String oldElementId, String newElementId) {
    BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
    for (Process process : model.getBpmnModel().getProcesses()) {
      for (Lane lane : process.getLanes()) {
        if (lane.getFlowReferences().contains(oldElementId)) {
          lane.getFlowReferences().remove(oldElementId);
          lane.getFlowReferences().add(newElementId);
          return;
        }
      }
    }
  }

  protected void updateFlows(BaseElement element, String newElementId) {
    if (element instanceof FlowNode) {
      FlowNode flowNode = (FlowNode) element;
      for (SequenceFlow sequenceFlow : flowNode.getIncomingFlows()) {
        sequenceFlow.setTargetRef(newElementId);
      }
      for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
        sequenceFlow.setSourceRef(newElementId);
      }
    }
  }
  
  protected String getName(Object bo) {
    String name = null;
    if (bo instanceof FlowElement) {
      name = ((FlowElement) bo).getName();
    } else if (bo instanceof Pool) {
      name = ((Pool) bo).getName();
    } else if (bo instanceof Lane) {
      name = ((Lane) bo).getName();
    }
    return name;
  }
  
  protected void setName(Object bo, String name) {
    if (bo instanceof FlowElement) {
      ((FlowElement) bo).setName(name);
    } else if (bo instanceof Pool) {
      ((Pool) bo).setName(name);
    } else if (bo instanceof Lane) {
      ((Lane) bo).setName(name);
    }
  }

}
