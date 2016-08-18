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
/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.kickstart.process.dialog;

import org.activiti.designer.eclipse.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.wizards.IWizardDescriptor;

/**
 * A dialog that allows selecting a Kickstart Form file from within a project.
 * 
 * @author Frederik Heremans
 */
public class KickstartFormReferenceSelect extends TitleAreaDialog {

  public static final String NEW_FORM_WIZARD_ID = "org.activiti.designer.kickstart.eclipse.ui.wizard.diagram.CreateKickstartFormWizard";
  
  protected IFile selectedFormFile;
  protected IResource selectedParent;
  protected IProject project;

  protected TreeViewer treeViewer;

  public KickstartFormReferenceSelect(Shell parentShell, IProject project) {
    super(parentShell);
    this.project = project;
  }

  @Override
  public void create() {
    super.create();
    setTitle("Select or create form");
    setMessage("Select a form that should be used or create a new one", IMessageProvider.INFORMATION);
  }
  
  public IFile getSelectedFormFile() {
    return selectedFormFile;
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite area = (Composite) super.createDialogArea(parent);
    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));

    GridLayout layout = new GridLayout(1, false);
    layout.verticalSpacing = 0;
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    container.setLayout(layout);
    
    Composite topComposite = new Composite(container, SWT.NONE);
    GridLayout topLayout = new GridLayout(2, false);
    topLayout.marginTop = 0;
    topLayout.marginBottom = 0;
    
    topComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    topComposite.setLayout(topLayout);
    
    Label helpText = new Label(topComposite, SWT.NONE);
    helpText.setText("Available forms:");
    helpText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
    
    // Create add form button
    Button addNewFormButton = new Button(topComposite, SWT.PUSH);
    addNewFormButton.setText("Create new form");
    addNewFormButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    
    // Project tree viewer
    treeViewer = new TreeViewer(container, SWT.SINGLE | SWT.BORDER);
    treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
    treeViewer.setContentProvider(new KickstartFormsTreeContentProvider(project));
    treeViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
    treeViewer.setInput(ResourcesPlugin.getWorkspace());

    // Selection listener for folders
    treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        TreeSelection selection = (TreeSelection) event.getSelection();
        Object[] selectedElements = selection.toArray();
        
        if (selectedElements != null && selectedElements.length > 0) {
          updateSelection(selectedElements[0]);
        }
      }
    });
    
    treeViewer.addDoubleClickListener(new IDoubleClickListener() {
      @Override
      public void doubleClick(DoubleClickEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        if(selection != null && !selection.isEmpty()) {
          updateSelection(selection.getFirstElement());
          if(selectedFormFile != null) {
            okPressed();
          }
        }
      }
    });
    
    // Add listener to open wizard to create new form
    addNewFormButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        IWizardDescriptor descriptor = PlatformUI.getWorkbench()
            .getNewWizardRegistry().findWizard(NEW_FORM_WIZARD_ID);
        
        if(descriptor != null) {
          try {
            final INewWizard createWizard = (INewWizard) descriptor.createWizard();
            IStructuredSelection structuredSelection = null;
            
            if(selectedParent != null) {
              structuredSelection = new StructuredSelection(selectedParent);
            } else {
              structuredSelection = new StructuredSelection();
            }
            
            // Initialize with the selected folder in the tree, if any
            createWizard.init(PlatformUI.getWorkbench(), structuredSelection);
            
            WizardDialog dialog = new WizardDialog(getShell(), createWizard);
            dialog.setBlockOnOpen(true);
            dialog.open();
            
            IFile file = null;
            if(createWizard instanceof IAdaptable) {
              file = (IFile) ((IAdaptable) createWizard).getAdapter(IFile.class);
            }

            if(file != null) {
              updateSelection(file);
              okPressed();
            } else {
              // Best effort to show location of newly created file
              if(selectedParent != null) {
                treeViewer.refresh(selectedParent);
                treeViewer.setSelection(new StructuredSelection(selectedFormFile), true);
              } else {
                treeViewer.refresh();
              }
            }
          } catch (CoreException ce) {
            Logger.logError("Error while creating new form", ce);
          }
        }
      }
    });
    
    // Initialize selection, in case a form is already known
    if(selectedFormFile != null) {
      treeViewer.setSelection(new StructuredSelection(selectedFormFile));
    }
    return area;
  }
  
  protected void updateSelection(Object selection) {
    // Reset current selection
    selectedFormFile = null;
    selectedParent = null;
    
    if (selection != null) {
      if (selection instanceof IFile) {
        selectedFormFile = (IFile) selection;
        selectedParent = selectedFormFile.getParent();
      } else if(selection instanceof IResource){
        selectedParent = (IResource) selection;
      }
    }
    
    Button okButton = getButton(IDialogConstants.OK_ID);
    if (okButton != null) {
      okButton.setEnabled(selectedFormFile != null);
    }
  }
  
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    super.createButtonsForButtonBar(parent);

    // OK button is disabled on creation
    getButton(IDialogConstants.OK_ID).setEnabled(selectedFormFile != null);
  }

  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected Point getInitialSize() {
    return new Point(600, 500);
  }

  public void setSelectedFile(IFile formResource) {
    updateSelection(formResource);
    
    if(formResource != null && treeViewer != null) {
      treeViewer.setSelection(new StructuredSelection(formResource));
    }
  }

}