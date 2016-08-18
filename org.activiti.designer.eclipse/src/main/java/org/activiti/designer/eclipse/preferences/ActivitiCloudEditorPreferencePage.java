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
package org.activiti.designer.eclipse.preferences;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ActivitiCloudEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ActivitiCloudEditorPreferencePage() {
		super(GRID);
	}

	public void createFieldEditors() {
	  getFieldEditorParent().setLayout(new FillLayout());

    addField(new StringFieldEditor(Preferences.ACTIVITI_CLOUD_EDITOR_URL.getPreferenceId(), "&Activiti cloud editor URL", getFieldEditorParent()));
    addField(new StringFieldEditor(Preferences.ACTIVITI_CLOUD_EDITOR_USERNAME.getPreferenceId(), "&Activiti cloud editor username", getFieldEditorParent()));
    addField(new StringFieldEditor(Preferences.ACTIVITI_CLOUD_EDITOR_PASSWORD.getPreferenceId(), "&Activiti cloud editor password", getFieldEditorParent()) {

      @Override
      protected void doFillIntoGrid(Composite parent, int numColumns) {
          super.doFillIntoGrid(parent, numColumns);
  
          getTextControl().setEchoChar('*');
      }
    
    });
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(prefStore);
		setDescription("Activiti cloud editor settings");
    setTitle("Activiti cloud editor settings");
	}
}
