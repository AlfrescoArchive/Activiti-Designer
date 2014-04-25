/**
 * 
 */
package org.activiti.designer.eclipse.preferences;

import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class ActivitiCloudEditorPreferencesPageInitializer extends AbstractPreferenceInitializer {
  
  public ActivitiCloudEditorPreferencesPageInitializer() {
  }

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = PreferencesUtil.getActivitiDesignerPreferenceStore();

    // CMIS settings
    store.setDefault(Preferences.ACTIVITI_CLOUD_EDITOR_URL.getPreferenceId(), "https://activiti.alfresco.com");
    store.setDefault(Preferences.ACTIVITI_CLOUD_EDITOR_USERNAME.getPreferenceId(), "YOUR_EMAIL");
    store.setDefault(Preferences.ACTIVITI_CLOUD_EDITOR_PASSWORD.getPreferenceId(), "password");
  }
}
