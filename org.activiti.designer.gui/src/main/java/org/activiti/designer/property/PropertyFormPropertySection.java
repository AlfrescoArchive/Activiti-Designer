package org.activiti.designer.property;

import java.util.List;

import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.property.ui.FormPropertyEditor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyFormPropertySection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private FormPropertyEditor formPropertyEditor;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    Composite formPropertiesComposite = getWidgetFactory().createComposite(formComposite, SWT.WRAP);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 150);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, VSPACE);
    formPropertiesComposite.setLayoutData(data);
    GridLayout layout = new GridLayout();
    layout.marginTop = 0;
    layout.numColumns = 1;
    formPropertiesComposite.setLayout(layout);
    formPropertyEditor = new FormPropertyEditor("formPropertyEditor", formPropertiesComposite, (ModelUpdater) this);
    formPropertyEditor.getLabelControl(formPropertiesComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    
    CLabel formPropertiesLabel = getWidgetFactory().createCLabel(formComposite, "Form properties:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(formPropertiesComposite, -HSPACE);
    data.top = new FormAttachment(formPropertiesComposite, 0, SWT.TOP);
    formPropertiesLabel.setLayoutData(data);
  }
  
  @Override
  public void refresh() {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      
      PictogramElement element = getSelectedPictogramElement();
      Object bo = getBusinessObject(element);

      List<FormProperty> formPropertyList = null;
      if (bo instanceof UserTask) {
        formPropertyList = ((UserTask) bo).getFormProperties();
      } else if (bo instanceof StartEvent) {
        formPropertyList = ((StartEvent) bo).getFormProperties();
      } else {
        return;
      }
      
      formPropertyEditor.pictogramElement = pe;
      formPropertyEditor.initialize(formPropertyList);
    }
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    // nothing to do
  }
}
