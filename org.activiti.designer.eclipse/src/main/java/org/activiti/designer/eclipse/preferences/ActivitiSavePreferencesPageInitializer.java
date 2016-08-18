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

/**
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 2
 * 
 */
public class ActivitiSavePreferencesPageInitializer extends AbstractPreferenceInitializer {

  private static final String FORMTYPES_STARTEVENT = "wf:submitAdhocTask;wf:submitReviewTask;wf:submitGroupReviewTask;wf:submitParallelReviewTask";
  private static final String FORMTYPES_USERTASK = "wf:adhocTask;wf:completedAdhocTask;wf:activitiReviewTask;wf:approvedTask;wf:rejectedTask;wf:approvedParallelTask;wf:rejectedParallelTask";

  public ActivitiSavePreferencesPageInitializer() {
  }

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = PreferencesUtil.getActivitiDesignerPreferenceStore(ActivitiPlugin.getDefault());

    // BPMN 2 Marshaller
    store.setDefault(Preferences.ALFRESCO_ENABLE.getPreferenceId(), true);

    store.setDefault(Preferences.ALFRESCO_FORMTYPES_STARTEVENT.getPreferenceId(), FORMTYPES_STARTEVENT);

    store.setDefault(Preferences.ALFRESCO_FORMTYPES_USERTASK.getPreferenceId(), FORMTYPES_USERTASK);

    // Image Marshaller
    store.setDefault(Preferences.SAVE_IMAGE.getPreferenceId(), false);

    // Overlay preferences
    store.setDefault(Preferences.SAVE_IMAGE_ADD_OVERLAY.getPreferenceId(), false);
    store.setDefault(Preferences.SAVE_IMAGE_ADD_OVERLAY_TEXT_COLOR.getPreferenceId(), "0,0,0");
    store.setDefault(Preferences.SAVE_IMAGE_ADD_OVERLAY_BORDER_COLOR.getPreferenceId(), "0,0,0");
    store.setDefault(Preferences.SAVE_IMAGE_ADD_OVERLAY_BACKGROUND_COLOR.getPreferenceId(), "255,255,255");

  }
}
