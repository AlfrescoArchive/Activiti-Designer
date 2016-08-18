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
package org.activiti.designer.kickstart.util.widget;

import java.util.List;
import java.util.Map;

import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IMessage;

public class PropertySelectionDialog extends TitleAreaDialog {

  protected Map<String, List<FormPropertyDefinition>> properties;
  protected TreeViewer tree;
  protected Image formImage;
  
  protected FormPropertyDefinition selectedProperty;

  public PropertySelectionDialog(Shell parentShell, Map<String, List<FormPropertyDefinition>> properties, Image formImage) {
    super(parentShell);
    this.properties = properties;
    this.formImage = formImage;
  }
  
  @Override
  protected Control createDialogArea(Composite parent) {
    setTitle("Select property");
    setMessage("Select a property from a form used in the process", IMessage.INFORMATION);
    
    Composite area =  (Composite) super.createDialogArea(parent);
    
    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));

    GridLayout layout = new GridLayout(1, false);
    layout.verticalSpacing = 0;
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    container.setLayout(layout);
    
    tree = new TreeViewer(container, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
    tree.setContentProvider(new PropertyReferenceTreeProvider());
    tree.setInput(properties);
    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.heightHint = 300;
    
    tree.getTree().setLayoutData(data);
    tree.setLabelProvider(new FormPropertyDefinitionLabelProvider(formImage));
    
    tree.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = tree.getSelection();
        if(selection != null && !selection.isEmpty()) {
          Object selected = ((IStructuredSelection) selection).getFirstElement();
          if(selected instanceof FormPropertyDefinition) {
            selectedProperty = (FormPropertyDefinition) selected;
          } else {
            selectedProperty = null;
          }
        } else {
          selectedProperty = null;
        }
        getButton(OK).setEnabled(selectedProperty != null);
      }
    });
    
    tree.addDoubleClickListener(new IDoubleClickListener() {
      @Override
      public void doubleClick(DoubleClickEvent event) {
        if(selectedProperty != null) {
          okPressed();
        }
      }
    });
    return area;
  }
  
  public FormPropertyDefinition getSelectedProperty() {
    return selectedProperty;
  }
  
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    super.createButtonsForButtonBar(parent);
    
    getButton(OK).setEnabled(false);
  }
  
  private class PropertyReferenceTreeProvider implements ITreeContentProvider {

    @Override
    public void dispose() {
      // nothing to do here
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // nothing to do here, viewer will re-fetch children 
    }

    @Override
    public Object[] getElements(Object inputElement) {
      return properties.keySet().toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
      List<FormPropertyDefinition> children = properties.get(parentElement);
      if(children != null) {
        return children.toArray();
      }
      return new Object[] {};
    }

    @Override
    public Object getParent(Object element) {
      return null;
    }

    @Override
    public boolean hasChildren(Object element) {
      List<FormPropertyDefinition> children = properties.get(element);
      if(children != null) {
        return !children.isEmpty();
      }
      return false;
    }
  }

}
