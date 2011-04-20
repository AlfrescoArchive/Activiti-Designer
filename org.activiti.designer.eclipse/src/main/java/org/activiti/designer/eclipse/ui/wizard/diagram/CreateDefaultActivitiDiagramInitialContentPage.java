package org.activiti.designer.eclipse.ui.wizard.diagram;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.common.PluginImage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class CreateDefaultActivitiDiagramInitialContentPage extends WizardPage {

  public static final String PAGE_NAME = "createDefaultActivitiDiagramInitialContentPage";

  private Composite container;

  private Group contentSourceGroup;

  private Button contentSourceNone;
  private Button contentSourceImport;
  private Button contentSourceTemplate;

  public CreateDefaultActivitiDiagramInitialContentPage() {
    super(PAGE_NAME);
    setTitle("New Activiti Diagram");
    setImageDescriptor(ActivitiPlugin.getImageDescriptor(PluginImage.ACTIVITI_LOGO_64x64));
    setDescription("Select the initial content for the new diagram.");
  }

  @Override
  public void createControl(Composite parent) {

    FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    toolkit.setBackground(parent.getBackground());

    container = toolkit.createComposite(parent, SWT.NULL);

    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 1;

    GridData data = null;

    contentSourceGroup = new Group(container, SWT.SHADOW_IN);
    contentSourceGroup.setText("Do you want to add content to your diagram to start editing?");
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = SWT.FILL;
    contentSourceGroup.setLayoutData(data);

    contentSourceGroup.setLayout(new RowLayout(SWT.VERTICAL));

    contentSourceNone = toolkit.createButton(contentSourceGroup, "No, just create an empty diagram", SWT.RADIO);
    contentSourceNone.setSelection(true);

    contentSourceImport = toolkit.createButton(contentSourceGroup, "Yes, import a BPMN 2.0 file", SWT.RADIO);
    contentSourceImport.setEnabled(false);

    contentSourceTemplate = toolkit.createButton(contentSourceGroup, "Yes, use a template", SWT.RADIO);
    contentSourceTemplate.setEnabled(false);

    setControl(container);
    setPageComplete(false);

  }

  /**
   * Gets the information for the initial content.
   */
  public InitialContentInfo getInitialContentInfo() {
    // Only one option enabled at the moment
    return new InitialContentInfo(InitialContentType.NONE, null);
  }

  public static class InitialContentInfo {

    private final InitialContentType initialContentType;
    private final Object contentDefinition;

    public InitialContentInfo(InitialContentType initialContentType, Object contentDefinition) {
      super();
      this.initialContentType = initialContentType;
      this.contentDefinition = contentDefinition;
    }

    public InitialContentType getInitialContentType() {
      return initialContentType;
    }

    public Object getContentDefinition() {
      return contentDefinition;
    }

  }

  public static enum InitialContentType {
    NONE, IMPORT, TEMPLATE;
  }

}
