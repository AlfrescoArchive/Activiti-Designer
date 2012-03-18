package org.activiti.designer.eclipse.ui.wizard.diagram;

import java.lang.reflect.InvocationTargetException;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.editor.Bpmn2DiagramCreator;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;


/**
 * The Class CreateDefaultActivitiDiagramWizard.
 */
public class CreateDefaultActivitiDiagramWizard extends BasicNewResourceWizard {

  private Diagram diagram;
  private CreateDefaultActivitiDiagramInitialContentPage initialContentPage;

  @Override
  public void addPages() {
    super.addPages();
    addPage(new CreateDefaultActivitiDiagramNameWizardPage(super.getSelection()));
    initialContentPage = new CreateDefaultActivitiDiagramInitialContentPage();
    addPage(initialContentPage);
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

		final IResource container = diagramFile.getProject();

		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					IPath path = container.getFullPath().append(diagramName);
					IFolder folder = null;
					Bpmn2DiagramCreator factory = new Bpmn2DiagramCreator();

					folder = Bpmn2DiagramCreator.getTempFolder(path);

					factory.setDiagramFile(Bpmn2DiagramCreator.getTempFile(path,folder));

					factory.setDiagramFolder(folder);

					factory.createDiagram(true);

				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
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
