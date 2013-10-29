package org.activiti.designer.kickstart.eclipse.ui.wizard.form;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

/**
 * Wizard for creating a new Kickstart form.
 * 
 * @author Frederik Heremans
 */
// TODO: do NOT use BasicNewFileResourceWizard, as it's not made for extending. using now for
// easy access to selectAndReveal() but that can be done using an IAction...
public class CreateKickstartFormWizard extends BasicNewFileResourceWizard implements IAdaptable {

  private CreateKickstartFormNameWizardPage namePage;
  private IFile file;
  
  @Override
  public void addPages() {
    namePage = new CreateKickstartFormNameWizardPage(getSelection());
    addPage(namePage);
  }
  
  @Override
  public boolean performFinish() {
    // Create the file that has been selected in the "create file" page
    file = namePage.createNewFile();
    
    if(file == null) {
      return false;
    }

    selectAndReveal(file);
    
    // Open editor on new file.
    IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
    try {
        if (dw != null) {
            IWorkbenchPage page = dw.getActivePage();
            if (page != null) {
                IDE.openEditor(page, file, true);
            }
        }
    } catch (PartInitException e) {
      MessageDialog.openError(getShell(), "Error while ", e.getMessage());
    }
    return true;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Object getAdapter(Class adapter) {
    if(IFile.class.equals(adapter)) {
      return file;
    }
    return null;
  }
}
