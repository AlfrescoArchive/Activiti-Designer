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
package org.activiti.designer.eclipse.navigator.cloudrepo.dialog;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.eclipse.editor.Bpmn2DiagramCreator;
import org.activiti.designer.eclipse.navigator.cloudrepo.ActivitiCloudEditorException;
import org.activiti.designer.eclipse.navigator.cloudrepo.ActivitiCloudEditorNavigatorSelectionHolder;
import org.activiti.designer.eclipse.navigator.cloudrepo.ActivitiCloudEditorSameContentException;
import org.activiti.designer.eclipse.navigator.cloudrepo.ActivitiCloudEditorUtil;
import org.activiti.designer.eclipse.util.FileService;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author jbarrez
 * @author Tijs Rademakers
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
	    
    List<JsonNode> selectedObjects = new ArrayList<JsonNode>(ActivitiCloudEditorNavigatorSelectionHolder.getInstance().getSelectedObjects()); // need to clone list to avoid concurrent modification
		for (JsonNode modelNode : selectedObjects) {
			try {
        IFile file = containerToDownloadTo.getProject().getFile(new Path(modelNode.get("name").asText() + ".bpmn"));
	      ActivitiCloudEditorUtil.downloadProcessModel(modelNode.get("id").asText(), file);
	      
	      containerToDownloadTo.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	      
	      if (file != null) {
	        IPath path = file.getFullPath();
		            
          // get or create the corresponding temporary folder
          final IFolder tempFolder = FileService.getOrCreateTempFolder(path);

          // finally get the diagram file that corresponds to the data file
          final IFile diagramFile = FileService.getTemporaryDiagramFile(path, tempFolder);
          
          Bpmn2DiagramCreator creator = new Bpmn2DiagramCreator();
          creator.createBpmnDiagram(file, diagramFile, null, null, true);
	      }
	      
			} catch (ActivitiCloudEditorSameContentException e) {
        showCloudEditorErrorMessage(shell, "Already in-sync", e);
        
			} catch (final ActivitiCloudEditorException e) {
			  showCloudEditorErrorMessage(shell, "Download failed", e);
		      
	    } catch (Exception e) {
	      Logger.logError("Error downloading model " + modelNode, e);
	      return Status.CANCEL_STATUS;
	    }
		}
    
    return Status.OK_STATUS;
	}
	
	protected void showCloudEditorErrorMessage(final Shell shell, final String title, final ActivitiCloudEditorException exception) {
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
}
