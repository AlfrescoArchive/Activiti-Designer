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
package org.activiti.designer.preferences;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.editor.ModifiableListEditor;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class AlfrescoPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public AlfrescoPreferencesPage() {
    super(GRID);
  }

  public void createFieldEditors() {

    Group group = new Group(getFieldEditorParent(), SWT.BORDER);
    group.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false, 2, 1));

    addField(new BooleanFieldEditor(Preferences.ALFRESCO_ENABLE.getPreferenceId(),
            "&Enable Alfresco elements", group));

    Group groupStartEvent = new Group(getFieldEditorParent(), SWT.BORDER);
    groupStartEvent.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false, 2, 1));
    addField(new ModifiableListEditor(Preferences.ALFRESCO_FORMTYPES_STARTEVENT.getPreferenceId(), 
            "&Start event form types", groupStartEvent) {
      
      @Override
      protected String[] parseString(String input) {
        if(input != null && input.length() > 0) {
          return input.split(";");
        }
        return new String[] {};
      }
      
      @Override
      protected String getNewInputObject() {
        InputDialog entryDialog = new InputDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "New entry", "New entry:", null, null);
        if (entryDialog.open() == InputDialog.OK) {
            return entryDialog.getValue();
        }
        return null;
      }
      
      @Override
      protected String getModifiedEntry(String original) {
        InputDialog entryDialog = new InputDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "New entry", "New entry:", original, null);
        if (entryDialog.open() == InputDialog.OK) {
            return entryDialog.getValue();
        }
        return null;
      }
      
      @Override
      protected String createList(String[] outputArray) {
        StringBuilder output = new StringBuilder();
        if(outputArray != null && outputArray.length > 0) {
          for (String string : outputArray) {
            if(output.length() > 0) {
              output.append(";");
            }
            output.append(string);
          }
        }
        return output.toString();
      }
    });
    
    Group groupUsertask = new Group(getFieldEditorParent(), SWT.BORDER);
    groupUsertask.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false, 2, 1));
    addField(new ModifiableListEditor(Preferences.ALFRESCO_FORMTYPES_USERTASK.getPreferenceId(), 
            "&User task form types", groupUsertask) {
      
      @Override
      protected String[] parseString(String input) {
        if(input != null && input.length() > 0) {
          return input.split(";");
        }
        return new String[] {};
      }
      
      @Override
      protected String getNewInputObject() {
        InputDialog entryDialog = new InputDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "New entry", "New entry:", null, null);
        if (entryDialog.open() == InputDialog.OK) {
            return entryDialog.getValue();
        }
        return null;
      }
      
      @Override
      protected String getModifiedEntry(String original) {
        InputDialog entryDialog = new InputDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "New entry", "New entry:", original, null);
        if (entryDialog.open() == InputDialog.OK) {
            return entryDialog.getValue();
        }
        return null;
      }
      
      @Override
      protected String createList(String[] outputArray) {
        StringBuilder output = new StringBuilder();
        if(outputArray != null && outputArray.length > 0) {
          for (String string : outputArray) {
            if(output.length() > 0) {
              output.append(";");
            }
            output.append(string);
          }
        }
        return output.toString();
      }
    });
  }
  @Override
  public void init(IWorkbench workbench) {
    IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
    setPreferenceStore(prefStore);
    setDescription("Alfresco extension settings");
    setTitle("Alfresco extension settings");
  }

}
