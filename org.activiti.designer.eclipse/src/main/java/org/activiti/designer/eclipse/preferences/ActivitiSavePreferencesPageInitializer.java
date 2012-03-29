/**
 * 
 */
package org.activiti.designer.eclipse.preferences;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 2
 * 
 */
public class ActivitiSavePreferencesPageInitializer extends AbstractPreferenceInitializer {
  
  private static final String FORMTYPES_STARTEVENT = "wf:submitAdhocTask±wf:submitReviewTask±wf:submitGroupReviewTask±wf:submitParallelReviewTask";
  private static final String FORMTYPES_USERTASK = "wf:adhocTask±wf:completedAdhocTask±wf:activitiReviewTask±wf:approvedTask±wf:rejectedTask±wf:approvedParallelTask±wf:rejectedParallelTask";

  public ActivitiSavePreferencesPageInitializer() {
  }

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = PreferencesUtil.getActivitiDesignerPreferenceStore();

    // BPMN 2 Marshaller
    store.setDefault(PreferencesUtil.getExportMarshallerPreferenceId(ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_NAME), true);
    
    store.setDefault(Preferences.ALFRESCO_ENABLE.getPreferenceId(), true);
    
    store.setDefault(Preferences.ALFRESCO_FORMTYPES_STARTEVENT.getPreferenceId(), FORMTYPES_STARTEVENT);
    
    store.setDefault(Preferences.ALFRESCO_FORMTYPES_USERTASK.getPreferenceId(), FORMTYPES_USERTASK);

    // BPMN 2 Validator
    store.setDefault(Preferences.VALIDATE_ACTIVITI_BPMN_FORMAT.getPreferenceId(), true);

    // Image Marshaller
    store.setDefault(PreferencesUtil.getExportMarshallerPreferenceId(ActivitiBPMNDiagramConstants.IMAGE_MARSHALLER_NAME), true);

  }
}
