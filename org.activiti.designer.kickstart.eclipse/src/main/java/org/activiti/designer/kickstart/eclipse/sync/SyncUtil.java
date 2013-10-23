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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.activiti.designer.kickstart.eclipse.Logger;
import org.activiti.designer.kickstart.eclipse.common.IoUtils;
import org.activiti.designer.kickstart.eclipse.navigator.CmisUtil;
import org.activiti.workflow.simple.converter.json.SimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author jbarrez
 */
public class SyncUtil {
	
	public static void startProcessSynchronizationBackgroundJob(final Shell shell, final CmisObject destination, 
			final String targetFileName, final boolean warnForExistingFile, final IFile sourceFile) {
		
	  Job uploadJob = new Job("Uploading file") {

	  	protected IStatus run(IProgressMonitor monitor) {
	  		try {
	  			
	  			String nodeId = null;
	  			
	  			// If destination is a folder, upload document to it
	  			if (destination instanceof Folder) {
		  			byte[] content = IoUtils.getBytesFromInputStream(sourceFile.getContents());
						nodeId = CmisUtil.uploadDocumentToFolder((Folder) destination, targetFileName, content);
	  			} else if (destination instanceof Document) { // If destination is a document, create a new version
	  				if (warnForExistingFile) {
	  					showFileExistsDialog(shell, targetFileName, sourceFile, (Document) destination);
	  					return Status.CANCEL_STATUS;
	  				} else {
	  					nodeId = uploadFileToRepository((Document) destination, sourceFile);
	  				}
	  			}
	  			
	  			updateKickstartProcessJson(sourceFile, nodeId);
					showSuccessMessage(shell, destination);
	     
	      } catch (CmisConstraintException cmisConstraintException) {
	      	
	      	// A file already exists: resolve conflict
	      	
	      	Document targetDocument = null;
	      	if (destination instanceof Document) {
	      		targetDocument = (Document) destination;
	      	} else if (destination instanceof Folder) {
	      		Folder destinationFolder = (Folder) destination;
	      		for (CmisObject child : destinationFolder.getChildren()) {
	      			if (child instanceof Document && child.getName().equals(targetFileName)) {
	      				targetDocument = (Document) child;
	      				break;
	      			}
	      		}
	      	}
	      	
	      	// Show a conflict dialog
	      	showFileExistsDialog(shell, targetFileName, sourceFile, targetDocument);
	      	
	      	return Status.CANCEL_STATUS;
	      	
	      } catch (Exception e) {
	      	Logger.logError(e);
	      	return Status.CANCEL_STATUS;
	      }
	  		return Status.OK_STATUS;
	  	}

			private void showFileExistsDialog(final Shell shell,
          final String targetFileName, final IFile sourceFile, final Document d) {
	      Display.getDefault().syncExec(new Runnable() {
	      	public void run() {
	      		FileExistsInFolderDialog fileExistsInFolderDialog = 
	      				new FileExistsInFolderDialog(shell, d, targetFileName, sourceFile);
	      		fileExistsInFolderDialog.open();
	      	}
	      });
      }

	  };
	  uploadJob.setUser(true);
	  uploadJob.schedule();
  }
	

	private static String uploadFileToRepository(final Document document,final IFile sourceFile) throws IOException, CoreException {
    return CmisUtil.uploadNewVersion(document, IoUtils.getBytesFromInputStream(sourceFile.getContents()), "application/zip");
  }
	
	private static void updateKickstartProcessJson(IFile sourceFile, String nodeId) throws CoreException, IOException {

		// Read
		SimpleWorkflowJsonConverter simpleWorkflowJsonConverter = new SimpleWorkflowJsonConverter();
		WorkflowDefinition workflowDefinition = simpleWorkflowJsonConverter.readWorkflowDefinition(sourceFile.getContents());
		
		// Update
		workflowDefinition.getParameters().put(SyncConstants.REPOSITORY_NODE_ID, nodeId);
		
		// Write
		FileWriter writer = new FileWriter(new File(sourceFile.getLocationURI().getPath()));
		simpleWorkflowJsonConverter.writeWorkflowDefinition(workflowDefinition, writer);
		sourceFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	private static void showSuccessMessage(final Shell shell, final CmisObject cmisObject) {
    Display.getDefault().syncExec(new Runnable() {
    	public void run() {
    		String path = (cmisObject instanceof Folder) ? 
    				((Folder) cmisObject).getPath() : ((Document)cmisObject).getParents().get(0).getPath();
    	  MessageDialog.openInformation(shell, "Process upload succesful", 
        		"New version of process is stored at " + path);
    	}
    });
  }

}
