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
package org.activiti.designer.property.ui;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.FormValue;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.property.ModelUpdater;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

public class FormPropertyEditor extends TableFieldEditor {

	protected Composite parent;
	protected ModelUpdater modelUpdater;
	public PictogramElement pictogramElement;

	public FormPropertyEditor(String key, Composite parent, ModelUpdater modelUpdater) {

		super(key, "",
				new String[] { "Id", "Name", "Type", "Expression", "Variable", "Default", "Pattern", "Required",
						"Readable", "Writeable", "Form values" },
				new int[] { 60, 100, 60, 100, 80, 80, 60, 60, 60, 60, 120 }, parent);
		this.parent = parent;
		this.modelUpdater = modelUpdater;
	}

	public void initialize(List<FormProperty> formPropertyList) {
		removeTableItems();
		if (formPropertyList == null || formPropertyList.size() == 0)
			return;
		for (FormProperty formProperty : formPropertyList) {
			addTableItem(formProperty);
		}
	}

	@Override
	protected boolean isTableChangeEnabled() {
		return false;
	}

	@Override
	protected String createList(String[][] items) {
		return null;
	}

	@Override
	protected String[][] parseString(String string) {
		return null;
	}

	protected void addTableItem(FormProperty formProperty) {

		if (table != null) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(0, formProperty.getId());
			tableItem.setText(1, formProperty.getName() != null ? formProperty.getName() : "");
			tableItem.setText(2, formProperty.getType() != null ? formProperty.getType() : "");
			tableItem.setText(3, formProperty.getExpression() != null ? formProperty.getExpression() : "");
			tableItem.setText(4, formProperty.getVariable() != null ? formProperty.getVariable() : "");
			tableItem.setText(5,
					formProperty.getDefaultExpression() != null ? formProperty.getDefaultExpression() : "");
			tableItem.setText(6, formProperty.getDatePattern() != null ? formProperty.getDatePattern() : "");
			tableItem.setText(7, "" + formProperty.isRequired());
			tableItem.setText(8, "" + formProperty.isReadable());
			tableItem.setText(9, "" + formProperty.isWriteable());

			StringBuilder formValuesString = new StringBuilder();
			for (int i = 0; i < formProperty.getFormValues().size(); i++) {
				FormValue formValue = formProperty.getFormValues().get(i);
				if (i > 0) {
					formValuesString.append(";");
				}
				formValuesString.append(formValue.getId()).append(":").append(formValue.getName());
			}
			tableItem.setText(10, formValuesString.toString());
			tableItem.setData("formValues", formProperty.getFormValues());
		}
	}

	@Override
	protected String[] getNewInputObject() {
		FormPropertyDialog dialog = new FormPropertyDialog(parent.getShell(), getItems());
		dialog.open();
		if (dialog.id != null && dialog.id.length() > 0) {
			createNewFormProperty(dialog);
			return new String[] { dialog.id, dialog.name, dialog.type, dialog.expression, dialog.variable,
					dialog.defaultExpression, dialog.datePattern, dialog.required.toLowerCase(),
					dialog.readable.toLowerCase(), dialog.writeable.toLowerCase(), dialog.formValues.toString() };
		} else {
			return null;
		}
	}

	@Override
	protected String[] getChangedInputObject(TableItem item) {
		int index = table.getSelectionIndex();
		FormPropertyDialog dialog = new FormPropertyDialog(parent.getShell(), getItems(), item.getText(0),
				item.getText(1), item.getText(2), item.getText(3), item.getText(4), item.getText(5), item.getText(6),
				item.getText(7), item.getText(8), item.getText(9), (List<FormValue>) item.getData("formValues"));
		dialog.open();
		if (dialog.id != null && dialog.id.length() > 0) {
			saveFormProperty(dialog, index);
			return new String[] { dialog.id, dialog.name, dialog.type, dialog.expression, dialog.variable,
					dialog.defaultExpression, dialog.datePattern, dialog.required.toLowerCase(),
					dialog.readable.toLowerCase(), dialog.writeable.toLowerCase(), dialog.formValues.toString() };
		} else {
			return null;
		}
	}

	@Override
	protected void removedItem(int index) {
		saveRemovedObject(index);
	}

	@Override
	protected void upPressed() {
		final int index = table.getSelectionIndex();
		Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
		FormProperty property = getFormProperties(updatableBo).remove(index);
		getFormProperties(updatableBo).add(index - 1, property);
		modelUpdater.executeModelUpdater();
		super.upPressed();
	}

	@Override
	protected void downPressed() {
		final int index = table.getSelectionIndex();
		Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
		FormProperty property = getFormProperties(updatableBo).remove(index);
		getFormProperties(updatableBo).add(index + 1, property);
		modelUpdater.executeModelUpdater();
		super.downPressed();
	}

	protected List<FormProperty> getFormProperties(Object bo) {
		List<FormProperty> formPropertyList = null;
		if (bo instanceof UserTask) {
			formPropertyList = ((UserTask) bo).getFormProperties();
		} else if (bo instanceof StartEvent) {
			formPropertyList = ((StartEvent) bo).getFormProperties();
		}
		return formPropertyList;
	}

	protected void createNewFormProperty(FormPropertyDialog dialog) {
		if (pictogramElement != null) {
			Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();

			FormProperty property = new FormProperty();
			if (dialog.id != null && dialog.id.length() > 0) {
				copyValuesToFormProperty(dialog, property);
				getFormProperties(updatableBo).add(property);
				modelUpdater.executeModelUpdater();
			}
		}
	}

	protected void saveFormProperty(FormPropertyDialog dialog, int index) {
		if (pictogramElement != null) {
			Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();

			FormProperty property = getFormProperties(updatableBo).get(index);
			if (property != null) {

				if (dialog.id != null && dialog.id.length() > 0) {
					copyValuesToFormProperty(dialog, property);
					modelUpdater.executeModelUpdater();
				}
			}
		}
	}

	protected void saveRemovedObject(int index) {
		if (pictogramElement != null) {
			Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();

			getFormProperties(updatableBo).remove(index);

			modelUpdater.executeModelUpdater();
		}
	}

	protected void copyValuesToFormProperty(FormPropertyDialog dialog, FormProperty property) {
		property.setId(dialog.id);
		property.setName(dialog.name);
		property.setType(dialog.type);
		property.setExpression(dialog.expression);
		property.setVariable(dialog.variable);
		property.setDefaultExpression(dialog.defaultExpression);
		property.setDatePattern(dialog.datePattern);
		if (StringUtils.isNotEmpty(dialog.required)) {
			property.setRequired(Boolean.valueOf(dialog.required.toLowerCase()));
		}
		if (StringUtils.isNotEmpty(dialog.readable)) {
			property.setReadable(Boolean.valueOf(dialog.readable.toLowerCase()));
		}
		if (StringUtils.isNotEmpty(dialog.writeable)) {
			property.setWriteable(Boolean.valueOf(dialog.writeable.toLowerCase()));
		}

		property.setFormValues(dialog.formValues);
	}
}
