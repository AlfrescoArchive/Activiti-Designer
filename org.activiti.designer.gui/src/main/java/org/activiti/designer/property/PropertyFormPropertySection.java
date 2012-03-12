package org.activiti.designer.property;

import java.util.List;

import org.activiti.designer.bpmn2.model.FormProperty;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.property.ui.FormPropertyEditor;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyFormPropertySection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private FormPropertyEditor formPropertyEditor;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    Composite composite = factory.createFlatFormComposite(parent);
    FormData data;
    
    Composite formPropertiesComposite = factory.createComposite(composite, SWT.WRAP);
    data = new FormData();
    data.left = new FormAttachment(0, 150);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, VSPACE);
    formPropertiesComposite.setLayoutData(data);
    GridLayout layout = new GridLayout();
    layout.marginTop = 0;
    layout.numColumns = 1;
    formPropertiesComposite.setLayout(layout);
    formPropertyEditor = new FormPropertyEditor("formPropertyEditor", formPropertiesComposite);
    formPropertyEditor.getLabelControl(formPropertiesComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    
    CLabel formPropertiesLabel = factory.createCLabel(composite, "Form properties:"); //$NON-NLS-1$
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
      Object bo = getBusinessObject(pe);
      if (bo == null)
        return;
      
      List<FormProperty> formPropertyList = null;
      if(bo instanceof UserTask) {
        formPropertyList = ((UserTask) bo).getFormProperties();
      } else if(bo instanceof StartEvent) {
        formPropertyList = ((StartEvent) bo).getFormProperties();
      } else {
        return;
      }
      
      formPropertyEditor.pictogramElement = pe;
      formPropertyEditor.diagramEditor = getDiagramEditor();
      formPropertyEditor.diagram = getDiagram();
      formPropertyEditor.initialize(formPropertyList);
   }
  }

}
