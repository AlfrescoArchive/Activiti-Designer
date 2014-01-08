/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.property;

import org.activiti.bpmn.model.EventListener;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.Process;
import org.activiti.designer.command.BpmnProcessModelUpdater;
import org.activiti.designer.property.ui.EventListenerDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyEventListenersSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  protected TableViewer tableViewer;
  protected Button addButton;
  protected Button removeButton;
  protected Button editButton;
  protected Button moveUpButton;
  protected Button moveDownButton;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    addTable();
    addButtons();
    addLabel();
    
    addActions();
  }
  
  protected void addActions() {
    
    addButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        EventListenerDialog dialog = new EventListenerDialog(formComposite.getShell());
        dialog.setBlockOnOpen(true);
        int open = dialog.open();
        if(open == Window.OK) {
          Process process = (Process) getProcessModelUpdater().getUpdatableBusinessObject();
          process.getEventListeners().add(dialog.getListener());
          executeModelUpdater();
        }
      }
    });
    
    editButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if(selection != null && !selection.isEmpty()) {
          EventListenerDialog dialog = new EventListenerDialog(formComposite.getShell());
          dialog.setBlockOnOpen(true);
          dialog.setListener((EventListener) selection.getFirstElement(), false);
          int open = dialog.open();
          if(open == Window.OK) {
            executeModelUpdater();
          }
        }
      }
    });
    removeButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if(selection != null && !selection.isEmpty()) {
          Process process = (Process) getProcessModelUpdater().getUpdatableBusinessObject();
          process.getEventListeners().remove(selection.getFirstElement());
          executeModelUpdater();
        }
      }
    });
    
    moveUpButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if(selection != null && !selection.isEmpty()) {
          Process process = (Process) getProcessModelUpdater().getUpdatableBusinessObject();
          
          
          if(process.getEventListeners() != null) {
            int currentIndex = process.getEventListeners().indexOf(selection.getFirstElement());
            if(currentIndex > 0) {
              Object removed = process.getEventListeners().remove(currentIndex - 1);
              process.getEventListeners().add(currentIndex, (EventListener) removed);
              executeModelUpdater();
            }
          }
        }
      }
    });
    
    moveDownButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if(selection != null && !selection.isEmpty()) {
          Process process = (Process) getProcessModelUpdater().getUpdatableBusinessObject();
          
          
          if(process.getEventListeners() != null) {
            int currentIndex = process.getEventListeners().indexOf(selection.getFirstElement());
            if(currentIndex < process.getEventListeners().size() - 1) {
              Object removed = process.getEventListeners().remove(currentIndex + 1);
              process.getEventListeners().add(currentIndex, (EventListener) removed);
              executeModelUpdater();
            }
          }
        }
      }
    });
  }
  
  protected void addTable() {
    tableViewer = new TableViewer(formComposite, SWT.BORDER | SWT.SINGLE);
    FormData data = new FormData();
    data.top = createTopFormAttachment();
    data.left = new FormAttachment(0, 150);
    data.right = new FormAttachment(100, -100);
    data.bottom = new FormAttachment(100, 0);
    tableViewer.setContentProvider(new ArrayContentProvider());
    Table table = tableViewer.getTable();
    table.setLayoutData(data);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    
    TableViewerColumn eventsColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    eventsColumn.getColumn().setText("Events");
    eventsColumn.getColumn().setWidth(100);
    eventsColumn.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public String getText(Object element) {
        return ((EventListener)element).getEvents();
      }
    });
    
    TableViewerColumn classColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    classColumn.getColumn().setText("Class");
    classColumn.getColumn().setWidth(100);
    classColumn.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public String getText(Object element) {
        EventListener listener = (EventListener) element;
        if(ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(listener.getImplementationType())) {
          return ((EventListener)element).getImplementation();
        }
        return null;
      }
    });
    
    TableViewerColumn delegateColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    delegateColumn.getColumn().setText("Delegate expression");
    delegateColumn.getColumn().setWidth(100);
    delegateColumn.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public String getText(Object element) {
        EventListener listener = (EventListener) element;
        if(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equals(listener.getImplementationType())) {
          return ((EventListener)element).getImplementation();
        }
        return null;
      }
    });
    
    TableViewerColumn enitityColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    enitityColumn.getColumn().setText("Entity Type");
    enitityColumn.getColumn().setWidth(100);
    enitityColumn.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public String getText(Object element) {
       return ((EventListener) element).getEntityType();
      }
    });
    
    TableViewerColumn throwEventColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    throwEventColumn.getColumn().setText("Throw event");
    throwEventColumn.getColumn().setWidth(100);
    throwEventColumn.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public String getText(Object element) {
        EventListener listener = (EventListener) element;
        String result = null;
        if(ImplementationType.IMPLEMENTATION_TYPE_THROW_SIGNAL_EVENT.equals(listener.getImplementationType())) {
          result = "Signal";
        } else if(ImplementationType.IMPLEMENTATION_TYPE_THROW_GLOBAL_SIGNAL_EVENT.equals(listener.getImplementationType())) {
          result = "Global signal";
        } else if(ImplementationType.IMPLEMENTATION_TYPE_THROW_MESSAGE_EVENT.equals(listener.getImplementationType())) {
          result = "Message";
        } else if(ImplementationType.IMPLEMENTATION_TYPE_THROW_ERROR_EVENT.equals(listener.getImplementationType())) {
          result = "Error";
        }
        return result;
      }
    });
    
    TableViewerColumn eventRefColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    eventRefColumn.getColumn().setText("Signal/Message/Error");
    eventRefColumn.getColumn().setWidth(150);
    eventRefColumn.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public String getText(Object element) {
        EventListener listener = (EventListener) element;
        if(ImplementationType.IMPLEMENTATION_TYPE_THROW_SIGNAL_EVENT.equals(listener.getImplementationType()) 
            || ImplementationType.IMPLEMENTATION_TYPE_THROW_GLOBAL_SIGNAL_EVENT.equals(listener.getImplementationType())
            || ImplementationType.IMPLEMENTATION_TYPE_THROW_MESSAGE_EVENT.equals(listener.getImplementationType())
            || (ImplementationType.IMPLEMENTATION_TYPE_THROW_ERROR_EVENT.equals(listener.getImplementationType()))) {
          return listener.getImplementation();
        }
        return null;
      }
    });
  }
  
  protected void addButtons() {
    GridLayout layout = new GridLayout(1, true);
    Composite buttonComposite = new Composite(formComposite, SWT.NONE);
    buttonComposite.setBackground(formComposite.getBackground());
    buttonComposite.setLayout(layout);
    FormData data = new FormData();
    data.top = createTopFormAttachment();
    data.left = new FormAttachment(tableViewer.getControl());
    data.right = new FormAttachment(100, 0);
    buttonComposite.setLayoutData(data);
    
    addButton = new Button(buttonComposite, SWT.PUSH);
    addButton.setText("Add");
    addButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    editButton = new Button(buttonComposite, SWT.PUSH);
    editButton.setText("Edit");
    editButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    removeButton = new Button(buttonComposite, SWT.PUSH);
    removeButton.setText("Remove");
    removeButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    moveUpButton = new Button(buttonComposite, SWT.PUSH);
    moveUpButton.setText("Up");
    moveUpButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    moveDownButton = new Button(buttonComposite, SWT.PUSH);
    moveDownButton.setText("Down");
    moveDownButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
  }
  
  protected void addLabel() {
    createLabel("Event listeners", tableViewer.getControl());
  }
  
  @Override
  protected BpmnProcessModelUpdater createProcessModelUpdater() {
    BpmnProcessModelUpdater updater = super.createProcessModelUpdater();
    Process process = (Process) updater.getUpdatableBusinessObject();
    tableViewer.setInput(process.getEventListeners());
    return updater;
  }
  
  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    // Not using controls, we use a custom table-view
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    // Not using controls, we use a custom table-view
  }
}
