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
package org.activiti.designer.kickstart.process.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
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
  protected PropertyChangeListener changeListener;
  
  protected String addItemLabel = "Add item";
  protected String removeItemLabel = "Remove item";
  protected String defaultItemValue = "item";
  
  
  public StringItemSelectionViewer(Composite parent, boolean multiSelect, PropertyChangeListener changeListener, StringItemBrowser browser) {
    this.multiSelect = multiSelect;
    this.changeListener = changeListener;
    
    composite = new Composite(parent, SWT.NONE);
    composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    composite.setLayout(new GridLayout(3, false));

    itemText = new Text(composite, SWT.BORDER);
    
    PropertyChangeListener browserChangeListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
          if(itemText.isEnabled()) {
            itemText.setText((String) evt.getNewValue());
            flushUserText();
        }
      }
    };
    
    if(multiSelect) {
      itemText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
      itemText.setEnabled(false);
      
      browserControl = browser.getBrowserControl(browserChangeListener, composite);
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
          ensureItems().add(newItem);
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
            ensureItems().remove((String) ((IStructuredSelection) listViewer.getSelection()).getFirstElement());
            listViewer.refresh();
          }
        }
      });
    } else {
      itemText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
      
      browserControl = browser.getBrowserControl(browserChangeListener, composite);
      browserControl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
      browserControl.setEnabled(true);
    }
    
    itemText.addFocusListener(new FocusAdapter() {
      public void focusLost(org.eclipse.swt.events.FocusEvent e) {
        flushUserText();
      };
    });
  }
  
  protected List<String> ensureItems() {
    if(items == null) {
      items = new ArrayList<String>();
      listViewer.setInput(items);
    }
    return items;
  }
  
  
  public Composite getComposite() {
    return composite;
  }
  
  public void setAddItemLabel(String addItemLabel) {
    this.addItemLabel = addItemLabel;
    addButton.setText(addItemLabel);
  }
  
  public void setRemoveItemLabel(String removeItemLabel) {
    this.removeItemLabel = removeItemLabel;
    removeButton.setText(removeItemLabel);
  }
  
  public void setDefaultItemValue(String defaultItemValue) {
    this.defaultItemValue = defaultItemValue;
  }
  
  public List<String> getItems() {
    return items;
  }
  
  public void setItems(List<String> items) {
    this.items = items;
    
    if(multiSelect && listViewer != null) {
      listViewer.setInput(items);
    }
    
    if(!multiSelect && itemText != null) {
      if(items != null && items.size() > 0) {
        itemText.setText(items.get(0));
      } else {
        itemText.setText("");
      }
    }
  }
  
  public String getItem() {
    if(items != null && items.size() > 0) {
      return items.get(0);
    }
    return null;
  }
  
  public void setItem(String item) {
    if(item != null) {
      setItems(new ArrayList<String>(Arrays.asList(item)));
    } else {
      setItems(new ArrayList<String>());
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
      if(listViewer != null) {
        listViewer.refresh();
      }
      if(changeListener != null) {
        changeListener.propertyChange(new PropertyChangeEvent(this, "items", null, null));
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
