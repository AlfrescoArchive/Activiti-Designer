package org.activiti.designer.eclipse.ui.wizard.diagram;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.common.PluginImage;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class CreateDefaultActivitiDiagramNameWizardPage extends WizardNewFileCreationPage {

  public static final String PAGE_NAME = "createDefaultActivitiDiagramNameWizardPage";

  public CreateDefaultActivitiDiagramNameWizardPage(IStructuredSelection selection) {
    super(PAGE_NAME, selection);
    setTitle("New Activiti Diagram");
    setImageDescriptor(ActivitiPlugin.getImageDescriptor(PluginImage.ACTIVITI_LOGO_64x64));
    setDescription("Create a new Activiti BPMN 2.0 Diagram.");
    setFileExtension(StringUtils.substringAfter(ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION, "."));
  }

  @Override
  public boolean isPageComplete() {
    return getWizard().canFinish();
  }

  @Override
  protected IStatus validateLinkedResource() {
    boolean valid = getWizard().canFinish();
    if (!valid) {
      final String errorMessage = String.format("A file with the name '%s' already exists in the project. Choose a different name for the diagram.",
              getDiagramName());
      this.setErrorMessage(errorMessage);
      return new Status(IStatus.ERROR, ActivitiPlugin.PLUGIN_ID, errorMessage);
    }
    return super.validateLinkedResource();
  }

  public String getDiagramName() {
    if (StringUtils.isNotBlank(super.getFileName())) {
      return super.getFileName();
    }
    return null;
  }

}
