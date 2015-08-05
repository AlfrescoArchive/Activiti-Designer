package org.activiti.designer.property;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.command.BpmnProcessModelUpdater;
import org.activiti.designer.command.UpdateBusinessObjectCommand;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.BpmnMemoryModel.BpmnModelListener;
import org.activiti.designer.util.editor.ModelHandler;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public abstract class ActivitiPropertySection extends BaseActivitiPropertySection implements ModelUpdater {

  /**
   * Internal list of controls added to this section.
   */
  private List<Control> controls;
  
  /**
   * Cached updater.
   */
  private BpmnProcessModelUpdater currentUpdater;
  
  /**
   * Shared focus-listener added to all controls created. Makes sure value-updates are performed
   * and includes a fix for the focus-issue on Eclipse Juno.
   */
  protected FocusListener focusListener;
  protected SelectionListener selectionListener;
  protected BpmnModelListener modelListener;
  protected boolean modelChangesEnabled = true;
  
  protected Composite formComposite;
  
  public ActivitiPropertySection() {
    
    controls = new ArrayList<Control>();
    
    focusListener = new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        if (e.widget instanceof Control) {
          flushControlValue((Control) e.widget);
        }
      }
      
      @Override
      public void focusGained(FocusEvent e) {
        if(e.widget instanceof Text) {
          // Fix for issue in Eclipse Juno where the text is filled with value from the previous active
          // text-control in some cases. We set the value based on the model on focus.
          Object bo = getBusinessObject(getSelectedPictogramElement());
          if (bo != null) {
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
    
    modelListener = new BpmnModelListener() {
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
  public void aboutToBeHidden() {
    super.aboutToBeHidden();
    BpmnMemoryModel model = (ModelHandler.getModel(EcoreUtil.getURI(getDiagram())));
    if (model != null) {
       model.removeModelListener(modelListener);
    }
  }
  
  @Override
  public void aboutToBeShown() {
    super.aboutToBeHidden();
    BpmnMemoryModel model = (ModelHandler.getModel(EcoreUtil.getURI(getDiagram())));
    if (model != null) {
       model.addModelListener(modelListener);
    }
  }
  
  @Override
  public void refresh() {
    PictogramElement element = getSelectedPictogramElement();
    Object bo = getBusinessObject(element);
    resetModelUpdater();
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
          
          BpmnProcessModelUpdater updater = getProcessModelUpdater();

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
  public void executeModelUpdater() {
    // Make sure the update of the model is done in the transactional editing domain
    // to allow for "undoing" changes
    TransactionalEditingDomain editingDomain = getTransactionalEditingDomain();
    
    if (currentUpdater != null) {
      // Do the actual changes to the business-object in a command
      editingDomain.getCommandStack().execute(new UpdateBusinessObjectCommand(editingDomain, currentUpdater));
    }
    
    // Create a new updater for future changes
    resetModelUpdater();
  }
  
  /**
   * @return an {@link UpdateBusinessObjectCommand} that will be used to record model updates. 
   */
  protected BpmnProcessModelUpdater createProcessModelUpdater() {
    PictogramElement element = getSelectedPictogramElement();
    ActivitiBPMNFeatureProvider featureProvider = getFeatureProvider(element);
    Object bo = getBusinessObject(element);
    if (featureProvider != null && bo != null) {
      return featureProvider.getModelUpdaterFor(bo, element);
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
  
  public BpmnProcessModelUpdater getProcessModelUpdater() {
    if(currentUpdater == null) {
      currentUpdater = createProcessModelUpdater();
    }
    return currentUpdater;
  }
  
  /**
   * Clears the current updates, if any and creates a new fresh updater based on the
   * current model state.
   */
  protected void resetModelUpdater() {
    currentUpdater = createProcessModelUpdater();
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
  
  protected ActivitiBPMNFeatureProvider getFeatureProvider(PictogramElement element) {
    if (element == null)
      return null;
    Diagram diagram = getContainer(element);
    BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
    if (model != null) {
      return (ActivitiBPMNFeatureProvider) model.getFeatureProvider();
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
    if(!(control instanceof Label) && !(control instanceof CLabel))
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
    data.left = new FormAttachment(0, 200);
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
  
  /**
   * @param values values for the combo
   * @param defaultSelectionIndex index of the default selection. If there is no default selection,
   * pass in a negative number.
   * @return the combo component
   */
  protected Combo createCombobox(String[] values, int defaultSelectionIndex) {
    Combo comboControl = new Combo(formComposite, SWT.READ_ONLY);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 200);
    data.right = new FormAttachment(100, 0);
    data.top = createTopFormAttachment();
    comboControl.setLayoutData(data);
    
    // Set possible values
    if (values != null && values.length > 0) {
      comboControl.setItems(values);
      
      if (defaultSelectionIndex >= 0) {
        comboControl.select(defaultSelectionIndex);
        // Store the default-selection as "data", so we can reselect it when
        // the combo needs to be reset
        comboControl.setData(defaultSelectionIndex);
      }
    }
    
    comboControl.addSelectionListener(selectionListener);
    registerControl(comboControl);
    return comboControl;
  }

  protected CLabel createLabel(String labelName, Control control) {
    CLabel labelControl = getWidgetFactory().createCLabel(formComposite, labelName);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(control, -HSPACE);
    data.top = new FormAttachment(control, 0, SWT.TOP);
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
  
  protected String getSafeText(String string) {
    if(string == null) {
      return "";
    }
    return string;
  }
  
  protected BpmnMemoryModel getModel(PictogramElement element) {
    if (element == null)
      return null;
    Diagram diagram = getContainer(element);
    BpmnMemoryModel model = (ModelHandler.getModel(EcoreUtil.getURI(diagram)));
    return model;
  }

	/**
	 * Returns the default business object for the currently selected pictogram element in the
	 * diagram.
	 *
	 * @param clazz the class of the business object
	 * @return the business object or <code>null</code> if either no pictogram element is selected
	 *     or no business object is found.
	 */
	protected <T> T getDefaultBusinessObject(Class<T> clazz) {
	  final PictogramElement pe = getSelectedPictogramElement();

	  if (pe == null) {
	    return null;
	  }

	  return clazz.cast(getBusinessObject(pe));
	}
	
	protected String getCommaSeperatedString(List<String> list) {
    StringBuffer stringBuffer = new StringBuffer();
    if (list.size() > 0) {
      for (String string : list) {
        if (stringBuffer.length() > 0) {
          stringBuffer.append(",");
        }
        stringBuffer.append(string.trim());
      }
    }
    return stringBuffer.toString();
  }
	
	protected List<String> commaSeperatedStringToList(String commaSeperated) {
	  List<String> list = new ArrayList<String>();
	  if (StringUtils.isNotEmpty(commaSeperated)) {
      String[] seperatedList = null;
      if (commaSeperated.contains(",")) {
        seperatedList = commaSeperated.split(",");
      } else {
        seperatedList = new String[] { commaSeperated };
      }
      
      for (String string : seperatedList) {
        list.add(string.trim());
      }
    }
	  return list;
	}

	protected String getFieldString(String fieldname, ServiceTask mailTask) {
    String result = null;
    for(FieldExtension extension : mailTask.getFieldExtensions()) {
      if (fieldname.equalsIgnoreCase(extension.getFieldName())) {
        if (StringUtils.isNotEmpty(extension.getExpression())) {
          result = extension.getExpression();
        } else {
          result = extension.getStringValue();
        }
      }
    }
    if (result == null) {
      result = "";
    }
    return result;
  }

  protected void setFieldString(String fieldname, String fieldValue, ServiceTask mailTask) {
    FieldExtension fieldExtension = null;
    for(FieldExtension extension : mailTask.getFieldExtensions()) {
      if (fieldname.equalsIgnoreCase(extension.getFieldName())) {
        fieldExtension = extension;
      }
    }
    if (fieldExtension == null) {
      fieldExtension = new FieldExtension();
      fieldExtension.setFieldName(fieldname);
      mailTask.getFieldExtensions().add(fieldExtension);
    }

    if (fieldValue != null && (fieldValue.contains("${") || fieldValue.contains("#{"))) {
      fieldExtension.setExpression(fieldValue);
      fieldExtension.setStringValue(null);
    } else {
      fieldExtension.setStringValue(fieldValue);
      fieldExtension.setExpression(null);
    }
  }
  
  protected String convertMessageRef(String messageRef) {
    String convertedMessageRef = messageRef;
    if (StringUtils.isNotEmpty(convertedMessageRef)) {
      BpmnMemoryModel memoryModel = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
      BpmnModel model = memoryModel.getBpmnModel();
      if (model.getTargetNamespace() != null && convertedMessageRef.startsWith(model.getTargetNamespace())) {
        convertedMessageRef = convertedMessageRef.replace(model.getTargetNamespace(), "");
        convertedMessageRef = convertedMessageRef.replaceFirst(":", "");
      }
    }
    return convertedMessageRef;
  }
}
