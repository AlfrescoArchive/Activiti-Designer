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

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyDefaultFlowSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Composite composite;
  private CCombo defaultCombo;
  private CLabel defaultLabel;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    composite = factory.createFlatFormComposite(parent);
    FormData data;

    defaultCombo = getWidgetFactory().createCCombo(composite, SWT.NONE);
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, VSPACE);
    defaultCombo.setLayoutData(data);
    defaultCombo.addFocusListener(listener);

    defaultLabel = getWidgetFactory().createCLabel(composite, "Default flow:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(defaultCombo, -HSPACE);
    data.top = new FormAttachment(defaultCombo, 0, SWT.CENTER);
    defaultLabel.setLayoutData(data);
  }
  
  @Override
  public void refresh() {
    defaultCombo.removeFocusListener(listener);

    PictogramElement pe = getSelectedPictogramElement();

    if (pe != null) {
      Object bo = getBusinessObject(pe);
      // the filter assured, that it is a EClass
      if (bo == null)
        return;

      List<SequenceFlow> flowList = null;
      if(bo instanceof Activity) {
        flowList = ((Activity) bo).getOutgoing();
      } else if(bo instanceof Gateway) {
        flowList = ((Gateway) bo).getOutgoing();
      }
      
      defaultCombo.removeAll();
        	
      for (SequenceFlow flow : flowList) {
        defaultCombo.add(flow.getId());
      }
      
      String defaultFlow = null;
      if(bo instanceof Activity) {
        defaultFlow = ((Activity) bo).getDefaultFlow();
      } else if(bo instanceof Gateway) {
        defaultFlow = ((Gateway) bo).getDefaultFlow();
      }
      
      if(defaultFlow != null) {
        defaultCombo.select(defaultCombo.indexOf(defaultFlow));
      }
      
      defaultCombo.addFocusListener(listener);
    }
  }

  private FocusListener listener = new FocusListener() {

    public void focusGained(final FocusEvent e) {
    }

    public void focusLost(final FocusEvent e) {
      final PictogramElement pe = getSelectedPictogramElement();
      if (pe == null)
        return;
      final Object bo = getBusinessObject(pe);

      DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
      TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
      ActivitiUiUtil.runModelChange(new Runnable() {

        public void run() {
          String defaultValue = defaultCombo.getText();
          if(bo instanceof Activity) {
            ((Activity) bo).setDefaultFlow(defaultValue);
          } else if(bo instanceof Gateway) {
            ((Gateway) bo).setDefaultFlow(defaultValue);
          }
        }
      }, editingDomain, "Model Update");
    }
  };

}
