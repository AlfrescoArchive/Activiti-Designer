package org.activiti.designer.kickstart.process.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.activiti.workflow.simple.definition.TimeDurationDefinition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class TimeDurationViewer {

  protected Spinner yearText;
  protected Spinner monthText;
  protected Spinner dayText;
  protected Spinner hourText;
  protected Spinner minuteText;
  protected Spinner secondText;
  protected Composite durationComposite;
  
  protected FocusListener focusListener;
  protected ModifyListener modifyListener;
  
  protected TimeDurationDefinition definition;
  protected PropertyChangeListener listener;
  
  protected boolean changing = false;
  
  public TimeDurationViewer(Composite parent, PropertyChangeListener listener) {
    this.listener = listener;
    
    durationComposite = new Composite(parent, SWT.NONE);
    durationComposite.setBackground(parent.getBackground());
    durationComposite.setLayout(new GridLayout(12, false));
    
    focusListener = new FocusListener() {
      
      @Override
      public void focusLost(FocusEvent e) {
        populateModel();
      }
      
      @Override
      public void focusGained(FocusEvent e) {
        populateControls();        
      }
    }; 
    
    modifyListener = new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        populateModel();
      }
    };
    
    yearText = createAndAddDurationText("Years:");
    monthText = createAndAddDurationText("Months:");
    dayText = createAndAddDurationText("Days:");
    hourText = createAndAddDurationText("Hours:");
    minuteText = createAndAddDurationText("Minutes:");
    secondText = createAndAddDurationText("Seconds:");
  }
  
  public Composite getComposite() {
    return durationComposite;
  }
  
  public void setDefinition(TimeDurationDefinition definition) {
    this.definition = definition;
    populateControls();
  }

  
  protected void populateModel() {
    if(changing) {
      return;
    }
    
    if(definition != null && secondText != null) {
      boolean changed = false;
      
      if (getSafeIntValue(definition.getYears()) != yearText.getSelection()) {
        changed = true;
        definition.setYears(yearText.getSelection());
      }
      if (getSafeIntValue(definition.getMonths()) != monthText.getSelection()) {
        changed = true;
        definition.setMonths(monthText.getSelection());
      }
      if (getSafeIntValue(definition.getDays()) != dayText.getSelection()) {
        changed = true;
        definition.setDays(dayText.getSelection());
      }
      if (getSafeIntValue(definition.getHours()) != hourText.getSelection()) {
        changed = true;
        definition.setHours(hourText.getSelection());
      }
      if (getSafeIntValue(definition.getMinutes()) != minuteText.getSelection()) {
        changed = true;
        definition.setMinutes(minuteText.getSelection());
      }
      if (getSafeIntValue(definition.getSeconds()) != secondText.getSelection()) {
        changed = true;
        definition.setSeconds(secondText.getSelection());
      }
      if(changed && listener != null) {
        listener.propertyChange(new PropertyChangeEvent(this, "duration", null, null));
      }
    }
  }
  
  protected Spinner createAndAddDurationText(String label) {
    addDurationLabel(label);
    Spinner result = new Spinner(durationComposite, SWT.BORDER);
    result.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    result.addFocusListener(focusListener);
    result.addModifyListener(modifyListener);
    result.setMaximum(Integer.MAX_VALUE);
    result.setMinimum(0);
    return result;
  }

  protected void addDurationLabel(String label) {
    Label component = new Label(durationComposite, SWT.NONE);
    component.setText(label);
  }
  
  protected void populateControls() {
    changing = true;
    
    if(definition != null) {
      if(!yearText.isFocusControl()) {
        yearText.setSelection(getSafeIntValue(definition.getYears()));
      }
      if(!monthText.isFocusControl()) {
        monthText.setSelection(getSafeIntValue(definition.getMonths()));
      }
      if(!dayText.isFocusControl()) {
        dayText.setSelection(getSafeIntValue(definition.getDays()));
      }
      if(!hourText.isFocusControl()) {
        hourText.setSelection(getSafeIntValue(definition.getHours()));
      }
      if(!minuteText.isFocusControl()) {
        minuteText.setSelection(getSafeIntValue(definition.getMinutes()));
      }
      if(!secondText.isFocusControl()) {
        secondText.setSelection(getSafeIntValue(definition.getSeconds()));
      }
    } else {
      // Clear all widgets
      monthText.setSelection(0);
      dayText.setSelection(0);
      hourText.setSelection(0);
      minuteText.setSelection(0);
      secondText.setSelection(0);
    }
    
    changing = false;
  }
  
  protected int getSafeIntValue(Integer value) {
    if(value != null) {
      return value;
    }
    return 0;
  }
}
