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
import org.activiti.designer.kickstart.eclipse.navigator.CmisNavigatorSelectionHolder;
import org.activiti.designer.kickstart.eclipse.navigator.CmisUtil;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
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

public class RenameDialog extends TitleAreaDialog {
	
	protected CmisObject objectToBeRenamed;
	
	protected Text nameText;
	protected String name;
	
  public RenameDialog(Shell parentShell) {
    super(parentShell);
  }

  @Override
  public void create() {
    super.create();
    
    this.objectToBeRenamed = CmisNavigatorSelectionHolder.getInstance().getSelectedObjects().get(0);
    if (objectToBeRenamed.getName() != null) {
    	nameText.setText(objectToBeRenamed.getName());
    }
    
    String type = ((objectToBeRenamed instanceof Folder) ? " folder" : " file");
    setTitle("Rename " + type);
    setMessage("Fill in the new name of " + type + " '" + objectToBeRenamed.getName() + "'", IMessageProvider.INFORMATION);
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
    label.setText("New Name");

    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;

    nameText = new Text(container, SWT.BORDER);
    nameText.setLayoutData(gridData);
  }
  
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    super.createButtonsForButtonBar(parent);
    
    // Click listener for OK button: download files
    getButton(IDialogConstants.OK_ID).addSelectionListener(new SelectionAdapter() {
    	@Override
    	public void widgetSelected(SelectionEvent e) {
    		final String jobName = "Renaming " + ((objectToBeRenamed instanceof Folder) ? " folder" : " file"); 
    		Job job = new Job(jobName) {
  			  
  			  @Override
  			  protected IStatus run(IProgressMonitor monitor) {
  			    monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
  			    CmisUtil.renameCmisObject(objectToBeRenamed, name);
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