package org.activiti.designer.property.ui;


import org.activiti.bpmn.model.EventListener;
import org.activiti.bpmn.model.ImplementationType;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

public class EventListenerDialog extends TitleAreaDialog implements ITabbedPropertyConstants {

  protected static final String[] ENTITY_TYPES = new String[] {"", "attachment", "comment", "execution", "identity-link", "job", "process-definition", "process-instance", "task"};
  protected static final String[] IMPLEMENTATION_TYPES = new String[] {"Class", "Delegate Expression", "Signal", "Global signal", "Message", "Error"};
  
  protected Composite composite;
  protected Text eventsText;
  protected Text implementationText;
  protected CLabel implementationReferenceLabel;
  protected Combo implementationTypeCombo;
  protected Combo entityTypeCombo;
  
  protected EventListener listener;
  protected boolean newListener = true;
  
  public EventListenerDialog(Shell parentShell) {
    super(parentShell);
  }
  
  public EventListener getListener() {
    return listener;
  }
  
  public void setListener(EventListener listener, boolean isNew) {
    this.listener = listener;
    this.newListener = isNew;
  }

  @Override
  protected void okPressed() {
    if(listener == null) {
      listener = new EventListener();
    }
    
    listener.setEvents(eventsText.getText());
    if(StringUtils.isEmpty(entityTypeCombo.getText())) {
      listener.setEntityType(null);
    } else {
      listener.setEntityType(entityTypeCombo.getText());
    }
    
    listener.setImplementation(implementationText.getText());
    
    // Update model according to values in the widgets
    if(IMPLEMENTATION_TYPES[0].equals(implementationTypeCombo.getText())) {
      listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
    } else if(IMPLEMENTATION_TYPES[1].equals(implementationTypeCombo.getText())) {
      listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
    } else if(IMPLEMENTATION_TYPES[2].equals(implementationTypeCombo.getText())) {
      listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_THROW_SIGNAL_EVENT);
    } else if(IMPLEMENTATION_TYPES[3].equals(implementationTypeCombo.getText())) {
      listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_THROW_GLOBAL_SIGNAL_EVENT);
    } else if(IMPLEMENTATION_TYPES[4].equals(implementationTypeCombo.getText())) {
      listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_THROW_MESSAGE_EVENT);
    } else if(IMPLEMENTATION_TYPES[5].equals(implementationTypeCombo.getText())) {
      listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_THROW_ERROR_EVENT);
    }
    super.okPressed();
  }
  
  @Override
  protected Control createDialogArea(Composite parent) {
    updateTitle();
    
    composite = new Composite(parent, SWT.NONE);
    FormLayout layout = new FormLayout();
    if(parent.getLayout() instanceof GridLayout) {
      composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    }
    layout.marginHeight = VMARGIN;
    layout.marginWidth = HMARGIN;
    layout.spacing = HSPACE;
    composite.setLayout(layout);
    
    // Events
    eventsText = new Text(composite, SWT.BORDER);
    FormData data = new FormData();
    data.top = new FormAttachment(0,0);
    data.left = new FormAttachment(0, 150);
    data.right = new FormAttachment(100, 0);
    eventsText.setLayoutData(data);
    addLabel("Events", eventsText);
    
    // Entity type
    entityTypeCombo = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
    entityTypeCombo.setItems(ENTITY_TYPES);
    data = new FormData();
    data.top = new FormAttachment(eventsText);
    data.left = new FormAttachment(0, 150);
    data.right = new FormAttachment(100, 0);
    entityTypeCombo.setLayoutData(data);
    addLabel("Entity type", entityTypeCombo);
    
    Label seperator = addSeperator(entityTypeCombo);
    
    // Implementation event type
    implementationTypeCombo = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
    implementationTypeCombo.setItems(IMPLEMENTATION_TYPES);
    data = new FormData();
    data.top = new FormAttachment(seperator);
    data.left = new FormAttachment(0, 150);
    data.right = new FormAttachment(100, 0);
    implementationTypeCombo.setLayoutData(data);
    addLabel("Implementation", implementationTypeCombo);
    
    // Class
    implementationText = new Text(composite, SWT.BORDER);
    data = new FormData();
    data.top = new FormAttachment(implementationTypeCombo);
    data.left = new FormAttachment(0, 150);
    data.right = new FormAttachment(100, 0);
    implementationText.setLayoutData(data);
    implementationReferenceLabel = addLabel("Class", implementationText);
    
    if(newListener) {
      resetFields();
    } else {
      initializeFields();
    }
    
    implementationTypeCombo.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        updateImplementationLabel(true);
      }
    });
    return super.createDialogArea(parent);
  }
  
  protected void resetFields() {
    implementationTypeCombo.setText(IMPLEMENTATION_TYPES[0]);
  }

  protected void initializeFields() {
    if(StringUtils.isEmpty(listener.getEvents())) {
      eventsText.setText("");
    } else {
      eventsText.setText(listener.getEvents());
    }
    if(listener.getEntityType() != null) {
      entityTypeCombo.setText(listener.getEntityType());
    }

    if(ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(listener.getImplementationType())) {
      implementationTypeCombo.setText(IMPLEMENTATION_TYPES[0]);
    } else if(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equals(listener.getImplementationType())) {
      implementationTypeCombo.setText(IMPLEMENTATION_TYPES[1]);
    } else if(ImplementationType.IMPLEMENTATION_TYPE_THROW_SIGNAL_EVENT.equals(listener.getImplementationType())) {
      implementationTypeCombo.setText(IMPLEMENTATION_TYPES[2]);
    } else if(ImplementationType.IMPLEMENTATION_TYPE_THROW_GLOBAL_SIGNAL_EVENT.equals(listener.getImplementationType())) {
      implementationTypeCombo.setText(IMPLEMENTATION_TYPES[3]);
    } else if(ImplementationType.IMPLEMENTATION_TYPE_THROW_MESSAGE_EVENT.equals(listener.getImplementationType())) {
      implementationTypeCombo.setText(IMPLEMENTATION_TYPES[4]);
    } else if(ImplementationType.IMPLEMENTATION_TYPE_THROW_ERROR_EVENT.equals(listener.getImplementationType())) {
      implementationTypeCombo.setText(IMPLEMENTATION_TYPES[5]);
    }
    
    if(listener.getImplementation() != null) {
      implementationText.setText(listener.getImplementation());
    }
    
    updateImplementationLabel(false);
  }

  protected void updateImplementationLabel(boolean clear) {
    if(clear) {
      implementationText.setText("");
    }
    
    if(IMPLEMENTATION_TYPES[0].equals(implementationTypeCombo.getText())) {
      implementationReferenceLabel.setText("Classname");
    } else if(IMPLEMENTATION_TYPES[1].equals(implementationTypeCombo.getText())) {
      implementationReferenceLabel.setText("Delegate Expression");
    } else if(IMPLEMENTATION_TYPES[2].equals(implementationTypeCombo.getText())) {
      implementationReferenceLabel.setText("Signal name");
    } else if(IMPLEMENTATION_TYPES[3].equals(implementationTypeCombo.getText())) {
      implementationReferenceLabel.setText("Signal name");
    } else if(IMPLEMENTATION_TYPES[4].equals(implementationTypeCombo.getText())) {
      implementationReferenceLabel.setText("Message name");
    } else if(IMPLEMENTATION_TYPES[5].equals(implementationTypeCombo.getText())) {
      implementationReferenceLabel.setText("Error code");
    }
  }

  protected CLabel addLabel(String text, Control relativeTo) {
    CLabel labelControl = new CLabel(composite, SWT.WRAP);
    labelControl.setText(text);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(relativeTo, -HSPACE);
    data.top = new FormAttachment(relativeTo, 0, SWT.CENTER);
    labelControl.setLayoutData(data);
    return labelControl;
  }
  
  protected Label addSeperator(Control relativeTo) {
    Label labelControl = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(relativeTo, 0);
    labelControl.setLayoutData(data);
    return labelControl;
  }

  protected void updateTitle() {
    setTitle("Event listener");
    if(newListener) {
      setMessage("Create a new event-listener");
    } else {
      setMessage("Update an existing event-listener");
    }
  }
}
