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

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.kickstart.eclipse.common.KickstartPlugin;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Utilities for working with preferences.
 * 
 * @author Tiese Barrell
 * @version 2
 * @since 0.5.1
 * 
 */
public final class PreferencesUtil {

  /**
	 * 
	 */
  private PreferencesUtil() {
  }

  /**
   * Gets the preference store for the Activiti designer.
   * 
   * @return the preference store
   */
  public static final IPreferenceStore getActivitiDesignerPreferenceStore() {
    return KickstartPlugin.getDefault().getPreferenceStore();
  }

  /**
   * Gets a string preference's value from the preference store.
   * 
   * @param preference
   *          the {@link Preferences} to get
   * @return the value of the string or null if no value is stored for the
   *         preference
   */
  public static final String getStringPreference(final Preferences preference) {
    final IPreferenceStore store = KickstartPlugin.getDefault().getPreferenceStore();
    return store.getString(preference.getPreferenceId());
  }

  /**
   * Gets a boolean preference's value from the preference store.
   * 
   * @param preference
   *          the {@link Preferences} to get
   * @return true if the preference is stored as true, otherwise false and false
   *         if there is no preference applied
   */
  public static final boolean getBooleanPreference(final Preferences preference) {
    final IPreferenceStore store = KickstartPlugin.getDefault().getPreferenceStore();
    return store.getBoolean(preference.getPreferenceId());
  }

  /**
   * Gets a boolean preference's value from the preference store. This method is
   * intended for dynamic preference ids only. If possible, you should use
   * {@link #getBooleanPreference(Preferences)} instead.
   * 
   * @param preferenceId
   *          the id of the preferences to get
   * @return true if the preference is stored as true, otherwise false and false
   *         if there is no preference applied
   */
  public static final boolean getBooleanPreference(final String preferenceId) {
    final IPreferenceStore store = KickstartPlugin.getDefault().getPreferenceStore();
    return store.getBoolean(preferenceId);
  }
  
  public static final List<String> getStringArray(final Preferences preferenceName) {
    List<String> resultList = new ArrayList<String>();
    String defaultFormTypes = PreferencesUtil.getStringPreference(preferenceName);
    if(defaultFormTypes != null && defaultFormTypes.length() > 0) {
      String[] formList = defaultFormTypes.split("�");
      if (formList != null) {
        for (String formKey : formList) {
          resultList.add(formKey);
        }
      }
    }
    return resultList;
  }
}
