package org.activiti.designer.eclipse.ui.wizard.diagram;

import java.io.InputStream;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.common.FileService;
import org.activiti.designer.eclipse.navigator.nodes.base.AbstractInstancesOfTypeContainerNode;
import org.activiti.designer.eclipse.preferences.Preferences;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.eclipse.util.Util;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * The Class CreateDefaultActivitiDiagramWizard.
 */
public class CreateDefaultActivitiDiagramWizard extends BasicNewResourceWizard {

  private Diagram diagram;

  @Override
  public void addPages() {
    super.addPages();
    addPage(new CreateDefaultActivitiDiagramNameWizardPage(super.getSelection()));
    addPage(new CreateDefaultActivitiDiagramInitialContentPage());
  }

  @Override
  public boolean canFinish() {
    return canCreateDiagramFile();
  }

  private boolean canCreateDiagramFile() {
    final IFile fileToCreate = getDiagramFile();
    if (fileToCreate != null) {
      return !fileToCreate.exists();
    }
    return false;
  }

  private IFile getDiagramFile() {

    final String diagramName = getDiagramName();
    if (StringUtils.isBlank(diagramName)) {
      return null;
    }

    IProject project = null;
    IFolder diagramFolder = null;

    // Added check on IJavaProject
    // Kept IProject check for future facet implementation
    Object element = getSelection().getFirstElement();
    if (element instanceof IProject) {
      project = (IProject) element;
    } else if (element instanceof IJavaProject) {
      IJavaProject javaProject = (IJavaProject) element;
      project = javaProject.getProject();
    } else if (element instanceof AbstractInstancesOfTypeContainerNode) {
      AbstractInstancesOfTypeContainerNode aiocn = (AbstractInstancesOfTypeContainerNode) element;
      project = aiocn.getProject();
    } else if (element instanceof IFolder) {
      diagramFolder = (IFolder) element;
      project = diagramFolder.getProject();
    } else if (element instanceof PackageFragment) { // access is
      // discouraged, but
      // inevitable when
      // the selection is
      // the diagrams
      // package itself
      PackageFragment fragment = (PackageFragment) element;
      project = fragment.getJavaProject().getProject();
    }

    if (project == null || !project.isAccessible()) {
      String error = "No open project was found for the current selection. Select a project and restart the wizard.";
      IStatus status = new Status(IStatus.ERROR, ActivitiPlugin.getID(), error);
      ErrorDialog.openError(getShell(), "No Project Found", null, status);
      return null;
    }

    if (diagramFolder == null) {
      diagramFolder = project.getFolder(ActivitiBPMNDiagramConstants.DIAGRAM_FOLDER);
    }

    return diagramFolder.getFile(diagramName);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.wizards.newresource.BasicNewResourceWizard#init(org.eclipse
   * .ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
   */
  @Override
  public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
    super.init(workbench, currentSelection);
  }

  @Override
  public IWizardPage getNextPage(IWizardPage page) {
    if (page instanceof CreateDefaultActivitiDiagramNameWizardPage) {

    }
    return super.getNextPage(page);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish() {

    final String diagramTypeId = "BPMNdiagram";

    final IFile diagramFile = getDiagramFile();
    final String diagramName = getDiagramName();

    URI uri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);

    TransactionalEditingDomain domain = null;

    boolean createContent = PreferencesUtil.getBooleanPreference(Preferences.EDITOR_ADD_DEFAULT_CONTENT_TO_DIAGRAMS);

    if (createContent) {
      final InputStream contentStream = Util.getContentStream(Util.Content.NEW_DIAGRAM_CONTENT);
      InputStream replacedStream = Util.swapStreamContents(diagramName, contentStream);
      domain = FileService.createEmfFileForDiagram(uri, null, replacedStream, diagramFile);
      diagram = org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal.getEmfService().getDiagramFromFile(diagramFile, domain.getResourceSet());

    } else {
      diagram = Graphiti.getPeCreateService().createDiagram(diagramTypeId, diagramName, true);

      domain = FileService.createEmfFileForDiagram(uri, diagram, null, null);

      final Runnable runnable = new Runnable() {

        public void run() {

          org.eclipse.bpmn2.Process process = Bpmn2Factory.eINSTANCE.createProcess();

          final String simpleDiagramName = StringUtils.substringBefore(diagramName, ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION);

          final String diagramId = StringUtils.deleteWhitespace(WordUtils.capitalize(simpleDiagramName));

          process.setId(diagramId);
          process.setName(simpleDiagramName);
          Documentation documentation = Bpmn2Factory.eINSTANCE.createDocumentation();
          documentation.setId("documentation_process");
          documentation.setText(String.format("Place documentation for the '%s' process here.", simpleDiagramName));
          process.getDocumentation().add(documentation);

          diagram.eResource().getContents().add(process);
        }
      };

      domain.getCommandStack().execute(new RecordingCommand(domain, "default process content") {

        protected void doExecute() {
          runnable.run();
        }
      });
    }

    String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
    DiagramEditorInput editorInput = new DiagramEditorInput(EcoreUtil.getURI(diagram), domain, providerId);

    try {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, ActivitiBPMNDiagramConstants.DIAGRAM_EDITOR_ID);
    } catch (PartInitException e) {
      String error = "Error while opening diagram editor";
      IStatus status = new Status(IStatus.ERROR, ActivitiPlugin.getID(), error, e);
      ErrorDialog.openError(getShell(), "An error occured", null, status);
      return false;
    }

    return true;
  }

  /**
   * Gets the diagram.
   * 
   * @return the diagram
   */
  public Diagram getDiagram() {
    return diagram;
  }

  private CreateDefaultActivitiDiagramNameWizardPage getNamePage() {
    return (CreateDefaultActivitiDiagramNameWizardPage) getPage(CreateDefaultActivitiDiagramNameWizardPage.PAGE_NAME);
  }

  private String getDiagramName() {
    return getNamePage().getDiagramName();
  }

}
