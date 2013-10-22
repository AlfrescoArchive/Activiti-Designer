package org.activiti.designer.kickstart.process.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.activiti.designer.kickstart.process.Activator;
import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

/**
 * Wrapper around a control that displays a referenced for link and allows selecting a reference.
 * 
 * @author Frederik Heremans
 */
public class FormPropertyReferenceViewer {

  protected SelectionListener selectionListener;
  protected IProject project;
  
  protected Composite composite;
  protected Label referencePropertyName;
  protected Button clearSelectionButton;
  protected Label iconLabel;
  protected String propertyName;
  
  protected PropertyItemBrowser propertyBrowser;
  
  public FormPropertyReferenceViewer(Composite parent, SelectionListener listener, IProject theProject) {
    this.selectionListener = listener;
    this.project = theProject;
    
    composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(4, false));
    composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    
    iconLabel = new Label(composite, SWT.ICON);
    iconLabel.setImage(Activator.getImage(KickstartProcessPluginImage.FORM_ICON));
    iconLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    
    referencePropertyName = new Label(composite, SWT.NONE);
    referencePropertyName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    referencePropertyName.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    
    propertyBrowser = new PropertyItemBrowser();
    propertyBrowser.getBrowserControl(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if(selectionListener != null) {
          setPropertyName(evt.getPropertyName());
          Event event = new  Event();
          event.widget = composite;
          selectionListener.widgetSelected(new SelectionEvent(new Event()));
        }
      }
    }, parent).setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    
    clearSelectionButton = new Button(composite, SWT.PUSH);
    clearSelectionButton.setText("Clear");
    clearSelectionButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    
    clearSelectionButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if(selectionListener != null) {
          Event event = new  Event();
          event.widget = composite;
          
          setPropertyName(null);
          selectionListener.widgetSelected(new SelectionEvent(event));
        }
      }
    });
  }
  
  public Composite getComposite() {
    return composite;
  }

  public void setProject(IProject project) {
    this.project = project;
  }
  
  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
    if(referencePropertyName != null) {
      if(propertyName != null) {
        referencePropertyName.setText(propertyName);
      } else {
        referencePropertyName.setText("(No property selected)");
      }
    }
  }
}
