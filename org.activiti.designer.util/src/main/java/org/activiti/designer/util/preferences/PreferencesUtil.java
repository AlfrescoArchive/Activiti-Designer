/**
 * 
 */
package org.activiti.designer.util.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

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
  public static final IPreferenceStore getActivitiDesignerPreferenceStore(AbstractUIPlugin plugin) {
    return plugin.getPreferenceStore();
  }

  /**
   * Gets a string preference's value from the preference store.
   * 
   * @param preference
   *          the {@link Preferences} to get
   * @return the value of the string or null if no value is stored for the
   *         preference
   */
  public static final String getStringPreference(final Preferences preference, AbstractUIPlugin plugin) {
    final IPreferenceStore store = plugin.getPreferenceStore();
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
  public static final boolean getBooleanPreference(final Preferences preference, AbstractUIPlugin plugin) {
    final IPreferenceStore store = plugin.getPreferenceStore();
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
  public static final boolean getBooleanPreference(final String preferenceId, AbstractUIPlugin plugin) {
    final IPreferenceStore store = plugin.getPreferenceStore();
    return store.getBoolean(preferenceId);
  }

  public static final List<String> getStringArray(final Preferences preferenceName, AbstractUIPlugin plugin) {
    List<String> resultList = new ArrayList<String>();
    String arrayString = getStringPreference(preferenceName, plugin);
    if (arrayString != null && arrayString.length() > 0) {
      String[] stringArray = arrayString.split(";");
      if (stringArray != null) {
        for (String key : stringArray) {
          resultList.add(key);
        }
      }
    }
    return resultList;
  }
}
