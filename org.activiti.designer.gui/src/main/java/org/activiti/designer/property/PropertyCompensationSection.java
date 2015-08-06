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

public class PropertyCompensationSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
  
  protected Button compensationButton;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    compensationButton = createCheckboxControl("Is for compensation?");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    Activity activity = (Activity) businessObject;
    if (control == compensationButton) {
      return activity.isForCompensation();
    } 
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    Activity activity = (Activity) businessObject;
    if (control == compensationButton) {
      activity.setForCompensation(compensationButton.getSelection());
    }
  }
}
