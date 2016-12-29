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

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.kickstart.process.command.KickstartProcessModelUpdater;
import org.activiti.designer.kickstart.process.command.UpdateBusinessObjectCommand;
import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel.KickstartProcessModelListener;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
/**
 * Abstract base-class for all property sections used in Kickstart Form.
 * 
 * @author Frederik heremans
 */
public abstract class AbstractKickstartProcessPropertySection extends GFPropertySection implements ITabbedPropertyConstants {

  protected static final Integer LABEL_WIDTH = 120;
  /**
   * Internal list of controls added to this section.
   */
  private List<Control> controls;
  
  /**
   * Cached updater.
   */
  private KickstartProcessModelUpdater<?> currentUpdater;
  
  /**
   * Shared focus-listener added to all controls created. Makes sure value-updates are performed
   * and includes a fix for the focus-issue on Eclipse Juno.
   */
  protected FocusListener focusListener;
  protected FocusListener rememberSelectionFocusListener;
  protected SelectionListener selectionListener;
  protected ModifyListener modifyListener;
  protected KickstartProcessModelListener modelListener;
  protected boolean modelChangesEnabled = true;
  
  protected Composite formComposite;
  
  public AbstractKickstartProcessPropertySection() {
    
    controls = new ArrayList<Control>();
    
    rememberSelectionFocusListener = new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        if(e.getSource() instanceof Text) {
          Text text = (Text) e.getSource();
          if(text.getSelection() != null) {
            text.setData(text.getSelection());
          } else {
            text.setData(null);
          }
        }
      }
      
