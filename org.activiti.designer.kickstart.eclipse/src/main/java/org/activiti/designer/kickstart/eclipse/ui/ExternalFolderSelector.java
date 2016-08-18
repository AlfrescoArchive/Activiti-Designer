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
package org.activiti.designer.kickstart.eclipse.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Wrapper class around a component that allows selecting an external folder and displaying this in an
 * editable textfield. Action widget to use exposed in {@link #getComposite()}.
 * 
 * @author Frederik Heremans
 */
public class ExternalFolderSelector {

  private Text text;
  private Button browseButton;
  private Composite composite;
  private Label label;
  
  private String currentPath;
  
  public ExternalFolderSelector(Composite parent, String labelText) {
    int cols = labelText != null ? 3 : 2;
    composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(cols, false));

    if(labelText != null) {
      label = new Label(composite, SWT.NONE);
      label.setText(labelText);
      GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
      data.widthHint = 150;
      label.setLayoutData(data);
    }
    
    GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
    text = new Text(composite, SWT.BORDER | SWT.SINGLE);
    text.setLayoutData(data);
    
    browseButton = new Button(composite, SWT.PUSH);
    browseButton.setText("Browse");
    data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
    browseButton.setLayoutData(data);
    
    browseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        DirectoryDialog dialog = new  DirectoryDialog(getComposite().getShell());
        dialog.setText("Select a folder");
        
        if(text.getText() != null) {
          dialog.setFilterPath(text.getText());
        }
        setCurrentPath(dialog.open());
      }
    });
  }
  
  
  public ExternalFolderSelector(Composite parent) {
    this(parent, null);
  }
  
  public String getCurrentPath() {
    return currentPath;
  }
  
  public void setCurrentPath(String currentPath) {
    this.currentPath = currentPath;
    if(currentPath != null) {
      text.setText(currentPath);
    } else {
      text.setText("");
    }
  }
  
  public Composite getComposite() {
    return composite; 
  }

  public void setEnabled(boolean selection) {
    text.setEnabled(selection);
    browseButton.setEnabled(selection);
  }
}
