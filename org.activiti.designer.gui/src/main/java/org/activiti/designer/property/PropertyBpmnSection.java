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

import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.TextAnnotation;
import org.activiti.designer.util.ActivitiConstants;
import org.activiti.designer.util.TextUtil;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyBpmnSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Composite composite;
  private Text idText;
  private CLabel nameLabel;
  private Text nameText;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    composite = factory.createFlatFormComposite(parent);
    FormData data;

    idText = factory.createText(composite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, -HSPACE);
    data.top = new FormAttachment(0, VSPACE);
    idText.setLayoutData(data);

    CLabel idLabel = factory.createCLabel(composite, "Id:", SWT.WRAP); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(idText, -HSPACE);
    data.top = new FormAttachment(idText, 0, SWT.CENTER);
    idLabel.setLayoutData(data);

    nameText = factory.createText(composite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, -HSPACE);
    data.top = new FormAttachment(idText, VSPACE);
    nameText.setLayoutData(data);
    nameText.addFocusListener(listener);

    nameLabel = factory.createCLabel(composite, "Name:", SWT.WRAP); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(nameText, -HSPACE);
    data.top = new FormAttachment(nameText, 0, SWT.CENTER);
    nameLabel.setLayoutData(data);

  }

  @Override
  public void refresh() {

    nameText.removeFocusListener(listener);
    idText.removeFocusListener(listener);

    PictogramElement pe = getSelectedPictogramElement();

    if (pe != null) {
      Object bo = getBusinessObject(pe);
      // the filter assured, that it is a EClass
      if (bo == null) {
        return;
      }

      String name = null;
      if (bo instanceof FlowElement) {
        name = ((FlowElement) bo).getName();
      } else if (bo instanceof Pool) {
        name = ((Pool) bo).getName();
      } else if (bo instanceof Lane) {
        name = ((Lane) bo).getName();
      } else if (bo instanceof Artifact) {
        // text annotations do not have a name
        nameText.setVisible(false);
        nameLabel.setVisible(false);
      }

      String id = ((BaseElement) bo).getId();
      nameText.setText(name == null ? "" : name);
      idText.setText(id == null ? "" : id);

      idText.addFocusListener(listener);
      nameText.addFocusListener(listener);
    }

  }

  private FocusListener listener = new FocusListener() {

    @Override
    public void focusGained(final FocusEvent e) {
    }

    @Override
    public void focusLost(final FocusEvent e) {
      final PictogramElement pe = getSelectedPictogramElement();
      if (pe == null) {
        return;
      }
      final Object bo = getBusinessObject(pe);

      if (!(bo instanceof BaseElement) || bo instanceof TextAnnotation) {
    	  // we need to exclude the text annotation here, although it is a base element
    	  // because it's text is handled differently and by the corresponding property
    	  // PropertyTextAnnotationSection

    	  // if we do not ignore this here, the text will get lost during activation
    	  // of the base property sheet.
    	  return;
      }

      DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
      TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
      ActivitiUiUtil.runModelChange(new Runnable() {

        @Override
        public void run() {
          BaseElement element = (BaseElement) bo;
          String id = idText.getText();
          updateParentLane(element.getId(), id);
          updateFlows(element, id);
          element.setId(id);

          String name = nameText.getText();
          if (bo instanceof FlowElement) {
            ((FlowElement) bo).setName(name);
          } else if (bo instanceof Pool) {
            ((Pool) bo).setName(name);
          } else if (bo instanceof Lane) {
            ((Lane) bo).setName(name);
          }

          UpdateContext updateContext = new UpdateContext(pe);
          IUpdateFeature updateFeature = getFeatureProvider(pe).getUpdateFeature(updateContext);
          if(updateFeature != null) {
            updateFeature.update(updateContext);
          }

          if (pe instanceof ContainerShape) {
            ContainerShape cs = (ContainerShape) pe;
            for (Shape shape : cs.getChildren()) {
              if (shape.getGraphicsAlgorithm() instanceof Text) {
                org.eclipse.graphiti.mm.algorithms.Text text = (org.eclipse.graphiti.mm.algorithms.Text) shape.getGraphicsAlgorithm();
                text.setValue(name);
              }
              if (shape.getGraphicsAlgorithm() instanceof MultiText) {
                MultiText text = (MultiText) shape.getGraphicsAlgorithm();
                text.setValue(name);
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
              text.setValue(name);
              TextUtil.setTextSize(name, text);
            }
          }
        }
      }, editingDomain, ActivitiConstants.DEFAULT_MODEL_CHANGE_TEXT);
    }
  };

  protected void updateParentLane(String oldElementId, String newElementId) {
    Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
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

}