      @Override
      public void focusGained(FocusEvent e) {
        if(e.getSource() instanceof Text) {
          Text text = (Text) e.getSource();
          if(text.getData() != null && text.getData() instanceof Point) {
            text.setSelection((Point)text.getData());
          }
        }
      }
    };
    
    focusListener = new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        if(e.widget instanceof Control) {
          flushControlValue((Control) e.widget);
        }
      }
      
      @Override
      public void focusGained(FocusEvent e) {
        if(e.widget instanceof Text) {
          // Fix for issue in Eclipse Juno where the text is filled with value from the previous active
          // text-control in some cases. We set the value based on the model on focus.
          Object bo = getBusinessObject(getSelectedPictogramElement());
          if(bo != null) {
            populateControl((Control) e.widget, bo);
          } else {
            resetControl((Control) e.widget);
          }
        }
      }
    };
    
    selectionListener = new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if(e.widget instanceof Control) {
          flushControlValue((Control) e.widget);
        }
      }
    };
    
    modelListener = new KickstartProcessModelListener() {
      @Override
      public void objectUpdated(Object updatedObject) {
        if(modelChangesEnabled) {
          Object bo = getBusinessObject(getSelectedPictogramElement());
          if(bo != null && bo.equals(updatedObject)) {
            refresh();
          }
        }
      }
    };
    
    modifyListener = new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        if(e.widget instanceof Control) {
          flushControlValue((Control) e.widget);
        }
      }
    };
  }
  
  @Override
  public void aboutToBeHidden() {
    super.aboutToBeHidden();
    KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram())));
    if (model != null) {
       model.removeModelListener(modelListener);
    }
  }
  
  @Override
  public void aboutToBeShown() {
    super.aboutToBeHidden();
    KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram())));
    if (model != null) {
       model.addModelListener(modelListener);
    }
  }
  
  @Override
  public void refresh() {
    PictogramElement element = getSelectedPictogramElement();
    Object bo = getBusinessObject(element);
    if(bo != null) {
      // Populate all controls, based on the model
      for(Control control : controls) {
        populateControl(control, bo);
      }
    } else {
      // Reset all controls, no object selected
      for(Control control : controls) {
        resetControl(control);
      }
    }
  }

  @Override
  public final void createControls(final Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
    super.createControls(parent, aTabbedPropertySheetPage);
    
    final GridData parentGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
    parent.setLayoutData(parentGridData);
    
    parent.addListener(SWT.Resize, new Listener () {
      public void handleEvent(Event event) {
          Rectangle bounds = parent.getBounds();
          parentGridData.widthHint = bounds.width;
      }
    });
    
    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    // Create a shared form-composite to add all controls to
    formComposite = factory.createFlatFormComposite(parent);
    // Let superclass create controls
    createFormControls(aTabbedPropertySheetPage);
  }
  
  /**
   * @param formComposite the composite with a form-layout to add the controls to.
   * @param aTabbedPropertySheetPage aTabbedPropertySheetPage
   */
  protected abstract void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage);
  
  /**
   * @param control 
   * @param businessObject model to get value from, is never null.
   * @return the current value the model contains for the given control
   */
  protected abstract Object getModelValueForControl(Control control, Object businessObject);
  
  /**
   * Method that is called when a control has a changed value and the model needs to
   * be updated accordingly.
   * 
   * @param control the control that has an updated value.
   * @param businessObject the model to update, is never null.
   */
  protected abstract void storeValueInModel(Control control, Object businessObject);

  protected void flushControlValue(final Control control) {
    final Object bo = getBusinessObject(getSelectedPictogramElement());
    if (bo != null) {
      // Get the current value from the model
      Object oldValue = getModelValueForControl(control, bo);

      // Compare the old value with the new value from the control
      Object newValue = getValueFromControl(control);
      if (hasChanged(oldValue, newValue)) {
        try {
          modelChangesEnabled = false;
          
          KickstartProcessModelUpdater<?> updater = getModelUpdater();

          // Perform the changes on the updatable BO instead of the original
          Object updatableBo = updater.getUpdatableBusinessObject();
          storeValueInModel(control, updatableBo);
          executeModelUpdater();
          
        } finally {
          // Re-enable model-change listener after our change has been applied
          modelChangesEnabled = true;
        }
      }
    }
  }
  
  /**
   * Execute the updates queued in the given updated using the current transactional
   * edition domain. 
   */
  protected void executeModelUpdater() {
    // Make sure the update of the model is done in the transactional editing domain
    // to allow for "undoing" changes
	DiagramEditor diagramEditor = (DiagramEditor) getDiagramContainer();
	TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
    
    if(currentUpdater != null) {
      // Do the actual changes to the business-object in a command
      editingDomain.getCommandStack().execute(new UpdateBusinessObjectCommand(editingDomain, currentUpdater));
    }
    
    // Create a new updater for future changes
    resetModelUpdater();
  }
  
  /**
   * @return an {@link UpdateBusinessObjectCommand} that will be used to record model updates. 
   */
  protected KickstartProcessModelUpdater<?> createModelUpdater() {
    PictogramElement element = getSelectedPictogramElement();
    KickstartProcessFeatureProvider featureProvider = getFeatureProvider(element);
    if(featureProvider != null) {
      return featureProvider.getModelUpdaterFor(featureProvider.getBusinessObjectForPictogramElement(element), element);
    }
    return null;
  }

  protected boolean hasChanged(Object oldValue, Object newValue) {
    if(oldValue == null) {
      return newValue != null;
    } else {
      return !oldValue.equals(newValue);
    }
  }
  
  protected KickstartProcessModelUpdater<?> getModelUpdater() {
    if(currentUpdater == null) {
      currentUpdater = createModelUpdater();
    }
    return currentUpdater;
  }
  
  /**
   * Clears the current updates, if any and creates a new fresh updater based on the
   * current model state.
   */
  protected void resetModelUpdater() {
    currentUpdater = createModelUpdater();
  }
  
  /**
   * @param control the control to set the value on, based on the given business object.
   * @param businessObject 
   */
  protected void populateControl(Control control, Object businessObject) {
    Object valueFromModel = getModelValueForControl(control, businessObject);
    
    // Set text of a text-field
    if(control instanceof Text) {
      if(valueFromModel == null) {
        ((Text)control).setText("");
      } else if(valueFromModel instanceof String) {
        ((Text)control).setText((String) valueFromModel);
      } else if(valueFromModel instanceof Integer || valueFromModel instanceof Long) {
        ((Text)control).setText(((Number) valueFromModel).toString());
      } else {
        throw new IllegalArgumentException("Text control expects a String model value");
      }
    } else if(control instanceof Button) {
      // Set selected value on the button. Null model value is considered to be 'false'
      if(valueFromModel == null) {
        ((Button)control).setSelection(false);
      } else if(valueFromModel instanceof Boolean) {
        ((Button)control).setSelection((Boolean) valueFromModel);
      } else {
        throw new IllegalArgumentException("Checkbox control expects a Boolean model value");
      }
    } else if(control instanceof Combo) {
      Combo comboControl = (Combo) control;
      int newIndex = -1;
       
      if(valueFromModel instanceof String) {
        // Locate the index for the model value in the available items
        for(int i=0; i<comboControl.getItemCount(); i++) {
          if(((String) valueFromModel).equals(comboControl.getItems()[i])) {
            newIndex = i;
            break;
          }
        }
      } else if(valueFromModel != null) {
        throw new IllegalArgumentException("List control expects a String model value");
      }
      if(newIndex < 0) {
        // When no index is found, revert to default (if available) or select first item
        if(comboControl.getData() != null && comboControl.getData() instanceof Integer) {
          newIndex = (Integer) comboControl.getData();
        } else {
          newIndex = 0;
        }
      }
      comboControl.select(newIndex);
      
    } else if(control instanceof Spinner) {
      Spinner spinner = (Spinner) control;
      if(valueFromModel == null) {
        spinner.setSelection(spinner.getMinimum());
      } else if(valueFromModel instanceof Integer) {
        spinner.setSelection((Integer) valueFromModel);
      } else {
        throw new IllegalArgumentException("Spinner control expects an Integer model value");
      }
    } else if((!(control instanceof Label) || !(control instanceof CLabel)) && valueFromModel != null){
      throw new IllegalArgumentException("Request to populate unsupported control based on model");
    }
  }
  
  protected Object getValueFromControl(Control control) {
    if(control instanceof Text) {
      return ((Text) control).getText();
    } else if(control instanceof Button) {
      return ((Button) control).getSelection();
    } else if(control instanceof Combo) {
      return ((Combo)control).getText();
    } else if(control instanceof Spinner) {
      return ((Spinner)control).getSelection();
    }
    throw new IllegalArgumentException("Unsupported control: "+ control.getClass().getName());
  }
  
  /**
   * Override if custom resetting of components need to be done. Default implementation will clear existing
   * values/selections.
   * 
   * @param control the control to reset to an empty state, regardless of the current selection.
   */
  protected void resetControl(Control control) {
    if(control instanceof Text) {
      control.removeFocusListener(focusListener);
      ((Text) control).setText("");
      control.addFocusListener(focusListener);
    } else if(control instanceof Button) {
      Button button = (Button) control;
      button.removeSelectionListener(selectionListener);
      button.setSelection(false);
      button.addSelectionListener(selectionListener);
    } else if(control instanceof Spinner) {
      Spinner spinner = (Spinner) control;
      spinner.removeModifyListener(modifyListener);
      spinner.setSelection(spinner.getSelection());
      spinner.addModifyListener(modifyListener);
    }
  }
  
  /**
   * @return the business-object associated with the given element, if any.
   */
  protected Object getBusinessObject(PictogramElement element) {
    if (element == null)
      return null;
    KickstartProcessFeatureProvider featureProvider = getFeatureProvider(element);
   if(featureProvider != null) {
     return featureProvider.getBusinessObjectForPictogramElement(element);
   }
   return null;
  }
  
  protected KickstartProcessFeatureProvider getFeatureProvider(PictogramElement element) {
    if (element == null)
      return null;
    Diagram diagram = getContainer(element);
    KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(diagram)));
    if (model != null) {
      return (KickstartProcessFeatureProvider) model.getFeatureProvider();
    }
    return null;
  }
  
  /**
   * Should be called when a control was added manually to the composite without using the create*
   * methods.
   */
  protected void registerControl(Control control) {
    // Add control to internal list
    controls.add(control);
    
    // Add shared focus-listener to this control
    if(!(control instanceof Label) && !(control instanceof CLabel) && !(control instanceof Composite))
    control.addFocusListener(focusListener);
    
    if(control instanceof Button) {
      ((Button) control).addSelectionListener(selectionListener);
    }
  }

  /**
   * @return a new {@link Text} control, added as last element of the given composite, attached
   * to the last control.
   */
  protected Text createTextControl(boolean multiLine) {
    Text textControl = null;
    FormData data = null;
    if (multiLine == true) {
      textControl = getWidgetFactory().createText(formComposite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
      data = new FormData(SWT.DEFAULT, 60);
    } else {
      textControl = getWidgetFactory().createText(formComposite, "", SWT.NONE);
      data = new FormData();
    }
    data.left = new FormAttachment(0, LABEL_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = createTopFormAttachment();
    textControl.setLayoutData(data);
    registerControl(textControl);
    return textControl;
  }
  
  /**
   * @return a new {@link Text} control, added as last element of the given composite, attached
   * to the last control.
   */
  protected Text createTextControlWithButton(boolean multiLine, Control button) {
    Text textControl = null;
    FormData data = null;
    if (multiLine == true) {
      textControl = getWidgetFactory().createText(formComposite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
      data = new FormData(SWT.DEFAULT, 60);
    } else {
      textControl = getWidgetFactory().createText(formComposite, "", SWT.NONE);
      data = new FormData();
    }
    data.left = new FormAttachment(0, LABEL_WIDTH);
    data.right = new FormAttachment(button, HSPACE);
    data.top = createTopFormAttachment();
    textControl.setLayoutData(data);
    
    data = new FormData();
    data.right = new FormAttachment(100, 0); 
    data.top = new FormAttachment(textControl, 0, SWT.CENTER);
    button.setLayoutData(data);
    
    registerControl(textControl);
    ensureCaretPositionRetained(textControl);
    return textControl;
  }
  
  protected Button createCheckboxControl(String label) {
    Button checkControl = getWidgetFactory().createButton(formComposite, label, SWT.CHECK);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, 0);
    data.top = createTopFormAttachment();
    
    checkControl.setLayoutData(data);
    registerControl(checkControl);
    return checkControl;
  }
  
  /**
   * @param values values for the combo
   * @param defaultSelectionIndex index of the default selection. If there is no default selection,
   * pass in a negative number.
   * @return the combo component
   */
  protected Combo createCombobox(String[] values, int defaultSelectionIndex) {
    Combo comboControl = new Combo(formComposite, SWT.READ_ONLY);
    FormData data = new FormData();
    data.left = new FormAttachment(0, LABEL_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = createTopFormAttachment();
    comboControl.setLayoutData(data);
    
    // Set possible values
    comboControl.setItems(values);
    
    if(defaultSelectionIndex >= 0) {
      comboControl.select(defaultSelectionIndex);
      // Store the default-selection as "data", so we can reselect it when
      // the combo needs to be reset
      comboControl.setData(defaultSelectionIndex);
    }
    
    comboControl.addSelectionListener(selectionListener);
    registerControl(comboControl);
    return comboControl;
  }
  
  /**
   * @param values values for the combo
   * @param defaultSelectionIndex index of the default selection. If there is no default selection,
   * pass in a negative number.
   * @return the combo component
   */
  protected Spinner createSpinner(int minValue, int maxValue) {
    Spinner spinnerControl = new Spinner(formComposite, SWT.BORDER);
    FormData data = new FormData();
    data.left = new FormAttachment(0, LABEL_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = createTopFormAttachment();
    spinnerControl.setLayoutData(data);
    
    spinnerControl.addSelectionListener(selectionListener);
    registerControl(spinnerControl);
    return spinnerControl;
  }

  protected CLabel createLabel(String labelName, Control control) {
    CLabel labelControl = getWidgetFactory().createCLabel(formComposite, labelName);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(control, -HSPACE);
    data.top = new FormAttachment(control, 0, SWT.CENTER);
    labelControl.setLayoutData(data);
    return labelControl;
  }
  
  protected Label createFullWidthLabel(String labelName) {
    Label labelControl = getWidgetFactory().createLabel(formComposite, labelName, SWT.WRAP);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, 0);
    data.top = createTopFormAttachment();
    labelControl.setLayoutData(data);
    
    registerControl(labelControl);
    return labelControl;
  }
  
  protected Label createSeparator() {
    Label labelControl = getWidgetFactory().createLabel(formComposite, "", SWT.SEPARATOR | SWT.HORIZONTAL);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, 0);
    data.top = createTopFormAttachment();
    labelControl.setLayoutData(data);
    
    registerControl(labelControl);
    return labelControl;
  }
  
  protected Boolean getBooleanParameter(StepDefinition stepdef, String key, boolean defaultValue) {
    Boolean result = defaultValue;
    if (stepdef.getParameters() != null) {
      Object value = stepdef.getParameters().get(key);
      if (value instanceof Boolean) {
        result = (Boolean) value;
      } else if (value != null) {
        result = Boolean.valueOf(value.toString());
      }
    }
    return result;
  }
  
  protected String getStringParameter(StepDefinition stepdef, String key, String defaultValue) {
    String result = defaultValue;
    if (stepdef.getParameters() != null) {
      Object value = stepdef.getParameters().get(key);
      if (value instanceof String) {
        result = (String) value;
      } else if (value != null) {
        result = value.toString();
      }
    }
    return result;
  }
  
  /**
   * @return a {@link FormAttachment} relative to the last element.
   */
  protected FormAttachment createTopFormAttachment() {
    if (controls.size() == 0) {
      return new FormAttachment(0, VSPACE);
    } else {
      return new FormAttachment(controls.get(controls.size() - 1), VSPACE);
    }
  }

  protected Diagram getContainer(EObject container) {
    if(container == null) {
      return null;
    }
    if (container instanceof Diagram) {
      return (Diagram) container;
    } else {
      return getContainer(container.eContainer());
    }
  }
  
  protected void ensureCaretPositionRetained(Text text) {
    text.addFocusListener(rememberSelectionFocusListener);
  }
  
  protected String getSafeText(String string) {
    if(string == null) {
      return "";
    }
    return string;
  }
}
