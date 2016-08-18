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
package org.activiti.designer.kickstart.eclipse.navigator.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.activiti.designer.kickstart.eclipse.common.KickstartPlugin;
import org.activiti.designer.kickstart.eclipse.editor.KickstartProcessDiagramCreator;
import org.activiti.designer.kickstart.eclipse.navigator.CmisNavigatorSelectionHolder;
import org.activiti.designer.kickstart.eclipse.navigator.CmisUtil;
import org.activiti.designer.kickstart.eclipse.sync.SyncConstants;
import org.activiti.designer.kickstart.eclipse.util.FileService;
import org.activiti.designer.kickstart.util.KickstartConstants;
import org.activiti.workflow.simple.alfresco.conversion.json.AlfrescoSimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.definition.AbstractConditionStepListContainer;
import org.activiti.workflow.simple.definition.AbstractStepListContainer;
import org.activiti.workflow.simple.definition.FormStepDefinition;
import org.activiti.workflow.simple.definition.ListConditionStepDefinition;
import org.activiti.workflow.simple.definition.ListStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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
	    
    List<CmisObject> selectedObjects = new ArrayList<CmisObject>(CmisNavigatorSelectionHolder.getInstance().getSelectedObjects()); // need to clone list to avoid concurrent modification
		for (CmisObject cmisObject : selectedObjects) {
			if (cmisObject instanceof Document) {
				final Document document = (Document) cmisObject;
 			
				try {
			    IFolder tempzipFolder = containerToDownloadTo.getProject().getFolder("tempzip");
          if (tempzipFolder.exists()) {
            tempzipFolder.delete(true, monitor);
          }
          
          tempzipFolder.create(true, true, monitor);
          
          IFile file = tempzipFolder.getFile(new Path(document.getName()));
		      file.create(CmisUtil.downloadDocument(document), true, null);
		      
		      IFile openFile = null;
		      byte[] buffer = new byte[1024];
		      if ("zip".equalsIgnoreCase(file.getFileExtension())) {
		        final ZipInputStream zis = new ZipInputStream(new FileInputStream(file.getLocation().toFile()));
		        boolean hasMoreEntries = true;
		        IFile processFile = null;
		        while (hasMoreEntries) {
		          ZipEntry entry = zis.getNextEntry();
		          if (entry != null) {
		            IFile unzippedFile = tempzipFolder.getFile(entry.getName());
		            if ("kickproc".equalsIgnoreCase(unzippedFile.getFileExtension())) {
		              processFile = unzippedFile;
		            }
		            String filePath = unzippedFile.getLocationURI().getPath();
		            File extractFile = new File(filePath);
		            FileOutputStream fos = new FileOutputStream(extractFile);
		            int len;
		            while ((len = zis.read(buffer)) > 0) {
		              fos.write(buffer, 0, len);
		            }
		 
		            fos.close(); 
		          } else {
		            hasMoreEntries = false;
		          }
		        }
		        zis.close();
		        
		        tempzipFolder.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		        if (processFile != null) {
		          openFile = processWorkflowDefinition(processFile, tempzipFolder, document);
		        }
		      }
		      
		      tempzipFolder.delete(true, monitor);
		      containerToDownloadTo.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		      
		      if (openFile != null) {
		        IPath path = openFile.getFullPath();
  		            
            // get or create the corresponding temporary folder
            final IFolder tempFolder = FileService.getOrCreateTempFolder(path);

            // finally get the diagram file that corresponds to the data file
            final IFile diagramFile = FileService.getTemporaryDiagramFile(path, tempFolder);
            
            KickstartProcessDiagramCreator creator = new KickstartProcessDiagramCreator();
            creator.creatProcessDiagram(openFile, diagramFile, null, null, true);
		      }
  		      
		    } catch (Exception e1) {
		      e1.printStackTrace();
		      return Status.CANCEL_STATUS;
		    }
			}
		}
    
    return Status.OK_STATUS;
	}
	
	protected IFile processWorkflowDefinition(IFile sourceFile, IFolder unzippedFolder, Document document) throws Exception {
	  
	  if (sourceFile.getProject().findMember(sourceFile.getName()) != null) {
	    sourceFile.getProject().findMember(sourceFile.getName()).delete(true, new NullProgressMonitor());
    }
    unzippedFolder.getFile(sourceFile.getName()).copy(sourceFile.getProject().getFullPath().append(sourceFile.getName()), true, new NullProgressMonitor());
    IFile newProcessFile = sourceFile.getProject().getFile(sourceFile.getName());
    
	  String filePath = newProcessFile.getLocationURI().getPath();
    File processFile = new File(filePath);
    FileInputStream fileStream = new FileInputStream(processFile);
    AlfrescoSimpleWorkflowJsonConverter converter = new AlfrescoSimpleWorkflowJsonConverter();
    WorkflowDefinition definition = null;
    try {
      definition = converter.readWorkflowDefinition(fileStream);
    } catch (final Exception e) {
      definition = new WorkflowDefinition();
      Display.getDefault().syncExec(new Runnable() {
        public void run() {
          Status errorStatus = null;
          if (e.getCause() != null) {
            errorStatus = new Status(IStatus.ERROR, KickstartPlugin.PLUGIN_ID, e.getCause().getMessage());
          } else {
            errorStatus = new Status(IStatus.ERROR, KickstartPlugin.PLUGIN_ID, e.getMessage());
          }
          ErrorDialog.openError(shell, "Error", "An error occured while reading kickstart process file.", errorStatus);
        }
      });
      return null;
    }
    
    if (definition.getParameters().containsKey(KickstartConstants.PARAMETER_FORM_REFERENCE)) {
      String startFormPath = (String) definition.getParameters().get(KickstartConstants.PARAMETER_FORM_REFERENCE);
      IFile startFormFile = sourceFile.getProject().getFile(new Path(startFormPath));
      if (unzippedFolder.getFile(startFormFile.getName()) != null) {
        IContainer newFolder = makeDirs(startFormPath, sourceFile.getProject());
        if (newFolder.findMember(startFormFile.getName()) != null) {
          newFolder.findMember(startFormFile.getName()).delete(true, new NullProgressMonitor());
        }
        unzippedFolder.getFile(startFormFile.getName()).copy(newFolder.getFullPath().append(startFormFile.getName()), true, new NullProgressMonitor());
      }
    }
    
    walkthroughForms(definition.getSteps(), unzippedFolder);
    
    // Update the JSON node location
    definition.getParameters().put(SyncConstants.REPOSITORY_NODE_ID, document.getId());
    definition.getParameters().put(SyncConstants.VERSION, document.getVersionLabel());
    
    // Write
    FileWriter writer = new FileWriter(new File(newProcessFile.getLocationURI().getPath()));
    converter.writeWorkflowDefinition(definition, writer);
    newProcessFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
    
    return newProcessFile;
	}
	
	protected void walkthroughForms(List<StepDefinition> stepList, IFolder unzippedFolder) throws Exception {
    for (StepDefinition step : stepList) {
      if (step instanceof FormStepDefinition) {
        FormStepDefinition formStep = (FormStepDefinition) step;

        if (formStep.getParameters().containsKey(KickstartConstants.PARAMETER_FORM_REFERENCE)) {
          String formPath = (String) formStep.getParameters().get(KickstartConstants.PARAMETER_FORM_REFERENCE);
          IFile formFile = unzippedFolder.getProject().getFile(new Path(formPath));
          if (unzippedFolder.getFile(formFile.getName()) != null) {
            IContainer newFolder = makeDirs(formPath, formFile.getProject());
            if (newFolder.findMember(formFile.getName()) != null) {
              newFolder.findMember(formFile.getName()).delete(true, new NullProgressMonitor());
            }
            unzippedFolder.getFile(formFile.getName()).copy(newFolder.getFullPath().append(formFile.getName()), true, new NullProgressMonitor());
          }
        }
      } else if (step instanceof AbstractStepListContainer<?>) {
        List<?> childList = ((AbstractStepListContainer<?>) step).getStepList();
        for (Object object : childList) {
          if (object instanceof ListStepDefinition<?>) {
            walkthroughForms(((ListStepDefinition<?>) object).getSteps(), unzippedFolder);
          }
        }
      
      } else if (step instanceof AbstractConditionStepListContainer<?>) {
        List<?> childList = ((AbstractConditionStepListContainer<?>) step).getStepList();
        for (Object object : childList) {
          if (object instanceof ListConditionStepDefinition<?>) {
            walkthroughForms(((ListConditionStepDefinition<?>) object).getSteps(), unzippedFolder);
          }
        }
      }
    }
  }
	
	protected IContainer makeDirs(String filePath, IProject project) throws Exception {
	  String[] folders = filePath.split("/");
	  if (folders.length == 1) {
	    return project;
	  } else {
  	  IFolder newFolder = project.getFolder(folders[0]);
  	  if (newFolder.exists() == false) {
  	    newFolder.create(true, true, new NullProgressMonitor());
  	  }
  	  for (int i = 1; i < folders.length - 1; i++) {
        IFolder childFolder = newFolder.getFolder(folders[i]);
        if (childFolder.exists() == false) {
          childFolder.create(true, true, new NullProgressMonitor());
        }
        newFolder = childFolder;
      }
  	  return newFolder;
	  }
	}
}
