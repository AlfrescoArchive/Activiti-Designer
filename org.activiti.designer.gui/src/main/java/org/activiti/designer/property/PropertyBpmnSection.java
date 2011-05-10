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

import java.util.List;

import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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

  private Text idText;
  private Text nameText;
  private CCombo defaultCombo;
  private CLabel defaultLabel;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    Composite composite = factory.createFlatFormComposite(parent);
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

    CLabel valueLabel = factory.createCLabel(composite, "Name:", SWT.WRAP); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(nameText, -HSPACE);
    data.top = new FormAttachment(nameText, 0, SWT.CENTER);
    valueLabel.setLayoutData(data);
    
    defaultCombo = factory.createCCombo(composite, SWT.NONE);
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(nameText, VSPACE);
    defaultCombo.setLayoutData(data);
    defaultCombo.setVisible(false);
    defaultCombo.addFocusListener(listener);

    defaultLabel = factory.createCLabel(composite, "Default flow:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(defaultCombo, -HSPACE);
    data.top = new FormAttachment(defaultCombo, 0, SWT.CENTER);
    defaultLabel.setLayoutData(data);
    defaultLabel.setVisible(false);
  }

  @Override
  public void refresh() {
    nameText.removeFocusListener(listener);
    idText.removeFocusListener(listener);
    defaultCombo.removeFocusListener(listener);

    PictogramElement pe = getSelectedPictogramElement();

    if (pe != null) {
      Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      // the filter assured, that it is a EClass
      if (bo == null)
        return;
      String name = ((FlowElement) bo).getName();
      String id = ((FlowElement) bo).getId();
      nameText.setText(name == null ? "" : name);
      idText.setText(id == null ? "" : id);
      
      boolean disableDefault = true;
      
      if (bo instanceof Activity == true || bo instanceof ExclusiveGateway == true) {
        
        defaultCombo.removeAll();
        List<SequenceFlow> flowList = null;
        if(bo instanceof Activity == true) {
          flowList = ((Activity) bo).getOutgoing();
        } else {
          flowList = ((ExclusiveGateway) bo).getOutgoing();
        }
        if(flowList != null && flowList.size() > 1) {
          
          for (SequenceFlow flow : flowList) {
            defaultCombo.add(flow.getId());
          }
          
          SequenceFlow defaultFlow;
          if(bo instanceof Activity == true) {
            defaultFlow = ((Activity) bo).getDefault();
          } else {
            defaultFlow = ((ExclusiveGateway) bo).getDefault();
          }
          if(defaultFlow != null) {
            defaultCombo.select(defaultCombo.indexOf(defaultFlow.getId()));
          }
          disableDefault = false;
        }
      }
      
      if(disableDefault == true) {
        defaultCombo.setVisible(false);
        defaultLabel.setVisible(false);
        
      } else {
        defaultCombo.setVisible(true);
        defaultLabel.setVisible(true);
      }
      
      idText.addFocusListener(listener);
      nameText.addFocusListener(listener);
      defaultCombo.addFocusListener(listener);
    }
  }

  private FocusListener listener = new FocusListener() {

    public void focusGained(final FocusEvent e) {
    }

    public void focusLost(final FocusEvent e) {
      PictogramElement pe = getSelectedPictogramElement();
      if (pe == null)
        return;
      Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);

      if (!(bo instanceof FlowElement))
        return;

      DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
      TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
      ActivitiUiUtil.runModelChange(new Runnable() {

        public void run() {
          Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
          if (bo == null)
            return;
          
          String id = idText.getText();
          ((FlowElement) bo).setId(id);
          
          String name = nameText.getText();
          ((FlowElement) bo).setName(name);
          
          if((bo instanceof Activity || bo instanceof ExclusiveGateway)
                  && defaultCombo.isVisible() == true) {
            
            String defaultValue = defaultCombo.getText();
            SequenceFlow defaultFlow = null;
            if(defaultValue != null && defaultValue.length() > 0) {
              List<SequenceFlow> flowList = null;
              if(bo instanceof Activity) {
                flowList = ((Activity) bo).getOutgoing();
              } else {
                flowList = ((ExclusiveGateway) bo).getOutgoing();
              }
              for (SequenceFlow flow : flowList) {
                if(flow.getId().equals(defaultValue)) {
                  defaultFlow = flow;
                }
              }
            }
            if(bo instanceof Activity) {
              ((Activity) bo).setDefault(defaultFlow);
            } else {
              ((ExclusiveGateway) bo).setDefault(defaultFlow);
            }
          }
          
          if (!(getSelectedPictogramElement() instanceof FreeFormConnection))
            return;
          EList<ConnectionDecorator> decoratorList = ((FreeFormConnection) getSelectedPictogramElement()).getConnectionDecorators();
          for (ConnectionDecorator decorator : decoratorList) {
            if (decorator.getGraphicsAlgorithm() instanceof org.eclipse.graphiti.mm.algorithms.Text) {
              org.eclipse.graphiti.mm.algorithms.Text text = (org.eclipse.graphiti.mm.algorithms.Text) decorator.getGraphicsAlgorithm();
              text.setValue(name);
            }
          }
        }
      }, editingDomain, "Model Update");
    }
  };

}
