package org.activiti.designer.kickstart.eclipse.ui.wizard.diagram;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.activiti.designer.kickstart.eclipse.Logger;
import org.activiti.designer.kickstart.eclipse.navigator.CmisUtil;
import org.activiti.designer.kickstart.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.kickstart.util.FormReferenceReader;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoWorkflowDefinitionConversionFactory;
import org.activiti.workflow.simple.alfresco.conversion.json.AlfrescoSimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.converter.WorkflowDefinitionConversion;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class ExportKickstartProcessWizard extends Wizard implements IExportWizard {

  public static final String EXPORT_WIZARD_TITLE = "Export Kickstart Process";

  protected IProject project;
  protected IResource processResource;
  

  // Pages
  protected ExportKickstartProcessWizardProjectPage projectReferencePage;
  protected ExportKickstartProcessWizardProcessPage diagramSelectionPage;
  protected ExportKickstartProcessTargetWizardPage targetPage;

  protected File repoFolder;
  protected File shareFolder;
  
  protected IFolder currentDir;
  protected String error;

  public ExportKickstartProcessWizard() {
    setNeedsProgressMonitor(true);
  }

  @Override
  public void addPages() {
    super.addPages();

    if (project == null) {
      projectReferencePage = new ExportKickstartProcessWizardProjectPage(EXPORT_WIZARD_TITLE);
      addPage(projectReferencePage);
    }

    if (processResource == null) {
      diagramSelectionPage = new ExportKickstartProcessWizardProcessPage(EXPORT_WIZARD_TITLE);
      addPage(diagramSelectionPage);
    }
    targetPage = new ExportKickstartProcessTargetWizardPage(EXPORT_WIZARD_TITLE);
    addPage(targetPage);
  }

  @Override
  public boolean performFinish() {
    // Disable finish button
    getContainer().updateButtons();
    
    error = null;

    try {
      getContainer().run(true, true, new IRunnableWithProgress() {
        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
          exportProcess(monitor);
        }
      });
    } catch (InvocationTargetException ite) {
      Logger.logError(ite);
      error = ite.getMessage();
    } catch (InterruptedException ie) {
      Logger.logError(ie);
      error = ie.getMessage();
    }
    
    if(error != null) {
      getContainer().getShell().getDisplay().asyncExec(new Runnable() {
        @Override
        public void run() {
          targetPage.setErrorMessage(error);
        }
      });
    }
    
    return error == null;
  }

  protected void exportProcess(IProgressMonitor monitor) {
    storePreferences();
    
    AlfrescoWorkflowDefinitionConversionFactory factory = new AlfrescoWorkflowDefinitionConversionFactory();
    
    if (Preferences.PROCESS_EXPORT_TYPE_FS.equals(targetPage.getTargetType())) {
      if (StringUtils.isEmpty(targetPage.getCustomRepositoryFolder())
          || StringUtils.isEmpty(targetPage.getCustomShareFolder())) {
        error = "Please select target folders for the artifacts";
      } else {
        repoFolder = new File(targetPage.getCustomRepositoryFolder());
        shareFolder = new File(targetPage.getCustomShareFolder());

        if (!repoFolder.exists()) {
          error = "The provided repository folder does not exist";
        } else if (!shareFolder.exists()) {
          error = "The provided share folder does not exist";
        }
      }

    } else {
    	if (!targetPage.isSkipRebuild()) ensureTargetExists(monitor);
    	else ensureModifiedTargetExists(monitor);
    }
    
    if (error == null) {
      // We have valid folder locations, finally export the actual process

      FormReferenceReader merger = null;
      try {
        monitor.beginTask("Converting process", IProgressMonitor.UNKNOWN);

        boolean isCmis = Preferences.PROCESS_EXPORT_TYPE_CMIS.equals(targetPage.getTargetType());

        // TODO: perhaps create once and share?
        AlfrescoSimpleWorkflowJsonConverter converter = new AlfrescoSimpleWorkflowJsonConverter();
        FileInputStream fis = new FileInputStream(processResource.getLocation().toFile());
        WorkflowDefinition definition = converter.readWorkflowDefinition(fis);
        
        // Request merge of the form-definitions
        merger = new FormReferenceReader(definition, project);
        merger.mergeFormDefinition();
            
        WorkflowDefinitionConversion definitionConversion = factory.createWorkflowDefinitionConversion(definition);
        definitionConversion.convert();
	
	    if (!targetPage.isSkipRebuild()){ //don't actually overwrite the existing files, only deploy them
		    monitor.beginTask("Exporting artifacts", IProgressMonitor.UNKNOWN);
	        factory.getArtifactExporter().exportArtifacts(definitionConversion, repoFolder, shareFolder, isCmis);
        }
        
        if(isCmis) {
          // Upload the created files through CMIS
          File modelsFile = new File(repoFolder, factory.getArtifactExporter()
              .getContentModelFileName(definitionConversion));
          
          
          monitor.beginTask("Checking cmis folders", IProgressMonitor.UNKNOWN);
          File processFile = new File(repoFolder, factory.getArtifactExporter().getBpmnFileName(definitionConversion));
          Folder modelsFolder = CmisUtil.getFolderByPath(targetPage.getCmisModelsPath());
          Folder processFolder = CmisUtil.getFolderByPath(targetPage.getCmisWorkflowDefinitionsPath());
          
          CmisObject existingProcess = CmisUtil.getFolderChild(processFolder, processFile.getName());
          CmisObject existingModel = CmisUtil.getFolderChild(modelsFolder, modelsFile.getName());
          
          // First, delete the process since the model cannot be deleted when process uses it
          if(existingProcess != null) {
            monitor.beginTask("Deleting previous artifacts", IProgressMonitor.UNKNOWN);
            CmisUtil.deleteCmisObjects(existingProcess);
          }
          
          // Delete model
          if(existingModel != null && targetPage.isDeleteModels()) {
            CmisUtil.deleteCmisObjects(existingModel);
            existingModel = null;
          }
          
          monitor.beginTask("Uploading model and workflow", IProgressMonitor.UNKNOWN);
          // Upload the model and process
          CmisUtil.uploadModel(modelsFolder, modelsFile, existingModel);
          CmisUtil.uploadProcess(processFolder, processFile);
          
          // Also upload share config, if needed
          if(targetPage.isEnableShare()) {
            monitor.beginTask("Uploading share config", IProgressMonitor.UNKNOWN);
            Folder surfConfigFolder = CmisUtil.getFolderByPath(targetPage.getCmisSharePath());
            Folder modulesFolder = CmisUtil.getOrCreateChildFolder(surfConfigFolder, "module-deployments");
            Folder extensionsFolder = CmisUtil.getOrCreateChildFolder(surfConfigFolder, "extensions");
            File shareConfigFile = new File(shareFolder, factory.getArtifactExporter().getShareConfigFileName(definitionConversion));
            File shareModuleDeploymentFile = new File(shareFolder, factory.getArtifactExporter().getShareModuleDeploymentFileName(definitionConversion));
            
            CmisUtil.uploadPersistedExtensions(extensionsFolder, shareConfigFile);
            CmisUtil.uploadModuleDeployment(modulesFolder, shareModuleDeploymentFile);
            
            monitor.beginTask("Forcing reload of modules in share", IProgressMonitor.UNKNOWN);
            
            URL url = new URL(targetPage.getShareReloadUrl());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            
            connection.getResponseCode();
            connection.disconnect();
          }
        }
        
      } catch (final Throwable t) {
        Logger.logError("Error while exporting process", t);
        error = "Error while exporting process: " + t.toString();
      } finally {
        monitor.done();
        
        // Revert all changes to the definition that were done during form-merging
        if(merger != null) {
          merger.removeFormReferences();
        }
      }
    }
  }

  protected void ensureTargetExists(IProgressMonitor monitor) {
    // Use the target folder of the project
    //IFolder target = project.getFolder("target");

	  //File cwd=new File(".")).getAbsolutePath();
	 //IFolder target=projectIFolder target=System.getProperty("user.dir").getFolder("target");
	  
	IFolder target=currentDir.getFolder("target");
    try {
      if (!target.exists()) {
        target.create(true, true, monitor);
        target.setDerived(true, monitor);
      }
      // Create folders for repo and share
      IFolder repoTargetFolder = target.getFolder("repo");
      if (!repoTargetFolder.exists()) {
        repoTargetFolder.create(true, true, monitor);
      }
      IFolder shareTargetFolder = target.getFolder("share");
      if (!shareTargetFolder.exists()) {
        shareTargetFolder.create(true, true, monitor);
      }

      shareFolder = shareTargetFolder.getLocation().toFile();
      repoFolder = repoTargetFolder.getLocation().toFile();
    } catch (CoreException e) {
      String errorMessage = "Failed to create target folder";
      targetPage.setErrorMessage(errorMessage);
      Logger.logError(errorMessage, e);
    }
  }

  protected void ensureModifiedTargetExists(IProgressMonitor monitor) {
	    // Use the target folder of the project
	   // IFolder target = project.getFolder("modTarget");
		IFolder target=currentDir.getFolder("modTarget");
	    //IFolder dyntarget = project.getFolder("target");
	    IFolder dyntarget = currentDir.getFolder("target");
	    try {
	      if (!target.exists()) {
	        target.create(true, true, monitor);
	      }
	      // Create folders for repo and share
	      IFolder repoTargetFolder = target.getFolder("repo");
	      if (!repoTargetFolder.exists()) {
	        repoTargetFolder.create(true, true, monitor);
	        IFolder realdyntarget=dyntarget.getFolder("repo");
	        if (realdyntarget.exists()){
	        	copyAll (realdyntarget, repoTargetFolder);
	        }
	        
	      }
	      IFolder shareTargetFolder = target.getFolder("share");
	      if (!shareTargetFolder.exists()) {
	        shareTargetFolder.create(true, true, monitor);
	        IFolder realdyntarget=dyntarget.getFolder("share");
	        if (realdyntarget.exists()){
	        	copyAll (realdyntarget, shareTargetFolder);
	        }
	      }

	      shareFolder = shareTargetFolder.getLocation().toFile();
	      repoFolder = repoTargetFolder.getLocation().toFile();
	    } catch (CoreException e) {
	      String errorMessage = "Failed to create modified target folder";
	      targetPage.setErrorMessage(errorMessage);
	      Logger.logError(errorMessage, e);
	    }
	  }  
  
  protected void copyAll (IFolder source, IFolder dest) {
	  File sourceFolder = source.getLocation().toFile();
      File destFolder = dest.getLocation().toFile();	  
      try {
		    FileUtils.copyDirectory(sourceFolder, destFolder);
		} catch (IOException e) {
		    e.printStackTrace();
		}
  }
  
  protected void storePreferences() {
    IPreferenceStore preferences = PreferencesUtil.getActivitiDesignerPreferenceStore();
    
    preferences.setValue(Preferences.PROCESS_EXPORT_TYPE.getPreferenceId(), targetPage.getTargetType());
    if(Preferences.PROCESS_EXPORT_TYPE_FS.equals(targetPage.getTargetType())) {
      preferences.setValue(Preferences.PROCESS_TARGET_LOCATION_REPOSITORY.getPreferenceId(), targetPage.getCustomRepositoryFolder());
      preferences.setValue(Preferences.PROCESS_TARGET_LOCATION_SHARE.getPreferenceId(), targetPage.getCustomShareFolder());
    } else if(Preferences.PROCESS_EXPORT_TYPE_CMIS.equals(targetPage.getTargetType())) {
      preferences.setValue(Preferences.CMIS_MODELS_PATH.getPreferenceId(), targetPage.getCmisModelsPath());
      preferences.setValue(Preferences.CMIS_MODELS_DELETE.getPreferenceId(), targetPage.isDeleteModels());
      preferences.setValue(Preferences.CMIS_WORKFLOW_DEFINITION_PATH.getPreferenceId(), targetPage.getCmisWorkflowDefinitionsPath());
      preferences.setValue(Preferences.CMIS_SHARE_CONFIG_PATH.getPreferenceId(), targetPage.getCmisSharePath());
      preferences.setValue(Preferences.SKIP_REBUILD.getPreferenceId(), targetPage.isSkipRebuild());
      
      
      preferences.setValue(Preferences.SHARE_ENABLED.getPreferenceId(), targetPage.isEnableShare());
      preferences.setValue(Preferences.SHARE_RELOAD_URL.getPreferenceId(), targetPage.getShareReloadUrl());
    }

    try {
      if(preferences instanceof IPersistentPreferenceStore) {
        ((IPersistentPreferenceStore) preferences).save();
      }
    } catch (IOException e) {
      // Inability to store preferences should not fail the wizard, just log the problem
      Logger.logError("Error while storing target paths in preferences", e);
    }
  }

  @Override
  public boolean canFinish() {
    return targetPage.isPageComplete();
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    if (selection != null && !selection.isEmpty()) {
      IResource selectedResource = null;
      Object element = selection.getFirstElement();
      if (element instanceof IResource)
        selectedResource = (IResource) element;
      if (element instanceof IAdaptable) {
        IAdaptable adaptable = (IAdaptable) element;
        Object adapter = adaptable.getAdapter(IResource.class);
        selectedResource = (IResource) adapter;
      }

      if (selectedResource != null) {
        project = selectedResource.getProject();
        
        currentDir=(IFolder) selectedResource.getParent();

        if (selectedResource instanceof IFile) {
          IFile resourceFile = (IFile) selectedResource;
          try {
            IContentDescription contentDescription = resourceFile.getContentDescription();
            if (contentDescription != null
                && contentDescription.getContentType() != null
                && KickstartProcessMemoryModel.KICKSTART_PROCESS_CONTENT_TYPE
                    .equals(contentDescription.getContentType().getId())) {
              processResource = selectedResource;
            }
          } catch (CoreException e) {
            Logger.logError("Error while getting file description for selected file: " + resourceFile.toString(), e);
          }
        }
      }
    }
  }

  public IProject getProject() {
    return project;
  }

  public void setProject(IProject project) {
    this.project = project;
  }

  public IResource getProcessResource() {
    return processResource;
  }

  public void setProcessResource(IResource diagramResource) {
    this.processResource = diagramResource;
  }
}
