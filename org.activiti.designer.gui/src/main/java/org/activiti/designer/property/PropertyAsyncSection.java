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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Adds the ability to modify Asynchronous and Exclusiveness of an activity to the "General"
 * property section.
 *
 * This will simply add two check boxes that define whether the activity is either asynchronous
 * and/or exclusive.
 */
public class PropertyAsyncSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
  
  private Button asyncButton;
  private Button exclusiveButton;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    asyncButton = createCheckboxControl("Asynchronous");
    exclusiveButton = createCheckboxControl("Exclusive");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    Activity activity = (Activity) businessObject;
    if (control == asyncButton) {
      return activity.isAsynchronous();
    } else if(control == exclusiveButton) {
      return !activity.isNotExclusive();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    Activity activity = (Activity) businessObject;
    if (control == asyncButton) {
      activity.setAsynchronous(asyncButton.getSelection());
    } else if (control == exclusiveButton) {
      activity.setNotExclusive(!exclusiveButton.getSelection());
    }
  }
}
