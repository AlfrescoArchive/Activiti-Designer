package org.activiti.designer.kickstart.eclipse.ui.wizard.form;

import org.activiti.designer.kickstart.eclipse.common.KickstartPlugin;
import org.activiti.designer.kickstart.eclipse.common.PluginImage;
import org.activiti.designer.kickstart.eclipse.util.KickstartConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * Page that allows selecting the file-name of a new form and the destination. Doesn't allow
 * finishing in case the resource already exists. 
 *  
 * @author Frederik Heremans
 */
public class CreateKickstartFormNameWizardPage extends WizardNewFileCreationPage {

  public static final String PAGE_NAME = "createKickstartFormNameWizardPage";
  private static final String DEFAULT_FORM_NAME = "MyForm." + KickstartConstants.KICKSTART_FORM_EXTENSION;
  
  public CreateKickstartFormNameWizardPage(IStructuredSelection selection) {
    super(PAGE_NAME, selection);
    super.setFileName(DEFAULT_FORM_NAME);
    
    setTitle("New Kickstart Form");
    setImageDescriptor(KickstartPlugin.getImageDescriptor(PluginImage.ACTIVITI_LOGO_64x64));
    setDescription("Create a new Kickstart form.");
    setFileExtension(KickstartConstants.KICKSTART_FORM_EXTENSION);
    
    // Don't allow overriding existing forms when creating a new one.
    setAllowExistingResources(false);
  }
}
