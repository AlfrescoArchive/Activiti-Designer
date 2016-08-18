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
/**
 * 
 */
package org.activiti.designer.eclipse.preferences;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class ActivitiCloudEditorPreferencesPageInitializer extends AbstractPreferenceInitializer {
  
  public ActivitiCloudEditorPreferencesPageInitializer() {
  }

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = PreferencesUtil.getActivitiDesignerPreferenceStore(ActivitiPlugin.getDefault());

    // CMIS settings
    store.setDefault(Preferences.ACTIVITI_CLOUD_EDITOR_URL.getPreferenceId(), "https://activiti.alfresco.com/activiti-app");
    store.setDefault(Preferences.ACTIVITI_CLOUD_EDITOR_USERNAME.getPreferenceId(), "YOUR_EMAIL");
    store.setDefault(Preferences.ACTIVITI_CLOUD_EDITOR_PASSWORD.getPreferenceId(), "password");
  }
}
