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

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.editor.ModifiableListEditor;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class ActivitiLanguagePreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	protected List<String> languages = new ArrayList<String>();
	protected RadioGroupFieldEditor defaultLanguageEditor = null;
	
  public ActivitiLanguagePreferencesPage() {
    super(GRID);
  }

  public void createFieldEditors() {

    Group languageGroup = new Group(getFieldEditorParent(), SWT.BORDER);
    languageGroup.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false, 2, 1));
    
    ModifiableListEditor languageEditor = new ModifiableListEditor(Preferences.ACTIVITI_LANGUAGES.getPreferenceId(), "&Languages", languageGroup) {
      
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
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "New entry", "New entry:", null, null);
        
        if (entryDialog.open() == InputDialog.OK) {
            String value = removeInvalidCharacters(entryDialog.getValue());
            String[] existingLanguages = getList().getItems();
            boolean containsLanguage = false;
            if (existingLanguages != null) {
              for (String existing : existingLanguages) {
                if (existing.equals(value)) {
                  containsLanguage = true;
                  break;
                }
              }
            }
            if (containsLanguage == false) {
              return value;
            }
        }
        return null;
      }
      
      @Override
      protected String getModifiedEntry(String original) {
        InputDialog entryDialog = new InputDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Update entry", "Update entry:", original, null);
        
        if (entryDialog.open() == InputDialog.OK) {
            String value = removeInvalidCharacters(entryDialog.getValue());
            String[] existingLanguages = getList().getItems();
            boolean containsLanguage = false;
            if (existingLanguages != null) {
              for (String existing : existingLanguages) {
                if (existing.equals(value)) {
                  containsLanguage = true;
                  break;
                }
              }
            }
            if (containsLanguage == false) {
              return value;
            }
        }
        return null;
      }
      
      @Override
      protected String createList(String[] outputArray) {
        StringBuilder output = new StringBuilder();
        if (outputArray != null && outputArray.length > 0) {
          for (String string : outputArray) {
            if(output.length() > 0) {
              output.append(";");
            }
            output.append(string);
          }
        }
        return output.toString();
      }
    };
    
    addField(languageEditor);
    
    languages = PreferencesUtil.getStringArray(Preferences.ACTIVITI_LANGUAGES, ActivitiPlugin.getDefault());
    String[][] languageArray = null;
    if (languages != null) {
      languageArray = new String[languages.size()][2];
      for (int i = 0; i < languages.size(); i++) {
        languageArray[i] = new String[] {languages.get(i), languages.get(i)};
      }
    } else {
      languageArray = new String[0][2];
    }
    
    Group defaultLanguageGroup = new Group(getFieldEditorParent(), SWT.BORDER);
    defaultLanguageGroup.setText("Default language");
    defaultLanguageEditor = new RadioGroupFieldEditor(Preferences.ACTIVITI_DEFAULT_LANGUAGE.getPreferenceId(), 
        "", 4, languageArray, defaultLanguageGroup, true);
    addField(defaultLanguageEditor);
  }
  
  @Override
  public void init(IWorkbench workbench) {
    IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
    setPreferenceStore(prefStore);
    setDescription("Activiti language settings");
    setTitle("Activiti language settings");
  }
  
  protected String removeInvalidCharacters(String value) {
    value = value.replace(":", "");
    value = value.replace(";", "");
    value = value.replace("{", "");
    value = value.replace("}", "");
    value = value.replace("^", "");
    value = value.replace("&", "");
    value = value.replace("@", "");
    return value;
  }

}
