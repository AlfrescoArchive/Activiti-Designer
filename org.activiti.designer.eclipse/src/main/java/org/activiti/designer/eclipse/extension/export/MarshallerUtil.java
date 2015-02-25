/**
 * 
 */
package org.activiti.designer.eclipse.extension.export;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
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
public final class MarshallerUtil {

  /**
	 * 
	 */
  private MarshallerUtil() {
  }

  /**
   * Returns the preference id to be used for an {@link ExportMarshaller}
   * extension.
   * 
   * @param marshaller
   *          the {@link ExportMarshaller} to get the property for
   * @return the id of the preference
   */
  public static final String getPreferenceId(final ExportMarshaller marshaller) {
    return getExportMarshallerPreferenceId(marshaller.getMarshallerName());
  }

  /**
   * Returns the preference id to be used for an {@link ExportMarshaller}
   * extension based on the marshaller's name.
   * 
   * @param marshallerName
   *          the name of the {@link ExportMarshaller} to get the property for
   * @return the id of the preference
   */
  public static final String getExportMarshallerPreferenceId(final String marshallerName) {
    return Preferences.SAVE_TO_FORMAT.getPreferenceId() + "." + marshallerName;
  }
}
