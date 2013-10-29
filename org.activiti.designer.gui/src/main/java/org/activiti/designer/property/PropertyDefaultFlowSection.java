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
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.SequenceFlow;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyDefaultFlowSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Combo defaultCombo;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    defaultCombo = createCombobox(new String[]{}, 0);
    createLabel("Default flow", defaultCombo);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    FlowNode flowNode = (FlowNode) businessObject;
    if (control == defaultCombo) {
      List<SequenceFlow> flowList = flowNode.getOutgoingFlows();
      
      defaultCombo.removeAll();
          
      for (SequenceFlow flow : flowList) {
        defaultCombo.add(flow.getId());
      }
      
      String defaultFlow = null;
      if (businessObject instanceof Activity) {
        defaultFlow = ((Activity) businessObject).getDefaultFlow();
      } else if (businessObject instanceof Gateway) {
        defaultFlow = ((Gateway) businessObject).getDefaultFlow();
      }
      return defaultFlow; 
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    if (control == defaultCombo) {
      if (businessObject instanceof Activity) {
        ((Activity) businessObject).setDefaultFlow(defaultCombo.getText());
      } else if (businessObject instanceof Gateway) {
        ((Gateway) businessObject).setDefaultFlow(defaultCombo.getText());
      }
    }
  }
}
