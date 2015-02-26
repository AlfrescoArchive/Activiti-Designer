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
package org.activiti.designer.eclipse.navigator.cloudrepo.sync;

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.eclipse.navigator.cloudrepo.ActivitiCloudEditorException;
import org.activiti.designer.eclipse.navigator.cloudrepo.ActivitiCloudEditorSameContentException;
import org.activiti.designer.eclipse.navigator.cloudrepo.ActivitiCloudEditorUtil;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Tijs Rademakers
 */
public class SyncUtil {
	
	public static void startUploadNewVersionBackgroundJob(final Shell shell, final String modelId, 
			final String targetFileName, final IFile sourceFile) {
		
	  Job uploadJob = new Job("Uploading file") {

	  	protected IStatus run(IProgressMonitor monitor) {
	  		try {
	  		  JsonNode modelNode = ActivitiCloudEditorUtil.uploadNewVersion(modelId, sourceFile.getName(), IOUtils.toByteArray(sourceFile.getContents()));
	  		  if (modelNode != null && modelNode.get("id") != null) {
	  		    ActivitiCloudEditorUtil.downloadProcessModel(modelNode.get("id").asText(), sourceFile);
	  		    showSuccessMessage(shell);
	  		  
	  		  } else {
	  		    showErrorMessage(shell);
	  		  }
	  		  
	  		} catch (ActivitiCloudEditorSameContentException e) {
          showCloudEditorErrorMessage(shell, "Already in-sync", e);
          
	  		} catch (ActivitiCloudEditorException e) {
          showCloudEditorErrorMessage(shell, "Process upload failed", e);
          
	      } catch (Exception e) {
	      	Logger.logError(e);
	      	return Status.CANCEL_STATUS;
	      }
	  		return Status.OK_STATUS;
	  	}
	  };
	  uploadJob.setUser(true);
	  uploadJob.schedule();
  }
	
	public static void startImportModelBackgroundJob(final Shell shell, final String targetFileName, final IFile sourceFile) {
    
    Job uploadJob = new Job("Importing file") {

      protected IStatus run(IProgressMonitor monitor) {
        try {
          JsonNode modelNode = ActivitiCloudEditorUtil.importModel(sourceFile.getName(), IOUtils.toByteArray(sourceFile.getContents()));
          if (modelNode != null && modelNode.get("id") != null) {
            ActivitiCloudEditorUtil.downloadProcessModel(modelNode.get("id").asText(), sourceFile);
            showSuccessImportMessage(shell);
          
          } else {
            showErrorImportMessage(shell);
          }
       
        } catch (ActivitiCloudEditorSameContentException e) {
          showCloudEditorErrorMessage(shell, "Already in-sync", e);
          
        } catch (ActivitiCloudEditorException e) {
          showCloudEditorErrorMessage(shell, "Process import failed", e);
          
        } catch (Exception e) {
          Logger.logError(e);
          return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
      }
    };
    uploadJob.setUser(true);
    uploadJob.schedule();
  }
	
	public static void startDownloadLatestVersionBackgroundJob(final Shell shell, final String modelId, final IFile sourceFile) {
    
    Job uploadJob = new Job("Downloading file") {

      protected IStatus run(IProgressMonitor monitor) {
        try {
          ActivitiCloudEditorUtil.downloadProcessModel(modelId, sourceFile);
          showSuccessDownloadMessage(shell);
          
        } catch (ActivitiCloudEditorSameContentException e) {
          showCloudEditorErrorMessage(shell, "Already in-sync", e);
          
        } catch (ActivitiCloudEditorException e) {
          showCloudEditorErrorMessage(shell, "Download failed", e);
       
        } catch (Exception e) {
          Logger.logError(e);
          return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
      }
    };
    uploadJob.setUser(true);
    uploadJob.schedule();
  }
	
	protected static void showCloudEditorErrorMessage(final Shell shell, final String title, final ActivitiCloudEditorException exception) {
    Display.getDefault().syncExec(new Runnable() {
      public void run() {
        String detailMessage = null;
        if (exception.getExceptionNode() != null) {
          detailMessage = exception.getExceptionNode().get("message").asText();
        } else {
          detailMessage = exception.getMessage();
        }
        MessageDialog.openInformation(shell, title, detailMessage);
      }
    });
  }

	protected static void showSuccessMessage(final Shell shell) {
    Display.getDefault().syncExec(new Runnable() {
    	public void run() {
    	  MessageDialog.openInformation(shell, "Process upload successful", 
        		"New version of process is stored in the Editor repository");
    	}
    });
  }
	
	protected static void showErrorMessage(final Shell shell) {
    Display.getDefault().syncExec(new Runnable() {
      public void run() {
        MessageDialog.openInformation(shell, "Process upload failed", 
            "The upload failed, please try again");
      }
    });
  }
	
	protected static void showSuccessImportMessage(final Shell shell) {
    Display.getDefault().syncExec(new Runnable() {
      public void run() {
        MessageDialog.openInformation(shell, "Process import successful", 
            "Process model is stored in the Editor repository");
      }
    });
  }
  
  protected static void showErrorImportMessage(final Shell shell) {
    Display.getDefault().syncExec(new Runnable() {
      public void run() {
        MessageDialog.openInformation(shell, "Process import failed", 
            "The import failed, please try again");
      }
    });
  }
	
	protected static void showSuccessDownloadMessage(final Shell shell) {
    Display.getDefault().syncExec(new Runnable() {
      public void run() {
        MessageDialog.openInformation(shell, "Process download successful", 
            "The latest version of the process model has been downloaded from the repository");
      }
    });
  }
}
