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
package org.activiti.designer.kickstart.eclipse.sync;
import org.apache.chemistry.opencmis.client.api.Document;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.eclipse.ui.PlatformUI;

public class FileExistsInFolderDialog extends TitleAreaDialog {
	
	protected Document destination;
	protected String targetFileName;
	protected IFile sourceFile;
	
	protected boolean defaultSetup;
	protected boolean repoVersionIsHigher;
	
	
  public FileExistsInFolderDialog(Shell parentShell, Document destination, String targetFileName, IFile sourceFile) {
    super(parentShell);
    this.destination = destination;
    this.targetFileName = targetFileName;
    this.sourceFile = sourceFile;
    
    try {
    	defaultSetup = true;
	    String localVersionLabel = SyncUtil.retrieveLocalVersionLabel(sourceFile);
	    if (localVersionLabel != null) {
	    	int versionComparison = SyncUtil.compareVersions(localVersionLabel, destination.getVersionLabel());
	    	if (versionComparison < 0) {
	    		repoVersionIsHigher = true;
	    		defaultSetup = false;
	    	} 
	    }
    } catch (CoreException e) {
    		// just do the default then, is already set
    }
  }

  @Override
  public void create() {
    super.create();
    setTitle("Conflict situation");
    
    if (defaultSetup) {
    	setDefaultMessage();
    } else if (repoVersionIsHigher) {
    	setRepoVersionIsHigherMessage();
    }
  }

	private void setDefaultMessage() {
	  setMessage("No previous synchronization data found. Do you want to upload a new version? " +
    		"This makes the local content the latest version in the repository.");
  }
	
	private void setRepoVersionIsHigherMessage() {
	  setMessage("The file in the repository has been updated by others. What do you want to do?");
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite area = (Composite) super.createDialogArea(parent);
    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    GridLayout layout = new GridLayout(2, false);
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    container.setLayout(layout);
    return area;
  }
  
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
		
		if (defaultSetup) {
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
			Button newVersionButton = createButton(parent, SyncConstants.NEW_VERSION_BUTTON_ID, "Create new version", true);
			newVersionButton.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		SyncUtil.startProcessSynchronizationBackgroundJob(
	    				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
	    				destination, targetFileName, false, true, sourceFile);
	    		close();
	    	}
	  	});
		} else if (repoVersionIsHigher) {
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
			
			Button useLocalContentButton = createButton(parent, SyncConstants.USE_LOCAL_CONTENT_BUTTON_ID, "Copy local version to repository", false);
			useLocalContentButton.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		SyncUtil.startProcessSynchronizationBackgroundJob(
	    				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
	    				destination, targetFileName, false, true, sourceFile);
	    		close();
	    	}
	  	});
			
			Button useRepoContentButton = createButton(parent, SyncConstants.USE_REPO_CONTENT_BUTTON_ID, "Copy repo version to local file", false);
			useRepoContentButton.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		SyncUtil.copyRepoFileToLocalFile(getShell(), sourceFile, destination);
	    		close();
	    	}
	  	});
		}
  }
  
  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected Point getInitialSize() {
    return new Point(780, 200);
  }

} 