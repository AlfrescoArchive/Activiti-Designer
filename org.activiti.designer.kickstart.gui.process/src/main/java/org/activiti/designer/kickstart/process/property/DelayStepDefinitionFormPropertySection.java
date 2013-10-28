package org.activiti.designer.kickstart.process.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.activiti.designer.kickstart.process.command.KickstartProcessModelUpdater;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.DelayStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.TimeDurationDefinition;
import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section containing all properties in the main {@link StepDefinition}.
 * 
 * @author Frederik Heremans
 */
public class DelayStepDefinitionFormPropertySection extends AbstractKickstartProcessPropertySection {

  protected StringItemSelectionViewer dateTimeViewer;
  protected TimeDurationViewer timeDurationViewer;
  protected PropertyItemBrowser itemBrowser;
  protected ToggleContainerViewer toggler;
  protected Composite options;
  protected Button durationOption;
  protected Button dateTimeOption;
  
  @Override
  protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    options = new Composite(formComposite, SWT.NONE);
    options.setLayout(new GridLayout(2, true));
    options.setBackground(formComposite.getBackground());
    durationOption = new Button(options, SWT.RADIO);
    durationOption.setText("Select step end date");
    durationOption.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if(durationOption.getSelection()) {
          ensureTimeDuration();
          toggler.showChild("duration");
        }
      }
    });
    
    dateTimeOption = new Button(options, SWT.RADIO);
    dateTimeOption.setText("Fixed step duration");
    dateTimeOption.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if(dateTimeOption.getSelection()) {
          ensureTimeDate();
        }
      }
    });
    
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, 0);
    data.top = createTopFormAttachment();
    options.setLayoutData(data);
    registerControl(options);
    
    toggler = new ToggleContainerViewer(formComposite);
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, 0);
    data.top = createTopFormAttachment();
    toggler.getComposite().setLayoutData(data);
    
    createSeparator();
    
    itemBrowser = new PropertyItemBrowser();
    itemBrowser.setItemfilter(new TypedPropertyItemFilter(DatePropertyDefinition.class));
    dateTimeViewer = new StringItemSelectionViewer(toggler.getComposite(), false, new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent event) {
        DelayStepDefinition def = (DelayStepDefinition) getModelUpdater().getUpdatableBusinessObject();
        if(dateTimeViewer.getItem() != null && !dateTimeViewer.getItem().isEmpty()) {
          def.setTimeDate((String) dateTimeViewer.getItem());
        } else {
          def.setTimeDate(null);
        }
        executeModelUpdater();
      }
    }, itemBrowser);
    
    toggler.addControl("dateTime", dateTimeViewer.getComposite());
    
    timeDurationViewer = new TimeDurationViewer(toggler.getComposite(), new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        executeModelUpdater();
      }
    });
    toggler.addControl("duration", timeDurationViewer.getComposite());
  }
  
  @Override
  protected void populateControl(Control control, Object businessObject) {
    if(dateTimeViewer.getComposite() == control) {
      DelayStepDefinition stepDefinition = (DelayStepDefinition) businessObject;
      dateTimeViewer.setItem(stepDefinition.getTimeDate());
    }
  }
  
  @Override
  @SuppressWarnings("unchecked")
  protected KickstartProcessModelUpdater<DelayStepDefinition> createModelUpdater() {
    
    KickstartProcessModelUpdater<DelayStepDefinition> createModelUpdater = (KickstartProcessModelUpdater<DelayStepDefinition>) super.createModelUpdater();
    if(createModelUpdater.getUpdatableBusinessObject().getTimeDuration() != null) {
      timeDurationViewer.setDefinition(createModelUpdater.getUpdatableBusinessObject().getTimeDuration());
    }
    
    return createModelUpdater;
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    return null;
  }
  
  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {
    super.setInput(part, selection);
    
    KickstartProcessMemoryModel model = ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram()));
    if (model != null && model.getModelFile() != null) {
      itemBrowser.setProject(model.getModelFile().getProject());
      itemBrowser.setWorkflowDefinition(model.getWorkflowDefinition());
    }
  }

  
  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
  }

  protected void ensureTimeDuration() {
    DelayStepDefinition stepDefinition = (DelayStepDefinition) getModelUpdater().getUpdatableBusinessObject();
    if(stepDefinition.getTimeDuration() == null) {
      stepDefinition.setTimeDuration(new TimeDurationDefinition());
      executeModelUpdater();
    }
  }
  
  protected void ensureTimeDate() {
    DelayStepDefinition stepDefinition = (DelayStepDefinition) getModelUpdater().getUpdatableBusinessObject();
    if(stepDefinition.getTimeDate() == null) {
      stepDefinition.setTimeDate("");
      executeModelUpdater();
    }
  }
  
  
  
  @Override
  public void refresh() {
    super.refresh();
    
    if(toggler != null) {
      if(getSelectedPictogramElement() != null) {
        DelayStepDefinition definition = (DelayStepDefinition) getBusinessObject(getSelectedPictogramElement());
        if(definition.getTimeDuration() != null) {
          toggler.showChild("duration");
          durationOption.setSelection(true);
          timeDurationViewer.setDefinition(definition.getTimeDuration());
        } else {
          toggler.showChild("dateTime");
          dateTimeOption.setSelection(true);
          dateTimeViewer.setItem(definition.getTimeDate());
        }
      }
    }
    resetModelUpdater();
  }

}
