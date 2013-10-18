package org.activiti.designer.kickstart.process.property;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;


/**
 * Viewer that wraps single or multiple item-selection. Allows adding multiple string-items to a list, 
 * editing them (using free-text and a reference-browser) and removing from the list. All changes are
 * propagated to the {@link SelectionListener} provided in the constructor.
 * 
 * @author Frederik Heremans
 */
public class StringItemSelectionViewer {

  protected ListViewer listViewer;
  protected Button addButton;
  protected Button removeButton;
  protected Composite composite;
  protected Text itemText;
  protected Control browserControl;
  
  protected List<String> items = new ArrayList<String>();
  protected boolean multiSelect;
  protected SelectionListener selectionListener;
  
  protected String addItemLabel = "Add item";
  protected String removeItemLabel = "Remove item";
  protected String defaultItemValue = "item";
  
  
  public StringItemSelectionViewer(Composite parent, boolean multiSelect, SelectionListener selectionListener, StringItemBrowser browser) {
    this.multiSelect = multiSelect;
    
    composite = new Composite(parent, SWT.NONE);
    composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    composite.setLayout(new GridLayout(3, false));

    itemText = new Text(composite, SWT.BORDER);
    
    if(multiSelect) {
      itemText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
      itemText.setEnabled(false);
      
      browserControl = browser.getBrowserControl(this, composite);
      browserControl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
      browserControl.setEnabled(false);
      
      listViewer = new ListViewer(composite, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
      listViewer.setContentProvider(new ArrayContentProvider());
      if(this.items != null) {
        listViewer.setInput(items);
      }
      listViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
      
      Composite buttons = new Composite(composite, SWT.NONE);
      buttons.setBackground(parent.getBackground());
      RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
      rowLayout.spacing = 0;
      rowLayout.pack = false;
      rowLayout.marginTop = 0;
      rowLayout.marginBottom = 0;
      rowLayout.marginLeft = 0;
      rowLayout.marginRight = 0;
      buttons.setLayout(rowLayout);
      buttons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
      
      addButton = new Button(buttons, SWT.PUSH);
      addButton.setText(addItemLabel);
      
      removeButton = new Button(buttons, SWT.PUSH);
      removeButton.setText(removeItemLabel);
      
      // Selection change
      listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
          editSelectedItem();
        }
      });
      
      // Adding an item
      addButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          String newItem = defaultItemValue;
          items.add(newItem);
          listViewer.refresh();
          listViewer.setSelection(new StructuredSelection(newItem));
          itemText.setFocus();
        }
      });
      
      // Remove item
      removeButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          if(listViewer.getSelection() != null && !listViewer.getSelection().isEmpty()) {
            items.remove((String) ((IStructuredSelection) listViewer.getSelection()).getFirstElement());
            listViewer.refresh();
          }
        }
      });
    } else {
      itemText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
    }
    
    itemText.addFocusListener(new FocusAdapter() {
      public void focusLost(org.eclipse.swt.events.FocusEvent e) {
        flushUserText();
      };
    });
  }
  
  public Composite getComposite() {
    return composite;
  }
  
  public void setAddItemLabel(String addItemLabel) {
    this.addItemLabel = addItemLabel;
  }
  
  public void setRemoveItemLabel(String removeItemLabel) {
    this.removeItemLabel = removeItemLabel;
  }
  
  public void setDefaultItemValue(String defaultItemValue) {
    this.defaultItemValue = defaultItemValue;
  }
  
  public List<String> getItems() {
    return items;
  }
  
  public void setItems(List<String> items) {
    this.items = items;
    
    if(listViewer != null) {
      listViewer.setInput(items);
    } 
  }
  
  protected void flushUserText() {
    boolean changed = false;
    if(multiSelect) {
      if(listViewer.getSelection() != null && !listViewer.getSelection().isEmpty()) {
        int index = listViewer.getList().getSelectionIndex();
        if(!StringUtils.equals(items.get(index), itemText.getText())) {
          items.set(index, itemText.getText());
          changed = true;
        }
      }
    } else {
      if(items.size() == 0) {
        items.add(itemText.getText());
        changed = true;
      } else {
        if(!StringUtils.equals(items.get(0), itemText.getText())) {
          items.set(0, itemText.getText());
          changed = true;
        }
      }
    }
    
    if(changed) {
      listViewer.refresh();
      if(selectionListener != null) {
        selectionListener.widgetSelected(new SelectionEvent(new Event()));
      }
    }
  }

  protected void editSelectedItem() {
    if(listViewer.getSelection() != null && !listViewer.getSelection().isEmpty()) {
      itemText.setText((String) ((IStructuredSelection) listViewer.getSelection()).getFirstElement());
      itemText.setEnabled(true);
      browserControl.setEnabled(true);
    } else {
      itemText.setText("");
      itemText.setEnabled(false);
      browserControl.setEnabled(false);
    }
  }
  
}
