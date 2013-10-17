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

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.kickstart.eclipse.navigator.CmisNavigatorSelectionHolder;
import org.activiti.designer.kickstart.eclipse.navigator.CmisUtil;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author jbarrez
 */
public class DownloadCurrentlySelectedFilesJob extends Job {
	
	protected Shell shell;
	protected IContainer containerToDownloadTo;
	
	public DownloadCurrentlySelectedFilesJob(Shell shell, IContainer containerToDownloadTo) {
	  super("Downloading files");
	  this.shell = shell;
	  this.containerToDownloadTo = containerToDownloadTo;
  }

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		 monitor.beginTask("Downloading files", IProgressMonitor.UNKNOWN);
	    
	    List<CmisObject> selectedObjects = new ArrayList<CmisObject>(CmisNavigatorSelectionHolder.getInstance().getSelectedObjects()); // need to clone list to avoid concurrent modification
			for (CmisObject cmisObject : selectedObjects) {
				if (cmisObject instanceof Document) {
					final Document document = (Document) cmisObject;
					
					IFile file = containerToDownloadTo.getFile(new Path(document.getName()));
   			
					if (!file.exists()) {
						 try {
					      file.create(CmisUtil.downloadDocument(document), true, null);
					    } catch (CoreException e1) {
					      e1.printStackTrace();
					      return Status.CANCEL_STATUS;
					    }
					} else {
						Display.getDefault().syncExec(new Runnable() {
							
							public void run() {
								FileExistsDialog fileExistsDialog = new FileExistsDialog(shell, containerToDownloadTo, document);
								fileExistsDialog.open();
							}
							
						});
					}
   			
				}
			}
	    
	    return Status.OK_STATUS;
	}
	
}
