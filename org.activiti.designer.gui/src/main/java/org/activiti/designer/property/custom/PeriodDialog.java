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
import org.activiti.designer.property.extension.util.ExtensionPropertyUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Dialog that enables the user to provide a value for a period field.
 * 
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 */
public class PeriodDialog extends Dialog {

  private Help help;
  private FormToolkit toolkit;
  private Composite composite;
  private String originalValue;
  private String value;

  public PeriodDialog(Shell parentShell, Help help, final String originalValue) {
    super(parentShell);
    this.help = help;
    this.originalValue = originalValue;
    this.toolkit = new FormToolkit(parentShell.getDisplay());
  }

  @Override
  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText("Specify period");
  }

  @Override
  protected Control createDialogArea(Composite parent) {

    this.composite = (Composite) super.createDialogArea(parent);
    composite.setBackground(ColorConstants.white);

    final int numberOfColumns = PeriodPropertyElement.values().length * 2;

    final GridLayout gridLayout = new GridLayout(numberOfColumns, false);
    composite.setLayout(gridLayout);

    GridData data;

    final Label instructionLabel = toolkit.createLabel(composite, "Specify a value for the period");
    data = new GridData();
    data.horizontalSpan = numberOfColumns;
    instructionLabel.setLayoutData(data);

    if (this.help != null) {
      final Label helpShort = toolkit.createLabel(composite, help.displayHelpShort());
      data = new GridData();
      data.horizontalSpan = numberOfColumns;
      helpShort.setLayoutData(data);

      final Label helpLong = toolkit.createLabel(composite, help.displayHelpLong());
      data = new GridData();
      data.horizontalSpan = numberOfColumns;
      helpLong.setLayoutData(data);
    }

    int i = 0;

    PeriodPropertyElement[] properties = PeriodPropertyElement.values();

    for (final PeriodPropertyElement element : properties) {

      final Spinner spinner = new Spinner(composite, SWT.BORDER);

      spinner.setData("PERIOD_KEY", element.getShortFormat());
      if (StringUtils.isNotBlank(originalValue)) {
        spinner.setSelection(ExtensionPropertyUtil.getPeriodPropertyElementFromValue(originalValue, element));
      }
      spinner.setEnabled(true);
      data = new GridData();
      data.widthHint = 30;
      spinner.setLayoutData(data);

      String labelText = element.getLongFormat();
      if (i != properties.length - 1) {
        labelText += " ,  ";
      }

      Label labelShort = toolkit.createLabel(composite, labelText, SWT.NONE);
      labelShort.setToolTipText(element.getLongFormat());

      i++;
    }
    return composite;
  }
  @Override
  protected void okPressed() {
    // store the value from the spinners so it can be set in the text control
    value = ExtensionPropertyUtil.getPeriodValueFromParent(composite);
    super.okPressed();
  }

  public String getValue() {
    return value;
  }

}
