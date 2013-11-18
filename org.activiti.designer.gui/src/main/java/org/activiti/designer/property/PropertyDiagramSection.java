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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyDiagramSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  protected Text idText;
  protected Text nameText;
  protected Text namespaceText;
  protected Text documentationText;
  protected Text candidateStarterUsersText;
  protected Text candidateStarterGroupsText;
	
  protected BpmnMemoryModel model = null;
	protected Process currentProcess = null;
	
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
  public void refresh() {
    Object businessObject = getBusinessObject(getSelectedPictogramElement());
    
    model = getModel(getSelectedPictogramElement());
    if (businessObject instanceof Process) {
      currentProcess = (Process) businessObject;
      if (model.getBpmnModel().getPools().size() > 0) {
        setEnabled(false);
      } else {
        setEnabled(true);
      }
      
    } else if (businessObject instanceof Pool) {
      Pool pool = (Pool) businessObject;
      currentProcess = model.getBpmnModel().getProcess(pool.getId());
      setEnabled(true);
    }
    
    super.refresh();
  }



  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    if (control == idText) {
      return currentProcess.getId();
      
    } else if (control == nameText) {
      return currentProcess.getName();
    
    } else if (control == namespaceText) {
      if (StringUtils.isNotEmpty(model.getBpmnModel().getTargetNamespace())) {
        return model.getBpmnModel().getTargetNamespace();
      } else {
        return "http://www.activiti.org/test";
      }
      
    } else if (control == candidateStarterUsersText) {
      return getCommaSeperatedString(currentProcess.getCandidateStarterUsers());
    
    } else if (control == candidateStarterGroupsText) {
      return getCommaSeperatedString(currentProcess.getCandidateStarterGroups());
      
    } else if (control == documentationText) {
      return currentProcess.getDocumentation();
    }
    
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, final Object businessObject) {
    if (control == idText) {
      currentProcess.setId(idText.getText());
      if (businessObject instanceof Pool) {
        Pool pool = (Pool) businessObject;
        pool.setProcessRef(currentProcess.getId());
      } else if (businessObject instanceof Process) {
        ((Process) businessObject).setId(currentProcess.getId());
      }
      
    } else if (control == nameText) {
      currentProcess.setName(nameText.getText());
      if (businessObject instanceof Process) {
        ((Process) businessObject).setName(currentProcess.getName());
      }
    
    } else if (control == namespaceText) {
      model.getBpmnModel().setTargetNamespace(namespaceText.getText());
    
    } else if (control == candidateStarterUsersText) {
      currentProcess.setCandidateStarterUsers(commaSeperatedStringToList(candidateStarterUsersText.getText()));
      if (businessObject instanceof Process) {
        ((Process) businessObject).setCandidateStarterUsers(currentProcess.getCandidateStarterUsers());
      }
    
    } else if (control == candidateStarterGroupsText) {
      currentProcess.setCandidateStarterGroups(commaSeperatedStringToList(candidateStarterGroupsText.getText()));
      if (businessObject instanceof Process) {
        ((Process) businessObject).setCandidateStarterGroups(currentProcess.getCandidateStarterGroups());
      }
    
    } else if (control == documentationText) {
      currentProcess.setDocumentation(documentationText.getText());
      if (businessObject instanceof Process) {
        ((Process) businessObject).setDocumentation(currentProcess.getDocumentation());
      }
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
