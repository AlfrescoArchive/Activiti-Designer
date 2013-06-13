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

import org.activiti.bpmn.model.Activity;
import org.activiti.designer.util.ActivitiConstants;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * Adds the ability to modify Asynchronous and Exclusiveness of an activity to the "General"
 * property section.
 *
 * This will simply add two check boxes that define whether the activity is either asynchronous
 * and/or exclusive.
 */
public class PropertyAsyncSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Composite composite;

  private Button asyncButton;
  private CLabel asyncLabel;

  private Button exclusiveButton;
  private CLabel exclusiveLabel;

  /** The selection listener for both check boxes */
  private SelectionListener buttonSelected = new SelectionListener() {

    @Override
    public void widgetSelected(final SelectionEvent event) {
      final PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        final Object bo = getBusinessObject(pe);
        final IDiagramContainer de = getDiagramEditor();
        final TransactionalEditingDomain ted = de.getDiagramBehavior().getEditingDomain();

        ActivitiUiUtil.runModelChange(new Runnable() {

          @Override
          public void run() {
            updateButtonStates(bo, false);
          }
        }, ted, ActivitiConstants.DEFAULT_MODEL_CHANGE_TEXT);
      }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
      widgetSelected(event);
    }

  };

  /**
   * Updates the state of either the business object or the buttons in the properties section.
   *
   * @param bo the business object (must be an activity, otherwise nothing will occur here)
   * @param reverse if <code>true</code> the buttons are updated, otherwise the business object.
   */
  private void updateButtonStates(final Object bo, boolean reverse) {
    if (bo instanceof Activity) {
      final Activity activity = (Activity) bo;

      if (reverse) {
        asyncButton.setSelection(activity.isAsynchronous());
        exclusiveButton.setSelection(!activity.isNotExclusive());
      } else {
        activity.setAsynchronous(asyncButton.getSelection());
        activity.setNotExclusive(!exclusiveButton.getSelection());
      }
    }
  }

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    composite = factory.createFlatFormComposite(parent);
    FormData data;

    asyncButton = getWidgetFactory().createButton(composite, null, SWT.CHECK);

    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(50, 0);
    data.top = new FormAttachment(0, VSPACE);
    asyncButton.setLayoutData(data);
    asyncButton.addSelectionListener(buttonSelected);

    asyncLabel = getWidgetFactory().createCLabel(composite, "Asynchronous:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(asyncButton, -HSPACE);
    data.top = new FormAttachment(asyncButton, 0, SWT.CENTER);
    asyncLabel.setLayoutData(data);

    exclusiveButton = getWidgetFactory().createButton(composite, null, SWT.CHECK);
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(50, 0);
    data.top = new FormAttachment(asyncButton, VSPACE);
    exclusiveButton.setLayoutData(data);
    exclusiveButton.addSelectionListener(buttonSelected);

    exclusiveLabel = getWidgetFactory().createCLabel(composite, "Exclusive:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(exclusiveButton, -HSPACE);
    data.top = new FormAttachment(exclusiveButton, 0, SWT.CENTER);
    exclusiveLabel.setLayoutData(data);
  }

  @Override
  public void refresh() {
    // remove the listener from both fields
    asyncButton.removeSelectionListener(buttonSelected);
    exclusiveButton.removeSelectionListener(buttonSelected);

    final PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      updateButtonStates(getBusinessObject(pe), true);
    }

    // again add the listeners
    asyncButton.addSelectionListener(buttonSelected);
    exclusiveButton.addSelectionListener(buttonSelected);
  }
}
