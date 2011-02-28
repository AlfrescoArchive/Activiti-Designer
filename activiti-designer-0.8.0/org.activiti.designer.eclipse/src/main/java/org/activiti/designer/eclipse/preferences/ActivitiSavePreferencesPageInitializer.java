/**
 * 
 */
package org.activiti.designer.eclipse.preferences;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 2
 * 
 */
public class ActivitiSavePreferencesPageInitializer extends AbstractPreferenceInitializer {

  public ActivitiSavePreferencesPageInitializer() {
  }

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = PreferencesUtil.getActivitiDesignerPreferenceStore();

    // BPMN 2 Marshaller
    store.setDefault(PreferencesUtil.getExportMarshallerPreferenceId(ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_NAME), true);

    // BPMN 2 Validator
    store.setDefault(Preferences.VALIDATE_ACTIVITI_BPMN_FORMAT.getPreferenceId(), true);

    // BPMN 2 Marshaller behavior for validation failures
    store.setDefault(Preferences.SKIP_BPMN_MARSHALLER_ON_VALIDATION_FAILURE.getPreferenceId(), ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_VALIDATION_SKIP);

    // Image Marshaller
    store.setDefault(PreferencesUtil.getExportMarshallerPreferenceId(ActivitiBPMNDiagramConstants.IMAGE_MARSHALLER_NAME), true);

  }
}
