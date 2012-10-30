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

import org.activiti.designer.bpmn2.model.Activity;
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

public class PropertyAsyncSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Composite composite;
  private CCombo asyncCombo;
  private CLabel asyncLabel;
  private CCombo exclusiveCombo;
  private CLabel exclusiveLabel;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    composite = factory.createFlatFormComposite(parent);
    FormData data;

    asyncCombo = getWidgetFactory().createCCombo(composite, SWT.DROP_DOWN | SWT.BORDER);
    asyncCombo.add("");
    asyncCombo.add("True");
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(50, 0);
    data.top = new FormAttachment(0, VSPACE);
    asyncCombo.setLayoutData(data);
    asyncCombo.addFocusListener(listener);
    
    asyncLabel = getWidgetFactory().createCLabel(composite, "Asynchronous:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(asyncCombo, -HSPACE);
    data.top = new FormAttachment(asyncCombo, 0, SWT.CENTER);
    asyncLabel.setLayoutData(data);
    
    exclusiveCombo = getWidgetFactory().createCCombo(composite, SWT.DROP_DOWN | SWT.BORDER);
    exclusiveCombo.add("");
    exclusiveCombo.add("False");
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(50, 0);
    data.top = new FormAttachment(asyncCombo, VSPACE);
    exclusiveCombo.setLayoutData(data);
    exclusiveCombo.addFocusListener(listener);
    
    exclusiveLabel = getWidgetFactory().createCLabel(composite, "Exclusive:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(exclusiveCombo, -HSPACE);
    data.top = new FormAttachment(exclusiveCombo, 0, SWT.CENTER);
    exclusiveLabel.setLayoutData(data);
  }
  
  @Override
  public void refresh() {
    asyncCombo.removeFocusListener(listener);
    exclusiveCombo.removeFocusListener(listener);
    
    PictogramElement pe = getSelectedPictogramElement();

    if (pe != null) {
      Object bo = getBusinessObject(pe);
      // the filter assured, that it is a EClass
      if (bo == null)
        return;

      Activity activity = (Activity) bo;
      if(activity.isAsynchronous()) {
        asyncCombo.select(1);
      } else {
        asyncCombo.select(0);
      }
  	
      if(activity.isNotExclusive()) {
        exclusiveCombo.select(1);
      } else {
        exclusiveCombo.select(0);
      }
    
      asyncCombo.addFocusListener(listener);
      exclusiveCombo.addFocusListener(listener);
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
          
          if(bo instanceof Activity) {
          	Activity activity = (Activity) bo;
          	if("true".equalsIgnoreCase(asyncCombo.getText())) {
          		activity.setAsynchronous(true);
          	} else {
          		activity.setAsynchronous(false);
          	}
          	
            if("false".equalsIgnoreCase(exclusiveCombo.getText())) {
              activity.setNotExclusive(true);
            } else {
              activity.setNotExclusive(false);
            }
          }
        }
      }, editingDomain, "Model Update");
    }
  };

}
