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
import org.activiti.designer.kickstart.eclipse.navigator.CmisUtil;
import org.apache.chemistry.opencmis.client.api.Document;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FileExistsDialog extends TitleAreaDialog {
	
	protected IContainer containerToDownloadTo;
	protected Document documentToDownload;
	
	protected Text nameText;
	protected String name;
	
  public FileExistsDialog(Shell parentShell, IContainer containerToDownloadTo, Document documentToDownload) {
    super(parentShell);
    this.containerToDownloadTo = containerToDownloadTo;
    this.documentToDownload = documentToDownload;
  }

  @Override
  public void create() {
    super.create();
    
    setTitle("A file named '" + documentToDownload.getName() + " already exists");
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite area = (Composite) super.createDialogArea(parent);
    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    GridLayout layout = new GridLayout(2, false);
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    container.setLayout(layout);

    createTextField(container);

    return area;
  }

  private void createTextField(Composite container) {
    Label label = new Label(container, SWT.NONE);
    label.setText("Name");

    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;

    nameText = new Text(container, SWT.BORDER);
    nameText.setLayoutData(gridData);
    if (documentToDownload != null && documentToDownload.getName() != null) {
    	nameText.setText(documentToDownload.getName());
    }
    
    // Update OK button state on text changes
    nameText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				IFile file = containerToDownloadTo.getFile(new Path(nameText.getText()));
				getButton(IDialogConstants.OK_ID).setEnabled(!file.exists());
			}
			
		});
  }
  
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    super.createButtonsForButtonBar(parent);
    
    getButton(IDialogConstants.OK_ID).setEnabled(false); // disabled by default
    
    // Click listener for OK button: download files
    getButton(IDialogConstants.OK_ID).addSelectionListener(new SelectionAdapter() {
    	@Override
    	public void widgetSelected(SelectionEvent e) {
  			Job job = new Job("Dowloading " + name) {
					
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						IFile file = containerToDownloadTo.getFile(new Path(name));
						 try {
					      file.create(CmisUtil.downloadDocument(documentToDownload), true, null);
					    } catch (CoreException e1) {
					      e1.printStackTrace();
					      return Status.CANCEL_STATUS;
					    }
						 return Status.OK_STATUS;
					}
				};
  			job.setUser(true);
  			job.schedule();
    	}
		});
  }
  
	private void saveInput() {
		name = nameText.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}
  
  
  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected Point getInitialSize() {
    return new Point(380, 190);
  }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
  
} 