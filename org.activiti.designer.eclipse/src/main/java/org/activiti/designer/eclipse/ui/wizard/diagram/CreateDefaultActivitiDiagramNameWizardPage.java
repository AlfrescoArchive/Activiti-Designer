package org.activiti.designer.eclipse.ui.wizard.diagram;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.common.PluginImage;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

public class CreateDefaultActivitiDiagramNameWizardPage extends WizardPage implements ITabbedPropertyConstants {

  public static final String PAGE_NAME = "createDefaultActivitiDiagramNameWizardPage";

  private Text diagramName;

  public CreateDefaultActivitiDiagramNameWizardPage() {
    super(PAGE_NAME, "New Activiti Diagram", ActivitiPlugin.getImageDescriptor(PluginImage.ACTIVITI_LOGO_64x64));
    setDescription("Create a new Activiti BPMN 2.0 Diagram.");
  }

  @Override
  public void createControl(Composite parent) {

    FormToolkit toolkit = new FormToolkit(parent.getDisplay());

    // GridData data = new GridData(20, 20, true, false);

    // parent.setLayoutData(data);

    final Label diagramNameLabel = toolkit.createLabel(parent, "Diagram name", SWT.NONE);
    diagramNameLabel.setToolTipText("Provide a name for the diagram");
    // data = new GridData(20, 20, true, false);
    // data.left = new FormAttachment(HSPACE);
    // diagramNameLabel.setLayoutData(data);

    diagramName = toolkit.createText(parent, "default.activiti", SWT.BORDER);
    // data = new FormData();
    // data.left = new FormAttachment(diagramNameLabel, HSPACE);
    // diagramName.setLayoutData(data);

    super.setControl(parent);

  }

  public String getDiagramName() {
    if (StringUtils.isNotBlank(diagramName.getText())) {
      return diagramName.getText();
    }
    return null;
  }

}
