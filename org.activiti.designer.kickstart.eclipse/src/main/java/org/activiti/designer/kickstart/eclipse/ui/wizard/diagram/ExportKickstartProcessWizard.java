package org.activiti.designer.kickstart.eclipse.ui.wizard.diagram;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;

import org.activiti.designer.kickstart.eclipse.Logger;
import org.activiti.designer.kickstart.eclipse.common.KickstartPlugin;
import org.activiti.designer.kickstart.eclipse.util.KickstartConstants;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoWorkflowDefinitionConversionFactory;
import org.activiti.workflow.simple.converter.WorkflowDefinitionConversion;
import org.activiti.workflow.simple.converter.json.SimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.definition.HumanStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.activiti.workflow.simple.definition.form.FormDefinition;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.service.prefs.BackingStoreException;

public class ExportKickstartProcessWizard extends Wizard implements IExportWizard {

  public static final String EXPORT_WIZARD_TITLE = "Export Kickstart Process";

  // TODO: use shared constants
  public static final String PARAMETER_FORM_REFERENCE = "form-reference";
  protected IProject project;
  protected IResource processResource;

  // Pages
  protected ExportKickstartProcessWizardProjectPage projectReferencePage;
  protected ExportKickstartProcessWizardProcessPage diagramSelectionPage;
  protected ExportKickstartProcessTargetWizardPage targetPage;

  protected File repoFolder;
  protected File shareFolder;
  
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
    if (targetPage.isCustomLocationUsed()) {

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
      ensureTargetExists(monitor);
    }

    if (error == null) {
      // We have valid folder locations, finally export the actual process

      try {
        monitor.beginTask("Converting process", IProgressMonitor.UNKNOWN);
        // TODO: perhaps create once and share?
        AlfrescoWorkflowDefinitionConversionFactory factory = new AlfrescoWorkflowDefinitionConversionFactory();

        SimpleWorkflowJsonConverter converter = new SimpleWorkflowJsonConverter();
        FileInputStream fis = new FileInputStream(processResource.getLocation().toFile());
        WorkflowDefinition definition = converter.readWorkflowDefinition(fis);
        
        // Add start-form, if any
        if(definition.getParameters().containsKey(PARAMETER_FORM_REFERENCE)) {
          String startFormPath = (String) definition.getParameters().get(PARAMETER_FORM_REFERENCE);
          IFile startFormFile = project.getFile(new Path(startFormPath));
          FormDefinition startForm = converter.readFormDefinition(new FileInputStream(startFormFile.getLocation().toFile()));
          definition.setStartFormDefinition(startForm);
          System.out.println("Using start-form: " + startFormFile);
        }

        // Add all forms
        for(StepDefinition step : definition.getSteps()) {
          if(step instanceof HumanStepDefinition) {
            HumanStepDefinition humanStep = (HumanStepDefinition) step;
            
            // TODO: REMOVE
            if(humanStep.getId() == null) {
              humanStep.setId(humanStep.getName().replace(" ", "").toLowerCase());
            }
            
            if(humanStep.getParameters().containsKey(PARAMETER_FORM_REFERENCE)) {
              String formPath = (String) humanStep.getParameters().get(PARAMETER_FORM_REFERENCE);
              IFile formFile = project.getFile(new Path(formPath));
              FormDefinition form = converter.readFormDefinition(new FileInputStream(formFile.getLocation().toFile()));
              humanStep.setForm(form);
              
              System.out.println("Using form: " + formFile);
            }
          }
        }
            
        WorkflowDefinitionConversion definitionConversion = factory.createWorkflowDefinitionConversion(definition);
        definitionConversion.convert();

        monitor.beginTask("Exporting artifacts", IProgressMonitor.UNKNOWN);
        factory.getArtifactExporter().exportArtifacts(definitionConversion, repoFolder, shareFolder);
      } catch (final Throwable t) {
        Logger.logError("Error while exporting process", t);
        error = "Error while exporting process: " + t.toString();
      } finally {
        monitor.done();
      }
    }
  }

  protected void ensureTargetExists(IProgressMonitor monitor) {
    // Use the target folder of the project
    IFolder target = project.getFolder("target");
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

  protected void storePreferences() {
    IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(KickstartPlugin.PLUGIN_ID);
    preferences.putBoolean(KickstartConstants.PREFERENCE_USE_CUSTOM_LCOATION, targetPage.isCustomLocationUsed());
    
    if(targetPage.isCustomLocationUsed()) {
      preferences.put(KickstartConstants.PREFERENCE_TARGET_LOCATION_REPOSITORY, targetPage.getCustomRepositoryFolder());
      preferences.put(KickstartConstants.PREFERENCE_TARGET_LOCATION_SHARE, targetPage.getCustomShareFolder());
    }

    try {
      preferences.flush();
    } catch (BackingStoreException e) {
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
