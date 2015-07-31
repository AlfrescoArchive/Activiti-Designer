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
