/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.designer.property.custom;

import org.activiti.designer.integration.servicetask.annotation.Help;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Dialog that enables the user to provide a value for a multiline text field.
 * 
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 */
public class MultilineTextDialog extends Dialog {

  private static final int SPACING = 10;

  private Help help;
  private FormToolkit toolkit;
  private Composite composite;
  private String originalValue;
  private String value;
  private Text textControl;

  public MultilineTextDialog(Shell parentShell, Help help, final String originalValue) {
    super(parentShell);
    this.help = help;
    this.originalValue = originalValue;
    this.toolkit = new FormToolkit(parentShell.getDisplay());
  }

  @Override
  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText("Specify text");
  }

  @Override
  protected Control createDialogArea(Composite parent) {

    this.composite = (Composite) super.createDialogArea(parent);
    composite.setBackground(ColorConstants.white);

    final FormLayout formLayout = new FormLayout();
    composite.setLayout(formLayout);

    FormData data;

    final Label instructionLabel = toolkit.createLabel(composite, "Specify a value for the text");
    data = new FormData();
    data.top = new FormAttachment(composite, SPACING);
    data.left = new FormAttachment(composite, SPACING);
    data.right = new FormAttachment(100, -SPACING);
    instructionLabel.setLayoutData(data);

    Control previousAnchor = instructionLabel;

    if (this.help != null) {
      final Label helpShort = toolkit.createLabel(composite, help.displayHelpShort());
      data = new FormData();
      data.top = new FormAttachment(previousAnchor, SPACING);
      data.left = new FormAttachment(composite, SPACING);
      data.right = new FormAttachment(100, -SPACING);
      helpShort.setLayoutData(data);
      previousAnchor = helpShort;

      final Label helpLong = toolkit.createLabel(composite, help.displayHelpLong(), SWT.WRAP);
      data = new FormData();
      data.top = new FormAttachment(previousAnchor, SPACING);
      data.left = new FormAttachment(composite, SPACING);
      data.right = new FormAttachment(100, -SPACING);
      helpLong.setLayoutData(data);
      previousAnchor = helpLong;
    }

    textControl = toolkit.createText(composite, originalValue, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER_SOLID);
    textControl.setEnabled(true);

    data = new FormData();
    data.top = new FormAttachment(previousAnchor, SPACING);
    data.left = new FormAttachment(composite, SPACING);
    data.right = new FormAttachment(100, -SPACING);
    data.height = 120;
    textControl.setLayoutData(data);

    return composite;
  }
  @Override
  protected void okPressed() {
    // store the value from the spinners so it can be set in the text control
    value = textControl.getText();
    super.okPressed();
  }

  public String getValue() {
    return value;
  }

}
