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

import java.util.List;

import org.activiti.bpmn.model.ValuedDataObject;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.property.ui.DataPropertyEditor;
import org.activiti.designer.util.BpmnBOUtil;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
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

public class PropertyDataPropertySection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private DataPropertyEditor dataPropertyEditor;

	@Override
	public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
		Composite dataPropertiesComposite = getWidgetFactory().createComposite(formComposite, SWT.WRAP);
		FormData data = new FormData();
		data.left = new FormAttachment(0, 150);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		dataPropertiesComposite.setLayoutData(data);
		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.numColumns = 1;
		dataPropertiesComposite.setLayout(layout);
		dataPropertyEditor = new DataPropertyEditor("dataPropertyEditor", dataPropertiesComposite, (ModelUpdater) this);
		dataPropertyEditor.getLabelControl(dataPropertiesComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		CLabel dataPropertiesLabel = getWidgetFactory().createCLabel(formComposite, "Data properties:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(dataPropertiesComposite, -HSPACE);
		data.top = new FormAttachment(dataPropertiesComposite, 0, SWT.TOP);
		dataPropertiesLabel.setLayoutData(data);
	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = getBusinessObject(pe);

			resetModelUpdater();
			List<ValuedDataObject> dataPropertyList = BpmnBOUtil.getDataObjects(bo, getDiagram());
			dataPropertyEditor.pictogramElement = pe;
			dataPropertyEditor.diagram = getDiagram();
			dataPropertyEditor.initialize(dataPropertyList);
		}
	}

	@Override  
	protected Object getBusinessObject(PictogramElement element) {
		if (element == null)
			return null;

		if (element instanceof Diagram) {
			BpmnMemoryModel model = getModel(element);
			if (model.getBpmnModel().getPools().size() > 0) {
				return model.getBpmnModel().getProcess(model.getBpmnModel().getPools().get(0).getId());
			} else {
				return model.getBpmnModel().getMainProcess();
			}
		}

		ActivitiBPMNFeatureProvider featureProvider = getFeatureProvider(element);
		if( featureProvider != null) {
			return featureProvider.getBusinessObjectForPictogramElement(element);
		}
		return null;
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
