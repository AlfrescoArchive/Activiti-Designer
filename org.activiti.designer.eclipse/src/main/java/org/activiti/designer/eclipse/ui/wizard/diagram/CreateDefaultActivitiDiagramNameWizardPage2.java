package org.activiti.designer.eclipse.ui.wizard.diagram;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.common.PluginImage;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class CreateDefaultActivitiDiagramNameWizardPage2 extends WizardNewFileCreationPage {

  public static final String PAGE_NAME = "createDefaultActivitiDiagramNameWizardPage";

  public CreateDefaultActivitiDiagramNameWizardPage2(IStructuredSelection selection) {
    super(PAGE_NAME, selection);
    setTitle("New Activiti Diagram");
    setImageDescriptor(ActivitiPlugin.getImageDescriptor(PluginImage.ACTIVITI_LOGO_64x64));
    setDescription("Create a new Activiti BPMN 2.0 Diagram.");
    setAllowExistingResources(false);
    setFileExtension(StringUtils.substringAfter(ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION, "."));
  }

  // @Override
  // public void createControl(Composite parent) {
  //
  // FormToolkit toolkit = new FormToolkit(parent.getDisplay());
  //
  // // GridData data = new GridData(20, 20, true, false);
  //
  // // parent.setLayoutData(data);
  //
  // final Label diagramNameLabel = toolkit.createLabel(parent, "Diagram name",
  // SWT.NONE);
  // diagramNameLabel.setToolTipText("Provide a name for the diagram");
  // // data = new GridData(20, 20, true, false);
  // // data.left = new FormAttachment(HSPACE);
  // // diagramNameLabel.setLayoutData(data);
  //
  // diagramName = toolkit.createText(parent, "default.activiti", SWT.BORDER);
  // // data = new FormData();
  // // data.left = new FormAttachment(diagramNameLabel, HSPACE);
  // // diagramName.setLayoutData(data);
  //
  // super.setControl(parent);
  //
  // }

  public String getDiagramName() {
    if (StringUtils.isNotBlank(super.getFileName())) {
      return super.getFileName();
    }
    return null;
  }

}
