/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.kickstart.process.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.kickstart.process.Activator;
import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.activiti.designer.kickstart.util.FormReferenceReader;
import org.activiti.designer.kickstart.util.widget.PropertySelectionDialog;
import org.activiti.workflow.simple.alfresco.conversion.script.PropertyReference;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.activiti.workflow.simple.definition.form.FormDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class PropertyItemBrowser implements StringItemBrowser {

  protected Button browseButton;
  protected String browseLabel;
  protected WorkflowDefinition workflowDefinition;
  protected IProject project;
  protected PropertyItemFilter itemfilter;

  public PropertyItemBrowser() {
    browseLabel = "Use property...";
  }
  
  public void setItemfilter(PropertyItemFilter itemfilter) {
    this.itemfilter = itemfilter;
  }

  public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) {
    this.workflowDefinition = workflowDefinition;
  }

  public void setProject(IProject project) {
    this.project = project;
  }

  @Override
  public Control getBrowserControl(final PropertyChangeListener listener, Composite parent) {
    browseButton = new Button(parent, SWT.PUSH);
    browseButton.setText(browseLabel);
    browseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        showDialog(listener);
      }
    });
    return browseButton;
  }

  public void setBrowseLabel(String browseLabel) {
    this.browseLabel = browseLabel;
    if (browseButton != null) {
      browseButton.setText(browseLabel);
    }
  }

  protected void showDialog(PropertyChangeListener listener) {
    // Fetch all form-properties
    final Map<String, List<FormPropertyDefinition>> properties = new LinkedHashMap<String, List<FormPropertyDefinition>>();
    
    IRunnableWithProgress propertiesRunnable = new IRunnableWithProgress() {
      @Override
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        if(workflowDefinition != null && project != null) {
          FormReferenceReader formReader =  new FormReferenceReader(workflowDefinition, project);
          FormDefinition definition = formReader.getReferenceStartForm();
          if(definition != null) {
            properties.put("Start form", getFlatDefinitionsList(definition));
          }
          
          Map<String, FormDefinition> referencedForms = formReader.getReferencedForms();
          List<FormPropertyDefinition> mathingDefinitions = null;
          for(Entry<String, FormDefinition> entry : referencedForms.entrySet()) {
            mathingDefinitions = getFlatDefinitionsList(entry.getValue());
            if(!mathingDefinitions.isEmpty()) {
              properties.put(entry.getKey(), mathingDefinitions);
            }
          }
        }
      }
    };

    try {
      new ProgressMonitorDialog(browseButton.getShell()).run(false, false, propertiesRunnable);
    } catch (InvocationTargetException e) {
      Logger.logError("Error while fetching form references", e);
    } catch (InterruptedException e) {
      Logger.logError("Error while fetching form references", e);
    }
    
    PropertySelectionDialog dialog = new PropertySelectionDialog(browseButton.getShell(), properties, Activator.getImage(KickstartProcessPluginImage.FORM_ICON));
    dialog.setBlockOnOpen(true);
    dialog.open();
    
    
    if (listener != null && dialog.getSelectedProperty() != null) {
      PropertyReference reference = new PropertyReference(dialog.getSelectedProperty().getName());
      listener.propertyChange(new PropertyChangeEvent(this, "property", null, reference.getPlaceholder()));
    }
  }

  protected List<FormPropertyDefinition> getFlatDefinitionsList(FormDefinition formDefinition) {
    List<FormPropertyDefinition> filteredDefinitions = new ArrayList<FormPropertyDefinition>();
    for(FormPropertyGroup group : formDefinition.getFormGroups()) {
      for(FormPropertyDefinition definition : group.getFormPropertyDefinitions()) {
        if(isValidProperty(definition)) {
          filteredDefinitions.add(definition);
        }
      }
    }
    return filteredDefinitions;
  }

  protected boolean isValidProperty(FormPropertyDefinition definition) {
    if(itemfilter != null) {
      return itemfilter.propertySelectable(definition);
    }
    return true;
  }

  public interface PropertyItemFilter {
    boolean propertySelectable(FormPropertyDefinition definition);
  }
}
