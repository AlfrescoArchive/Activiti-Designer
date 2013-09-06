package org.activiti.designer.kickstart.form.property;


import org.activiti.designer.kickstart.form.command.FormPropertyGroupModelUpdater;
import org.activiti.designer.kickstart.form.command.KickstartModelUpdater;
import org.activiti.workflow.simple.definition.form.ListPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ListPropertyEntry;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class ListPropertyDefinitionPropertySection extends AbstractKickstartFormComponentSection {

  protected TableViewer valueTableViewer;
  protected Text addEntryText;
  protected Button removeButton;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    // Add control to add a new entry, NOT part of controls which should trigger
    // a model-update. This is done manually when entry is added
    addEntryText = getWidgetFactory().createText(formComposite, "");
    FormData data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = createTopFormAttachment();
    
    // Use the default info-decoration
    FieldDecoration infoDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
    ControlDecoration controlDecoration = new ControlDecoration(addEntryText, SWT.LEFT | SWT.CENTER);
    controlDecoration.setDescriptionText("Press enter to add the new item. Label and value can be altered in the table below.");
    controlDecoration.setImage(infoDecoration.getImage());
    
    addEntryText.setLayoutData(data);
    createLabel("Create new item", addEntryText);
    
    addEntryText.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) { 
          flushNewItemValue();
        }
      }
    });
    
    initializeTable();
  }
  
  protected void initializeTable() {
    
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, -30);
    data.top = new FormAttachment(addEntryText, VSPACE);
    data.bottom = new FormAttachment(100);
    data.height = 150;
    
    valueTableViewer = new TableViewer(formComposite);
    valueTableViewer.getTable().setHeaderVisible(true);
    valueTableViewer.getTable().setLinesVisible(true);
    valueTableViewer.getTable().setLayoutData(data);
    
    // Set data provider
    valueTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    
    // Create columns
    TableViewerColumn valueColumn = new TableViewerColumn(valueTableViewer, SWT.NONE);
    valueColumn.getColumn().setText("Value");
    valueColumn.getColumn().setWidth(300);
    valueColumn.setEditingSupport(new ListPropertyEntryEditingSupport(valueTableViewer) {

      @Override
      protected Object getValueFromEntry(ListPropertyEntry entry) {
        return entry.getValue();
      }

      @Override
      protected void setValueInEntry(ListPropertyEntry entry, Object value) {
        String newValue = String.valueOf(value);
        if(hasChanged(entry.getValue(), newValue)) {
          entry.setValue(String.valueOf(value));
          executeModelUpdater();
        }
      }
    });
    
    
    valueColumn.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public String getText(Object element) {
        ListPropertyEntry entry = (ListPropertyEntry) element;
        return entry.getValue();
      }
    });
    
    TableViewerColumn nameColumn = new TableViewerColumn(valueTableViewer, SWT.NONE);
    nameColumn.getColumn().setText("Label");
    nameColumn.getColumn().setWidth(300);
    nameColumn.setEditingSupport(new ListPropertyEntryEditingSupport(valueTableViewer) {

      @Override
      protected Object getValueFromEntry(ListPropertyEntry entry) {
        return entry.getName();
      }

      @Override
      protected void setValueInEntry(ListPropertyEntry entry, Object value) {
        String newValue = String.valueOf(value);
        if(hasChanged(entry.getName(), newValue)) {
          entry.setName(String.valueOf(value));
          executeModelUpdater();
        }
      }
    });
    nameColumn.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public String getText(Object element) {
        ListPropertyEntry entry = (ListPropertyEntry) element;
        return entry.getName();
      }
    });
    
    // Add "remove' button, using default "delete" icon
    removeButton = getWidgetFactory().createButton(formComposite, "", SWT.PUSH | SWT.ICON);
    removeButton.setToolTipText("Remove current item");
    removeButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
        ISharedImages.IMG_ETOOL_DELETE));
    removeButton.setAlignment(SWT.CENTER);
    
    data = new FormData();
    data.right = new FormAttachment(100);
    data.top = new FormAttachment(addEntryText, VSPACE);
    
    removeButton.setLayoutData(data);
    removeButton.setSize(24, 24);
    removeButton.setEnabled(false);
    removeButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        deleteSelectedValue();
      }
    });
    
    
    // Enable/disable the button on selection of the table
    valueTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        removeButton.setEnabled(event.getSelection() != null && !event.getSelection().isEmpty());
      }
    });
  }
  
  @Override
  public void refresh() {
    super.refresh();
    
    if(valueTableViewer != null) {
      // Force initialization of updater
      resetModelUpdater();
    }
  }
  
  protected void flushNewItemValue() {
    if(addEntryText.getText() != null && !addEntryText.getText().isEmpty()) {
      String value = addEntryText.getText();
      addEntryText.setText("");
      
      ListPropertyDefinition propDef = (ListPropertyDefinition) getModelUpdater().getUpdatableBusinessObject();
      propDef.addEntry(new ListPropertyEntry(value, value));
      executeModelUpdater();
    }
  }
  
  protected void deleteSelectedValue() {
    if(valueTableViewer.getSelection() != null && !valueTableViewer.getSelection().isEmpty()) {
      IStructuredSelection selection = (IStructuredSelection) valueTableViewer.getSelection();
      Object selectedItem = selection.getFirstElement();
      ListPropertyDefinition propDef = (ListPropertyDefinition) getModelUpdater().getUpdatableBusinessObject();
      propDef.getEntries().remove(selectedItem);
      
      // Model updater will trigger refresh of the table's data-provider input with the new entries
      executeModelUpdater();
    }
  }
  
  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    // Do nothing, all update-logic is performed manually, not control-driven
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    // Do nothing, all update-logic is performed manually, not control-driven
  }

  @Override
  protected KickstartModelUpdater<?> createModelUpdater() {
    KickstartModelUpdater<?> updater = super.createModelUpdater();
    if(updater != null) {
       // Use the updateable model as source for the table
       valueTableViewer.setInput(((ListPropertyDefinition) updater.getUpdatableBusinessObject())
           .getEntries());
       return updater;
    }
    return null;
  }
}
