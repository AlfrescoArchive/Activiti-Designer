/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.kickstart.eclipse.ui.wizard.diagram;

import org.activiti.designer.kickstart.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.kickstart.eclipse.ui.ExternalFolderSelector;
import org.activiti.designer.kickstart.util.widget.ToggleContainerViewer;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExportKickstartProcessTargetWizardPage extends WizardPage {
  private static final int LABEL_WIDTH = 150;
  protected Button customFoldersButton;
  protected Button targetFolderButton;
  protected Button cmisButton;
  protected ToggleContainerViewer toggleContainer;
  
  protected ExternalFolderSelector repoFolderSelect;
  protected ExternalFolderSelector shareFolderSelect;
  
  protected Text cmisModelsText;
  protected Text cmisWorkflowsText;
  protected Text cmisShareText;
  protected Text shareReloadText;
  protected Button enableShareButton;
  protected Button deleteModelButton;
  protected String cmisWorkflowPath;
  protected String cmisModelsPath;
  protected String cmisSharePath;
  protected String targetType;
  protected String shareReloadUrl;
  protected boolean enableShare;
  protected boolean deleteModels;
  
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
    
    createLabel("Export target", radioButtonGroup, topLevel);
    
    targetFolderButton = new Button(radioButtonGroup, SWT.RADIO);
    targetFolderButton.setText("Project target folder");
    targetFolderButton.setData(Preferences.PROCESS_EXPORT_TYPE_TARGET);
    
    customFoldersButton = new Button(radioButtonGroup, SWT.RADIO);
    customFoldersButton.setData(Preferences.PROCESS_EXPORT_TYPE_FS);
    customFoldersButton.setText("Custom location");
    
    cmisButton = new Button(radioButtonGroup, SWT.RADIO);
    cmisButton.setData(Preferences.PROCESS_EXPORT_TYPE_CMIS);
    cmisButton.setText("CMIS");
    
    Label seperator = new Label(topLevel, SWT.SEPARATOR | SWT.HORIZONTAL);
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, -IDialogConstants.HORIZONTAL_MARGIN);
    data.top = new FormAttachment(radioButtonGroup, IDialogConstants.VERTICAL_SPACING);
    seperator.setLayoutData(data);
    
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, -IDialogConstants.HORIZONTAL_MARGIN);
    data.top = new FormAttachment(seperator, IDialogConstants.VERTICAL_SPACING);
    
    toggleContainer = new ToggleContainerViewer(topLevel);
    toggleContainer.setPack(false);
    toggleContainer.getComposite().setLayoutData(data);
    
    Composite fsComposite = new Composite(toggleContainer.getComposite(), SWT.NONE);
    fsComposite.setLayout(new GridLayout(1, true));
    toggleContainer.addControl(Preferences.PROCESS_EXPORT_TYPE_FS, fsComposite);
    
    // Add target widgets
    repoFolderSelect = new ExternalFolderSelector(fsComposite, "Repository folder");
    repoFolderSelect.getComposite().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    shareFolderSelect = new ExternalFolderSelector(fsComposite, "Share folder");
    shareFolderSelect.getComposite().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    
    // Add cmis widgets
    Composite cmisComposite = new Composite(toggleContainer.getComposite(), SWT.NONE);
    cmisComposite.setLayout(new GridLayout(1, true));
    toggleContainer.addControl(Preferences.PROCESS_EXPORT_TYPE_CMIS, cmisComposite);
    Label cmisInfoLabel = new Label(cmisComposite, SWT.WRAP);
    cmisInfoLabel.setText("Deploys the model and worklfow to the data-dictionary. If the model and process-file already exists, they will be deleted first. Please note that deleting "
        + " the model and process will result in the workflow being undeployed. In case other versions of the process-definition exist that reference the model " +
        ", the upload will fail. When using this approach, make sure you only deploy and undeploy the process using this tool. " +
        "Make sure ALL process-instances are ended/cancelled or redeploy will fail.");
    cmisInfoLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    
    cmisWorkflowsText = new Text(createCompositeWithLabel(cmisComposite, "Workflow definitions path"), SWT.BORDER | SWT.SINGLE);
    cmisWorkflowsText.setLayoutData(createFillHorizontalGridData());
    
    cmisModelsText = new Text(createCompositeWithLabel(cmisComposite, "Models path"), SWT.BORDER | SWT.SINGLE);
    cmisModelsText.setLayoutData(createFillHorizontalGridData());
    
    deleteModelButton = new Button(createCompositeWithLabel(cmisComposite, "Delete and recreate model"), SWT.CHECK);
    deleteModelButton.setLayoutData(createFillHorizontalGridData());
    
    enableShareButton = new Button(createCompositeWithLabel(cmisComposite, "Reload share"), SWT.CHECK);
    enableShareButton.setLayoutData(createFillHorizontalGridData());
    
    cmisShareText = new Text(createCompositeWithLabel(cmisComposite, "Share config path"), SWT.BORDER | SWT.SINGLE);
    cmisShareText.setLayoutData(createFillHorizontalGridData());
    
    shareReloadText = new Text(createCompositeWithLabel(cmisComposite, "Share force reload URL"), SWT.BORDER | SWT.SINGLE);
    shareReloadText.setLayoutData(createFillHorizontalGridData());
    
    Label targetInfoLabel = new Label(toggleContainer.getComposite(), SWT.WRAP);
    targetInfoLabel.setText("The deployment artifacts will be added to the project's target folder. The 'repo' folder contains the artifacts to be placed on the repository classpath and the 'share' folder contains artifacts for share.");
    toggleContainer.addControl(Preferences.PROCESS_EXPORT_TYPE_TARGET, targetInfoLabel);
    
    cmisModelsText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        cmisModelsPath = cmisModelsText.getText();
      }
    });
    
    cmisWorkflowsText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        cmisWorkflowPath = cmisWorkflowsText.getText();
      }
    });
    
    cmisShareText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        cmisSharePath = cmisShareText.getText();
      }
    });
    
    shareReloadText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        shareReloadUrl = shareReloadText.getText();
      }
    });
    
    enableShareButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        enableShare = enableShareButton.getSelection();
        cmisShareText.setEnabled(enableShare);
        shareReloadText.setEnabled(enableShare);
      }
    });
    
    deleteModelButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        deleteModels = deleteModelButton.getSelection();
      }
    });
    
    // Add listener to enable the target selection
    customFoldersButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if(customFoldersButton.getSelection()) {
          toggleContainer.showChild(Preferences.PROCESS_EXPORT_TYPE_FS);
          targetType = Preferences.PROCESS_EXPORT_TYPE_FS;
        }
      }
    });
    targetFolderButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if(targetFolderButton.getSelection()) {
          toggleContainer.showChild(Preferences.PROCESS_EXPORT_TYPE_TARGET);
          targetType = Preferences.PROCESS_EXPORT_TYPE_TARGET;
        }
      }
    });
    cmisButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if(cmisButton.getSelection()) {
          toggleContainer.showChild(Preferences.PROCESS_EXPORT_TYPE_CMIS);
          targetType = Preferences.PROCESS_EXPORT_TYPE_CMIS;
        }
      }
    });
    loadPreferences();
    
    setControl(topLevel);
  }
  

  protected Composite createCompositeWithLabel(Composite parent, String labelText) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
    gridData.widthHint = 150;
    Label label = new Label(composite, SWT.NONE);
    label.setLayoutData(gridData);
    label.setText(labelText);
    return composite;
  }
  
  protected GridData createFillHorizontalGridData() {
    return new GridData(SWT.FILL, SWT.CENTER, true, false);
  }
  
  protected void loadPreferences() {
    // Initialize paths and settings from preferences
    String repositoryPath = PreferencesUtil.getStringPreference(Preferences.PROCESS_TARGET_LOCATION_REPOSITORY);
    String sharePath = PreferencesUtil.getStringPreference(Preferences.PROCESS_TARGET_LOCATION_SHARE);
    String cmisModelPath = PreferencesUtil.getStringPreference(Preferences.CMIS_MODELS_PATH);
    String cmisWorkflowsPath = PreferencesUtil.getStringPreference(Preferences.CMIS_WORKFLOW_DEFINITION_PATH);
    String cmisSharePath = PreferencesUtil.getStringPreference(Preferences.CMIS_SHARE_CONFIG_PATH);
    String shareReloadUrl = PreferencesUtil.getStringPreference(Preferences.SHARE_RELOAD_URL);
    enableShare = PreferencesUtil.getBooleanPreference(Preferences.SHARE_ENABLED);
    deleteModels = PreferencesUtil.getBooleanPreference(Preferences.CMIS_MODELS_DELETE);
    
    // Update widgets according to preferences
    String targetType = PreferencesUtil.getStringPreference(Preferences.PROCESS_EXPORT_TYPE);
    if(targetType == null) {
      targetType = Preferences.PROCESS_EXPORT_TYPE_TARGET;
    }
    
    this.targetType = targetType;   
    if(Preferences.PROCESS_EXPORT_TYPE_FS.equals(targetType)) {
      customFoldersButton.setSelection(true);
      toggleContainer.showChild(Preferences.PROCESS_EXPORT_TYPE_FS);
    } else if(Preferences.PROCESS_EXPORT_TYPE_CMIS.equals(targetType)) {
      cmisButton.setSelection(true);
      toggleContainer.showChild(Preferences.PROCESS_EXPORT_TYPE_CMIS);
    } else {
      targetFolderButton.setSelection(true);
      toggleContainer.showChild(Preferences.PROCESS_EXPORT_TYPE_TARGET);
    }
    
    // Populate texts
    repoFolderSelect.setCurrentPath(repositoryPath);
    shareFolderSelect.setCurrentPath(sharePath);
    cmisModelsText.setText(cmisModelPath);
    cmisWorkflowsText.setText(cmisWorkflowsPath);
    cmisShareText.setText(cmisSharePath);
    shareReloadText.setText(shareReloadUrl);
    shareReloadText.setEnabled(enableShare);
    cmisShareText.setEnabled(enableShare);
    enableShareButton.setSelection(enableShare);
    deleteModelButton.setSelection(deleteModels);
  }

  public String getCustomRepositoryFolder() {
    return repoFolderSelect.getCurrentPath();
  }
  
  public String getCustomShareFolder() {
    return shareFolderSelect.getCurrentPath();
  }
  
  public String getCmisWorkflowDefinitionsPath() {
    return cmisWorkflowPath;
  }
  
  public String getCmisModelsPath() {
    return cmisModelsPath;
  }
  
  public String getCmisSharePath() {
    return cmisSharePath;
  }
  
  public String getTargetType() {
   return targetType;
  }
  
  public String getShareReloadUrl() {
    return shareReloadUrl;
  }

  public boolean isEnableShare() {
    return enableShare;
  }
  
  public boolean isDeleteModels() {
    return deleteModels;
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
