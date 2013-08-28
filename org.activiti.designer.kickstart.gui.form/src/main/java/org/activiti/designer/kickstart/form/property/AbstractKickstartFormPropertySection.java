package org.activiti.designer.kickstart.form.property;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.kickstart.form.command.KickstartModelUpdater;
import org.activiti.designer.kickstart.form.command.UpdateBusinessObjectCommand;
import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.KickstartFormMemoryModel.KickstartFormModelListener;
import org.activiti.designer.util.editor.ModelHandler;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
/**
 * Abstract base-class for all property sections used in Kickstart Form.
 * 
 * @author Frederik heremans
 */
public abstract class AbstractKickstartFormPropertySection extends GFPropertySection implements ITabbedPropertyConstants {

  /**
   * Internal list of controls added to this section.
   */
  private List<Control> controls;
  
  /**
   * Shared focus-listener added to all controls created. Makes sure value-updates are performed
   * and includes a fix for the focus-issue on Eclipse Juno.
   */
  protected FocusListener focusListener;
  protected SelectionListener selectionListener;
  protected KickstartFormModelListener modelListener;
  protected boolean modelChangesEnabled = true;
  
  protected Composite formComposite;
  
  public AbstractKickstartFormPropertySection() {
    
    controls = new ArrayList<Control>();
    
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
    
    modelListener = new KickstartFormModelListener() {
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
  }
  
  @Override
  public void refresh() {
    PictogramElement element = getSelectedPictogramElement();
    Object bo = getBusinessObject(element);
    if(bo != null) {
      // Make sure the model is wired with the listener
      Diagram diagram = getContainer(element);
      KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(diagram)));
      if (model != null) {
         model.addModelListener(modelListener);
      }
      
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
  public final void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
    super.createControls(parent, aTabbedPropertySheetPage);
    
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
          
          // Make sure the update of the model is done in the transactional editing domain
          // to allow for "undoing" changes
          DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
          TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
          KickstartModelUpdater<?> updater = getModelUpdater();

          // Perform the changes on the updatable BO instead of the original
          Object updatableBo = updater.getUpdatableBusinessObject();
          storeValueInModel(control, updatableBo);

          // Do the actual changes to the business-object in a command
          editingDomain.getCommandStack().execute(new UpdateBusinessObjectCommand(editingDomain, updater));
          
        } finally {
          // Re-enable model-change listener after our change has been applied
          modelChangesEnabled = true;
        }
      }
    }
  }
  
  /**
   * @return an {@link UpdateBusinessObjectCommand} that will be used to record model updates. 
   */
  protected abstract KickstartModelUpdater<?> getModelUpdater();

  protected boolean hasChanged(Object oldValue, Object newValue) {
    if(oldValue == null) {
      return newValue != null;
    } else {
      return !oldValue.equals(newValue);
    }
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
    } else {
      throw new IllegalArgumentException("Request to populate unsupported control based on model");
    }
  }
  
  protected Object getValueFromControl(Control control) {
    if(control instanceof Text) {
      return ((Text) control).getText();
    } else if(control instanceof Button) {
      return ((Button) control).getSelection();
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
    }
  }
  
  /**
   * @return the business-object associated with the given element, if any.
   */
  protected Object getBusinessObject(PictogramElement element) {
    if (element == null)
      return null;
    Diagram diagram = getContainer(element);
    KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(diagram)));
    if (model != null) {
      return model.getFeatureProvider().getBusinessObjectForPictogramElement(element);
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
      data = new FormData(SWT.DEFAULT, 100);
    } else {
      textControl = getWidgetFactory().createText(formComposite, "", SWT.NONE);
      data = new FormData();
    }
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = createTopFormAttachment();
    textControl.setLayoutData(data);
    registerControl(textControl);
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

  protected CLabel createLabel(String labelName, Control control) {
    CLabel labelControl = getWidgetFactory().createCLabel(formComposite, labelName);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(control, -HSPACE);
    data.top = new FormAttachment(control, 0, SWT.CENTER);
    labelControl.setLayoutData(data);
    return labelControl;
  }
  
  protected FormAttachment createTopFormAttachment() {
    if (controls.size() == 0) {
      return new FormAttachment(0, VSPACE);
    } else {
      return new FormAttachment(controls.get(controls.size() - 1), VSPACE);
    }
  }

  protected Diagram getContainer(EObject container) {
    if (container instanceof Diagram) {
      return (Diagram) container;
    } else {
      return getContainer(container.eContainer());
    }
  }
}
