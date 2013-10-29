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
package org.activiti.designer.kickstart.eclipse.navigator.dialog;
import org.activiti.designer.kickstart.eclipse.common.OpenFolderOnDoubleClickListener;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class SelectFolderForDownloadDialog extends TitleAreaDialog {
	
	protected IResource currentlySelectedResource;

  public SelectFolderForDownloadDialog(Shell parentShell) {
    super(parentShell);
  }

  @Override
  public void create() {
    super.create();
    setTitle("Destination folder for the process(es)");
    setMessage("Select a folder to which the selected process(es) will be downloaded.", IMessageProvider.INFORMATION);
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite area = (Composite) super.createDialogArea(parent);
    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    
    GridLayout layout = new GridLayout(2, false);
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    container.setLayout(layout);

    // Project tree viewer
    final TreeViewer projectTreeViewer = new TreeViewer(container, SWT.SINGLE| SWT.H_SCROLL | SWT.V_SCROLL);
    projectTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
    projectTreeViewer.setContentProvider(new FileTreeContentProvider());
    projectTreeViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
    projectTreeViewer.setInput(ResourcesPlugin.getWorkspace());
    
    // Expand current selection on double click
    projectTreeViewer.addDoubleClickListener(new OpenFolderOnDoubleClickListener());
    
    // Selection listener for folders
    projectTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection selection = (TreeSelection) event.getSelection();
				Object[] selectedElements = selection.toArray();
				if (selectedElements != null && selectedElements.length > 0) {
					Object selectedElement = selectedElements[0]; // Tree is single selection
					if (selectedElement instanceof IResource) {
						currentlySelectedResource = (IResource) selectedElement;
						Button okButton = getButton(IDialogConstants.OK_ID); 
						if (okButton != null) {
							okButton.setEnabled(true);
						}
					}
				} else {
					Button okButton = getButton(IDialogConstants.OK_ID); 
					if (okButton != null) {
						okButton.setEnabled(false);
					}
				}
			}
		});
    
    return area;
  }
  

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    super.createButtonsForButtonBar(parent);
    
    // OK button is disabled on creation
    getButton(IDialogConstants.OK_ID).setEnabled(false);
    
    // Click listener for OK button: download files
    getButton(IDialogConstants.OK_ID).addSelectionListener(new SelectionAdapter() {
    	@Override
    	public void widgetSelected(SelectionEvent e) {
    		if (currentlySelectedResource instanceof IContainer) {

    			DownloadCurrentlySelectedFilesJob job = new DownloadCurrentlySelectedFilesJob(getShell(), (IContainer) currentlySelectedResource);
    			job.setUser(true);
    			job.schedule();
    			
    		}
    	}
		});
  }
  
  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected Point getInitialSize() {
    return new Point(600, 500);
  }

} 