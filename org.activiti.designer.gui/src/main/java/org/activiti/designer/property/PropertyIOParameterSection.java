package org.activiti.designer.property;

import java.util.List;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.IOParameter;
import org.activiti.designer.property.ui.IOParameterEditor;
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

public class PropertyIOParameterSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private IOParameterEditor inParameterEditor;
  private IOParameterEditor outParameterEditor;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    Composite inParametersComposite = getWidgetFactory().createComposite(formComposite, SWT.WRAP);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 150);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, VSPACE);
    inParametersComposite.setLayoutData(data);
    GridLayout layout = new GridLayout();
    layout.marginTop = 0;
    layout.numColumns = 1;
    inParametersComposite.setLayout(layout);
    inParameterEditor = new IOParameterEditor("inputParameterEditor", inParametersComposite);
    inParameterEditor.getLabelControl(inParametersComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    
    CLabel inParametersLabel = getWidgetFactory().createCLabel(formComposite, "Input parameters:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(inParametersComposite, -HSPACE);
    data.top = new FormAttachment(inParametersComposite, 0, SWT.TOP);
    inParametersLabel.setLayoutData(data);
    
    Composite outParametersComposite = getWidgetFactory().createComposite(formComposite, SWT.WRAP);
    data = new FormData();
    data.left = new FormAttachment(0, 150);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(inParametersComposite, VSPACE);
    outParametersComposite.setLayoutData(data);
    GridLayout outLayout = new GridLayout();
    layout.marginTop = 0;
    layout.numColumns = 1;
    outParametersComposite.setLayout(outLayout);
    outParameterEditor = new IOParameterEditor("outputParameterEditor", outParametersComposite);
    outParameterEditor.getLabelControl(outParametersComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    
    CLabel outParametersLabel = getWidgetFactory().createCLabel(formComposite, "Output parameters:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(outParametersComposite, -HSPACE);
    data.top = new FormAttachment(outParametersComposite, 0, SWT.TOP);
    outParametersLabel.setLayoutData(data);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    List<IOParameter> inParameterList = ((CallActivity) businessObject).getInParameters();
    
    inParameterEditor.pictogramElement = getSelectedPictogramElement();
    inParameterEditor.diagramEditor = getDiagramEditor();
    inParameterEditor.diagram = getDiagram();
    inParameterEditor.isInputParameters = true;
    inParameterEditor.initialize(inParameterList);
    
    List<IOParameter> outParameterList = ((CallActivity) businessObject).getOutParameters();
    
    outParameterEditor.pictogramElement = getSelectedPictogramElement();
    outParameterEditor.diagramEditor = getDiagramEditor();
    outParameterEditor.diagram = getDiagram();
    outParameterEditor.isInputParameters = false;
    outParameterEditor.initialize(outParameterList);
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    // nothing to do
  }
}
