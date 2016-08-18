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

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.kickstart.process.Activator;
import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.activiti.designer.kickstart.process.dialog.KickstartFormReferenceSelect;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * Wrapper around a control that displays a referenced for link and allows selecting a reference.
 * 
 * @author Frederik Heremans
 */
public class FormReferenceViewer {

  protected SelectionListener selectionListener;
  protected IProject project;
  
  protected Composite composite;
  protected Link formReferenceLink;
  protected Button selectFormButton;
  protected Button clearSelectionButton;
  protected Label iconLabel;
  protected IFile formResource;
  
  public FormReferenceViewer(Composite parent, SelectionListener listener, IProject theProject) {
    this.selectionListener = listener;
    this.project = theProject;
    
    composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(4, false));
    composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    
    iconLabel = new Label(composite, SWT.ICON);
    iconLabel.setImage(Activator.getImage(KickstartProcessPluginImage.FORM_ICON));
    iconLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    
    formReferenceLink = new Link(composite, SWT.NONE);
    formReferenceLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    formReferenceLink.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    
    selectFormButton = new Button(composite, SWT.PUSH);
    selectFormButton.setText("Select or create form...");
    selectFormButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    
    clearSelectionButton = new Button(composite, SWT.PUSH);
    clearSelectionButton.setText("Clear");
    clearSelectionButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    
    clearSelectionButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if(selectionListener != null) {
          setReferencedForm(null);
          selectionListener.widgetSelected(e);
        }
      }
    });
    
    selectFormButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if(project != null) {
          KickstartFormReferenceSelect formSelect = new KickstartFormReferenceSelect(composite.getShell(), project);
          formSelect.setBlockOnOpen(true);
          formSelect.setSelectedFile(formResource);
          int open = formSelect.open();
          setReferencedForm(formSelect.getSelectedFormFile());
          
          if(selectionListener != null && open == Window.OK && formSelect.getSelectedFormFile() != null) {
            selectionListener.widgetSelected(e);
          }
        }
      }
    });
    
    formReferenceLink.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if(formResource != null) {
          if(formResource.exists()) {
            final IWorkbench workbench = PlatformUI.getWorkbench();
            workbench.getDisplay().syncExec(new Runnable() {
              
              @Override
              public void run() {
                try {
                  IDE.openEditor(workbench.getActiveWorkbenchWindow().getActivePage(), formResource, "org.activiti.designer.kickstart.editor.formEditor");
                } catch (PartInitException exception) {
                  Logger.logError("Error while opening referenced form editor", exception);
                }
              }
            });
          } else {
            final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID
                , "The referenced form does not exist: " + formResource.getProjectRelativePath());
            ErrorDialog.openError(composite.getShell(), "Unable to open form", "The form cannot be opened",
                status);
          }
        }
      }
    });
    
  }
  
  public Composite getComposite() {
    return composite;
  }

  public void setReferencedFormPath(String path) {
    if(path == null) {
      setReferencedForm(null);
    } else {
      // Get file
      IPath fromPortableString = Path.fromPortableString(path);
      setReferencedForm(project.getFile(fromPortableString));
    }
  }
  
  public String getReferencedFormPath() {
    if(formResource != null) {
      return formResource.getProjectRelativePath().toPortableString();
    }
    return null;
  }
  
  public void setProject(IProject project) {
    this.project = project;
  }
  
  protected void setReferencedForm(IFile file) {
    this.formResource = file;
    if(file == null) {
      formReferenceLink.setText("No form selected");
    } else {
      formReferenceLink.setText("<A>" + formResource.getName() + "</A>");
    }
  }
}
