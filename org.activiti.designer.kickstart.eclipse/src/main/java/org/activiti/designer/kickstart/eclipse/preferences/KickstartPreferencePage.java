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
package org.activiti.designer.kickstart.eclipse.preferences;

import org.activiti.designer.kickstart.eclipse.common.KickstartPlugin;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class KickstartPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public KickstartPreferencePage() {
		super(GRID);
	}

	public void createFieldEditors() {
	  getFieldEditorParent().setLayout(new FillLayout());

    addField(new StringFieldEditor(Preferences.CMIS_URL.getPreferenceId(), "&CMIS URL", getFieldEditorParent()));
    addField(new StringFieldEditor(Preferences.CMIS_USERNAME.getPreferenceId(), "&CMIS username", getFieldEditorParent()));
    addField(new StringFieldEditor(Preferences.CMIS_PASSWORD.getPreferenceId(), "&CMIS password", getFieldEditorParent()) {

      @Override
      protected void doFillIntoGrid(Composite parent, int numColumns) {
          super.doFillIntoGrid(parent, numColumns);
  
          getTextControl().setEchoChar('*');
      }
    
    });
    
    addSeparator();
    
    addField(new StringFieldEditor(Preferences.CMIS_WORKFLOW_DEFINITION_PATH.getPreferenceId(), "CMIS Workflow Definitions Path", getFieldEditorParent()));
    addField(new StringFieldEditor(Preferences.CMIS_MODELS_PATH.getPreferenceId(), "CMIS Models Path", getFieldEditorParent()));
    addField(new BooleanFieldEditor(Preferences.CMIS_MODELS_DELETE.getPreferenceId(), "Delete and recreate model", getFieldEditorParent()));
    addField(new StringFieldEditor(Preferences.CMIS_SHARE_CONFIG_PATH.getPreferenceId(), "CMIS Share config Path", getFieldEditorParent()));
    addField(new StringFieldEditor(Preferences.SHARE_RELOAD_URL.getPreferenceId(), "Share reload webscript URL", getFieldEditorParent()));
    addField(new BooleanFieldEditor(Preferences.SHARE_ENABLED.getPreferenceId(), "Share reload enabled", getFieldEditorParent()));
    
    addSeparator();
    
    addField(new StringFieldEditor(Preferences.PROCESS_TARGET_LOCATION_REPOSITORY.getPreferenceId(), "Custom repo tomcat folder", getFieldEditorParent()));
    addField(new StringFieldEditor(Preferences.PROCESS_TARGET_LOCATION_SHARE.getPreferenceId(), "Custom share tomcat folder", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore prefStore = KickstartPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(prefStore);
		setDescription("Kickstart settings");
    setTitle("Kickstart settings");
	}
	
	public void addSeparator()
    {
        Label spacer = new Label( getFieldEditorParent(), SWT.SEPARATOR
                                                          | SWT.HORIZONTAL );
        GridData spacerData = new GridData( GridData.FILL_HORIZONTAL );
        spacerData.horizontalSpan = 3;
        spacer.setLayoutData( spacerData );
    }
}
