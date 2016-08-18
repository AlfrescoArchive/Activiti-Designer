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
package org.activiti.designer.kickstart.eclipse.preferences;

import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class KickstartPreferencesPageInitializer extends AbstractPreferenceInitializer {
  
  public KickstartPreferencesPageInitializer() {
  }

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = PreferencesUtil.getActivitiDesignerPreferenceStore();

    // CMIS settings
    store.setDefault(Preferences.CMIS_URL.getPreferenceId(), "http://localhost:8080/alfresco/service/cmis");
    store.setDefault(Preferences.CMIS_USERNAME.getPreferenceId(), "admin");
    store.setDefault(Preferences.CMIS_PASSWORD.getPreferenceId(), "admin");
    
    // Export 
    store.setDefault(Preferences.PROCESS_EXPORT_TYPE.getPreferenceId(), Preferences.PROCESS_EXPORT_TYPE_TARGET);
    store.setDefault(Preferences.CMIS_WORKFLOW_DEFINITION_PATH.getPreferenceId(), "/Data Dictionary/Workflow Definitions");
    store.setDefault(Preferences.CMIS_MODELS_PATH.getPreferenceId(), "/Data Dictionary/Models");
    store.setDefault(Preferences.CMIS_MODELS_DELETE.getPreferenceId(), Boolean.TRUE);
    store.setDefault(Preferences.CMIS_SHARE_CONFIG_PATH.getPreferenceId(), "/Sites/surf-config");
    store.setDefault(Preferences.PROCESS_TARGET_LOCATION_REPOSITORY.getPreferenceId(), ".../shared/classes/alfresco/extension");
    store.setDefault(Preferences.PROCESS_TARGET_LOCATION_SHARE.getPreferenceId(), ".../shared/classes/alfresco/web-extension");
    store.setDefault(Preferences.SHARE_RELOAD_URL.getPreferenceId(), "http://localhost:8081/share/service/reload-module-deployments");
    store.setDefault(Preferences.SHARE_ENABLED.getPreferenceId(), Boolean.TRUE);
    
  }
}
