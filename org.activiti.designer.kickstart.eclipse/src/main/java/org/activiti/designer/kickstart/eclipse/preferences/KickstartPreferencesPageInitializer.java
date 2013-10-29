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
  }
}
