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

import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyDiagramSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text idText;
	private Text nameText;
	private Text namespaceText;
	private Text documentationText;
	private Text candidateStarterUsersText;
	private Text candidateStarterGroupsText;
	
	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    idText = createTextControl(false);
    createLabel("Id", idText);
    nameText = createTextControl(false);
    createLabel("Name", nameText);
    namespaceText = createTextControl(false);
    createLabel("Namespace", namespaceText);
    candidateStarterUsersText = createTextControl(false);
    createLabel("Candidate start users (comma separated)", candidateStarterUsersText);
    candidateStarterGroupsText = createTextControl(false);
    createLabel("Candidate start groups (comma separated)", candidateStarterGroupsText);
    documentationText = createTextControl(true);
    createLabel("Documentation", documentationText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    Process process = null;
    BpmnMemoryModel model = getModel(getSelectedPictogramElement());
    if (getSelectedPictogramElement() instanceof Diagram) {
      if (model.getBpmnModel().getPools().size() > 0) {
        process = model.getBpmnModel().getProcess(model.getBpmnModel().getPools().get(0).getId());
        setEnabled(false);
      } else {
        process = model.getBpmnModel().getMainProcess();
        setEnabled(true);
      }
    
    } else {
      Pool pool = ((Pool) getBusinessObject(getSelectedPictogramElement()));
      process = model.getBpmnModel().getProcess(pool.getId());
      setEnabled(true);
    }
    
    if (control == idText) {
      return process.getId();
      
    } else if (control == nameText) {
      return process.getName();
    
    } else if (control == namespaceText) {
      if (StringUtils.isNotEmpty(model.getBpmnModel().getTargetNamespace())) {
        return model.getBpmnModel().getTargetNamespace();
      } else {
        return "http://www.activiti.org/test";
      }
      
    } else if (control == candidateStarterUsersText) {
      return getCommaSeperatedString(process.getCandidateStarterUsers());
    
    } else if (control == candidateStarterGroupsText) {
      return getCommaSeperatedString(process.getCandidateStarterGroups());
      
    } else if (control == documentationText) {
      return process.getDocumentation();
    }
    
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, final Object businessObject) {
    Process process = null;
    BpmnMemoryModel model = getModel(getSelectedPictogramElement());
    if (getSelectedPictogramElement() instanceof Diagram) {
      if (model.getBpmnModel().getPools().size() > 0) {
        process = model.getBpmnModel().getProcess(model.getBpmnModel().getPools().get(0).getId());
        setEnabled(false);
      } else {
        process = model.getBpmnModel().getMainProcess();
        setEnabled(true);
      }
    
    } else {
      Pool pool = ((Pool) getBusinessObject(getSelectedPictogramElement()));
      process = model.getBpmnModel().getProcess(pool.getId());
      setEnabled(true);
    }
    
    if (control == idText) {
      process.setId(idText.getText());
      
    } else if (control == nameText) {
      process.setName(nameText.getText());
    
    } else if (control == namespaceText) {
      model.getBpmnModel().setTargetNamespace(namespaceText.getText());
    
    } else if (control == candidateStarterUsersText) {
      process.setCandidateStarterUsers(commaSeperatedStringToList(candidateStarterUsersText.getText()));
    
    } else if (control == candidateStarterGroupsText) {
      process.setCandidateStarterGroups(commaSeperatedStringToList(candidateStarterGroupsText.getText()));
    
    } else if (control == documentationText) {
      process.setDocumentation(documentationText.getText());
    }
  }
	
	private void setEnabled(boolean enabled) {
	  idText.setEnabled(enabled);
    nameText.setEnabled(enabled);
    namespaceText.setEnabled(enabled);
    documentationText.setEnabled(enabled);
    candidateStarterUsersText.setEnabled(enabled);
    candidateStarterGroupsText.setEnabled(enabled);
	}
}
