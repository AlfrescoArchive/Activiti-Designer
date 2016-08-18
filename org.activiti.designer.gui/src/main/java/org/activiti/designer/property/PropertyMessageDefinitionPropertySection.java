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
package org.activiti.designer.property;

import org.activiti.designer.property.ui.MessageDefinitionEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyMessageDefinitionPropertySection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	protected MessageDefinitionEditor messageEditor;

	@Override
	public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
		Composite messageComposite = getWidgetFactory().createComposite(formComposite, SWT.WRAP);
		FormData data = new FormData();
		data.left = new FormAttachment(0, 150);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		messageComposite.setLayoutData(data);
		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.numColumns = 1;
		messageComposite.setLayout(layout);
		messageEditor = new MessageDefinitionEditor("messageEditor", messageComposite);
		messageEditor.getLabelControl(messageComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		CLabel dataPropertiesLabel = getWidgetFactory().createCLabel(formComposite, "Message definitions:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(messageComposite, -HSPACE);
		data.top = new FormAttachment(messageComposite, 0, SWT.TOP);
		dataPropertiesLabel.setLayoutData(data);
	}

	@Override
	public void refresh() {
	  if (getSelectedPictogramElement() != null) {
	    messageEditor.diagram = getDiagram();
  	  messageEditor.editingDomain = getDiagramContainer().getDiagramBehavior().getEditingDomain();
  	  messageEditor.initialize(getModel(getSelectedPictogramElement()).getBpmnModel().getMessages());
	  }
	}

	@Override
	protected Object getModelValueForControl(Control control, Object businessObject) {
		return null;
	}

	@Override
	protected void storeValueInModel(Control control, Object businessObject) {
	    // nothing to do
	}
}
