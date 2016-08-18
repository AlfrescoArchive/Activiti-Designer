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

package org.activiti.designer.property.extension.field;

import java.lang.reflect.Field;

import org.activiti.bpmn.model.Task;
import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.property.AbstractPropertyCustomTaskSection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * @author Tiese Barrell
 * @since 0.7.0
 * @version 1
 */
public class CustomPropertyBooleanChoiceField extends AbstractCustomPropertyField {

  private Button buttonControl;

  public CustomPropertyBooleanChoiceField(final AbstractPropertyCustomTaskSection section, final Task task, final Field field) {
    super(section, task, field);
  }

  @Override
  public PropertyType getPrimaryPropertyType() {
    return PropertyType.BOOLEAN_CHOICE;
  }

  @Override
  public void refresh() {
    buttonControl.setSelection(Boolean.parseBoolean(getSimpleValueOrDefault()));
  }

  @Override
  public String getSimpleValue() {
    return Boolean.toString(buttonControl.getSelection());
  }

  @Override
  public Composite render(final Composite parent, final TabbedPropertySheetWidgetFactory factory, final FocusListener listener) {

    final Composite result = factory.createFlatFormComposite(parent);
    FormData data;

    buttonControl = factory.createButton(result, "", SWT.CHECK | SWT.BORDER_SOLID);
    buttonControl.setEnabled(true);

    if (getPropertyAnnotation().fieldValidator() != null) {
      addFieldValidator(buttonControl, getPropertyAnnotation().fieldValidator());
    }

    buttonControl.addSelectionListener(new CheckboxChangeListener(listener));

    data = new FormData();
    data.left = new FormAttachment(0);
    data.top = new FormAttachment(0);
    data.right = new FormAttachment(100);
    buttonControl.setLayoutData(data);

    return result;
  }

  private class CheckboxChangeListener implements SelectionListener {

    private final FocusListener delegate;

    public CheckboxChangeListener(final FocusListener delegate) {
      super();
      this.delegate = delegate;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
      handleSelection(e);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
      handleSelection(e);
    }

    private void handleSelection(SelectionEvent e) {

      Event propagatedEvent = new Event();
      propagatedEvent.item = e.item;
      propagatedEvent.widget = buttonControl;
      propagatedEvent.type = SWT.BUTTON1;

      delegate.focusLost(new FocusEvent(propagatedEvent));
    }

  }
}
