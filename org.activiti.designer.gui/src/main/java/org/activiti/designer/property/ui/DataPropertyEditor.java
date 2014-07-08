package org.activiti.designer.property.ui;

import java.util.List;

import org.activiti.bpmn.model.BooleanDataObject;
import org.activiti.bpmn.model.DateDataObject;
import org.activiti.bpmn.model.DoubleDataObject;
import org.activiti.bpmn.model.IntegerDataObject;
import org.activiti.bpmn.model.ItemDefinition;
import org.activiti.bpmn.model.LongDataObject;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.StringDataObject;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.ValuedDataObject;
import org.activiti.designer.property.ModelUpdater;
import org.activiti.designer.util.BpmnBOUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class DataPropertyEditor extends TableFieldEditor {

	protected Composite parent;
	protected ModelUpdater modelUpdater;
	public PictogramElement pictogramElement;
	public Diagram diagram;
	private List<ValuedDataObject> dataPropertyList;

	public DataPropertyEditor(String key, Composite parent, ModelUpdater modelUpdater) {

		super(key, "", new String[] {"Id", "Name", "Type", "Value"},
				new int[] {100, 200, 100, 200}, parent);
		this.parent = parent;
		this.modelUpdater = modelUpdater;
	}

	public void initialize(List<ValuedDataObject> dataPropertyList) {
		removeTableItems();
		this.dataPropertyList = dataPropertyList;
		if(dataPropertyList == null || dataPropertyList.size() == 0) return;
		for (ValuedDataObject dataProperty : dataPropertyList) {
			addTableItem(dataProperty);
		}
	}

	@Override
	protected String createList(String[][] items) {
		return null;
	}

	@Override
	protected String[][] parseString(String string) {
		return null;
	}

	protected void addTableItem(ValuedDataObject dataProperty) {

		if(table != null) {
			String dataType = getDataType(dataProperty);

			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(0, dataProperty.getId() != null ? dataProperty.getId() : "");
			tableItem.setText(1, dataProperty.getName() != null ? dataProperty.getName() : "");
			tableItem.setText(2, dataType != null ? dataType : "");
			tableItem.setText(3, dataProperty.getValue() != null ? dataProperty.getValue().toString() : "");
		}
	}

	@Override
	protected String[] getNewInputObject() {
		DataPropertyDialog dialog = new DataPropertyDialog(parent.getShell(), getItems());
		dialog.open();
		if(StringUtils.isNotEmpty(dialog.id) && StringUtils.isNotEmpty(dialog.name)) {      
	    	saveNewObject(dialog);
			return new String[] {dialog.id, dialog.name, dialog.type, dialog.value};
		} else {
			return null;
		}
	}

	@Override
	protected String[] getChangedInputObject(TableItem item) {
		int index = table.getSelectionIndex();
		
		DataPropertyDialog dialog = new DataPropertyDialog(parent.getShell(), getItems(), 
				dataPropertyList.get(table.getSelectionIndex()));
		dialog.open();
		if(StringUtils.isNotEmpty(dialog.id) && StringUtils.isNotEmpty(dialog.name)) {      
	    	saveChangedObject(dialog, index);
			return new String[] {dialog.id, dialog.name, dialog.type, dialog.value};
		} else {
			return null;
		}
	}

	@Override
	protected void removedItem(int index) {
		if (pictogramElement != null) {
			if(index >= 0 && index < dataPropertyList.size()) {
				Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
				BpmnBOUtil.removeDataObject(updatableBo, index, diagram);
				modelUpdater.executeModelUpdater();
			}
		}
	}

	private void saveNewObject(final DataPropertyDialog dialog) {
		if (pictogramElement != null) {
			// verify that id is unique
			if (!isUnique(dialog.id)) {
				MessageDialog.openError(parent.getShell(), "Validation error", "ID must be unique.");
				return;
			}

			// Perform the changes on the updatable BO instead of the original
			Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
			ValuedDataObject newDataObject = getDataObject(dialog.type);
			ItemDefinition itemDef = new ItemDefinition();
			itemDef.setStructureRef("xsd:" + dialog.type);
			
			newDataObject.setId(dialog.id);
			newDataObject.setName(dialog.name);
			newDataObject.setItemSubjectRef(itemDef);
			newDataObject.setValue(dialog.value);
			
			BpmnBOUtil.addDataObject(updatableBo, newDataObject, diagram);
			modelUpdater.executeModelUpdater();
		}
	}

	private void saveChangedObject(final DataPropertyDialog dialog, final int index) {
		if (pictogramElement != null) {
      ValuedDataObject originalDataProperty = dataPropertyList.get(index);

      // verify that id is unique
			if (!dialog.id.equals(originalDataProperty.getId()) && !isUnique(dialog.id)) {
				MessageDialog.openError(parent.getShell(), "Validation error", "ID must be unique.");
				return;
			}

			// Perform the changes on the updatable BO instead of the original
			Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
			ItemDefinition itemDef = new ItemDefinition();
			itemDef.setStructureRef("xsd:" + dialog.type);

			// check to see if any values actually changed, or it is a no op
      ValuedDataObject changedDataObject = originalDataProperty.clone();
			changedDataObject.setId(dialog.id);
			changedDataObject.setName(dialog.name);
			changedDataObject.setItemSubjectRef(itemDef);
			changedDataObject.setValue(dialog.value);

      if (!changedDataObject.equals(originalDataProperty)) {
				BpmnBOUtil.setDataObject(updatableBo, changedDataObject, index, diagram);
				modelUpdater.executeModelUpdater();
			}
		}
	}

	private List<ValuedDataObject> getDataProperties(Object bo) {
	    if (bo instanceof Process) {
	    	dataPropertyList = ((Process) bo).getDataObjects();
	    } else if (bo instanceof SubProcess) {
	    	dataPropertyList = ((SubProcess) bo).getDataObjects();
	    }
		return dataPropertyList;
	}

	@Override
	protected void upPressed() {
		final int index = table.getSelectionIndex();
		// Perform the changes on the updatable BO instead of the original
		Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
		List<ValuedDataObject> boDataObjects = getDataProperties(updatableBo);

		ValuedDataObject removedDataObject = boDataObjects.remove(index);
		boDataObjects.add(index - 1, removedDataObject);
		dataPropertyList = boDataObjects;
		modelUpdater.executeModelUpdater();
		super.upPressed();
	}

	@Override
	protected void downPressed() {
		final int index = table.getSelectionIndex();
		// Perform the changes on the updatable BO instead of the original
		Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
		List<ValuedDataObject> boDataObjects = getDataProperties(updatableBo);

		ValuedDataObject removedDataObject = boDataObjects.remove(index);
		boDataObjects.add(index + 1, removedDataObject);
		dataPropertyList = boDataObjects;
		modelUpdater.executeModelUpdater();
		super.downPressed();
	}

	@Override
	protected boolean isTableChangeEnabled() {
		return false;
	}
	
	private String getDataType(ValuedDataObject dataProperty) {
		String structureRef = null;
		String dataType = null;

		if (null != dataProperty.getItemSubjectRef()) {
			structureRef = dataProperty.getItemSubjectRef().getStructureRef();
		}

		if (null != structureRef) {
			dataType = structureRef.substring(structureRef.indexOf(':') + 1);
		}
		
		return dataType;
	}

	private ValuedDataObject getDataObject(String type) {
	    if (type.equals("string")) {
	        return new StringDataObject();
	      } else if (type.equals("integer")) {
	       return new IntegerDataObject();
	      } else if (type.equals("long")) {
	    	  return new LongDataObject();
	      } else if (type.equals("double")) {
	    	  return new DoubleDataObject();
	      } else if (type.equals("boolean")) {
	    	  return new BooleanDataObject();
	      } else if (type.equals("date")) {
	    	  return new DateDataObject();
	      } else {
	        // TODO should throw exception here for unsupported data type
	      }

		return null;
	}
	
	private boolean isUnique(String newId) {
		// verify that id is unique
		for (ValuedDataObject currentObject : dataPropertyList) {
			if (currentObject.getId().equals(newId)) {
				return false;
			}
		}
		return true;
	}
}
