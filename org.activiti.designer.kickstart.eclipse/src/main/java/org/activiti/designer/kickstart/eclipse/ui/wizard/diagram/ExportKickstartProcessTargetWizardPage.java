package org.activiti.designer.kickstart.eclipse.ui.wizard.diagram;

import org.activiti.designer.kickstart.eclipse.common.KickstartPlugin;
import org.activiti.designer.kickstart.eclipse.ui.ExternalFolderSelector;
import org.activiti.designer.kickstart.eclipse.util.KickstartConstants;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class ExportKickstartProcessTargetWizardPage extends WizardPage {
  private static final int LABEL_WIDTH = 150;
  protected Button customFoldersButton;
  protected Button targetFolderButton;
  
  protected ExternalFolderSelector repoFolderSelect;
  protected ExternalFolderSelector shareFolderSelect;
  
  protected boolean customLocationUsed = false;
  
  public ExportKickstartProcessTargetWizardPage(String title) {
    super("select-traget");

    setTitle(title);
    setDescription("Select the destination for the process artifacts");
  }
  
  @Override
  public void createControl(Composite parent) {
    Composite topLevel = new Composite(parent, SWT.NONE);
    topLevel.setLayout(new FormLayout());
    
    Composite radioButtonGroup = new Composite(topLevel, SWT.NONE);
    radioButtonGroup.setLayout(new RowLayout(SWT.VERTICAL));
    
    FormData data = new FormData();
    data.left = new FormAttachment(0, LABEL_WIDTH);
    data.right = new FormAttachment(100, -IDialogConstants.HORIZONTAL_MARGIN);
    data.top = new FormAttachment(0, IDialogConstants.VERTICAL_SPACING);
    radioButtonGroup.setLayoutData(data);
    
    targetFolderButton = new Button(radioButtonGroup, SWT.RADIO);
    targetFolderButton.setText("Project target folder");
    targetFolderButton.setData("target");
    targetFolderButton.setSelection(true);
    
    customFoldersButton = new Button(radioButtonGroup, SWT.RADIO);
    customFoldersButton.setData("folders");
    customFoldersButton.setText("Custom location");
    
    createLabel("Artifacts target", radioButtonGroup, topLevel);
    
    // Add target widgets
    repoFolderSelect = new ExternalFolderSelector(topLevel);
    repoFolderSelect.setEnabled(false);
    data = new FormData();
    data.left = new FormAttachment(0, LABEL_WIDTH);
    data.right = new FormAttachment(100, -IDialogConstants.HORIZONTAL_MARGIN);
    data.top = new FormAttachment(radioButtonGroup, IDialogConstants.VERTICAL_SPACING);
    repoFolderSelect.getComposite().setLayoutData(data);
    createLabel("Repository extensions folder", repoFolderSelect.getComposite(), topLevel);
    
    shareFolderSelect = new ExternalFolderSelector(topLevel);
    shareFolderSelect.setEnabled(false);
    data = new FormData();
    data.left = new FormAttachment(0, LABEL_WIDTH);
    data.right = new FormAttachment(100, -IDialogConstants.HORIZONTAL_MARGIN);
    data.top = new FormAttachment(repoFolderSelect.getComposite(), IDialogConstants.VERTICAL_SPACING);
    shareFolderSelect.getComposite().setLayoutData(data);
    createLabel("Share web-extensions folder", shareFolderSelect.getComposite(), topLevel);
    
    // Add listener to enable the target selection
    customFoldersButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        repoFolderSelect.setEnabled(customFoldersButton.getSelection());
        shareFolderSelect.setEnabled(customFoldersButton.getSelection());
        customLocationUsed = customFoldersButton.getSelection();
      }
    });
    
    // Initialize paths from preferences
    IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(KickstartPlugin.PLUGIN_ID);
    String repositoryPath = preferences.get(KickstartConstants.PREFERENCE_TARGET_LOCATION_REPOSITORY, "");
    String sharePath = preferences.get(KickstartConstants.PREFERENCE_TARGET_LOCATION_SHARE, "");
    
    repoFolderSelect.setCurrentPath(repositoryPath);
    shareFolderSelect.setCurrentPath(sharePath);
    
    setControl(topLevel);
  }

  public boolean isCustomLocationUsed() {
    return customLocationUsed;
  }
  
  public String getCustomRepositoryFolder() {
    return repoFolderSelect.getCurrentPath();
  }
  
  public String getCustomShareFolder() {
    return shareFolderSelect.getCurrentPath();
  }
  
  
  
  protected void createLabel(String text, Control control, Composite parent) {
    Label label = new Label(parent, SWT.NONE | SWT.WRAP);
    label.setText(text);
    FormData data = new FormData();
    data.left = new FormAttachment(0, IDialogConstants.HORIZONTAL_SPACING);
    data.right = new FormAttachment(control, -IDialogConstants.HORIZONTAL_MARGIN);
    data.top = new FormAttachment(control, IDialogConstants.VERTICAL_SPACING, SWT.TOP);
    label.setLayoutData(data);
  }

}
