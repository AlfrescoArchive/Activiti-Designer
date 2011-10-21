package org.activiti.designer.property;

import java.util.List;

import org.activiti.designer.property.ui.IOParameterEditor;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.IOParameter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
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

public class PropertyIOParameterSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private IOParameterEditor inParameterEditor;
  private IOParameterEditor outParameterEditor;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    Composite composite = factory.createFlatFormComposite(parent);
    FormData data;
    
    Composite inParametersComposite = factory.createComposite(composite, SWT.WRAP);
    data = new FormData();
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
    
    CLabel inParametersLabel = factory.createCLabel(composite, "Input parameters:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(inParametersComposite, -HSPACE);
    data.top = new FormAttachment(inParametersComposite, 0, SWT.TOP);
    inParametersLabel.setLayoutData(data);
    
    Composite outParametersComposite = factory.createComposite(composite, SWT.WRAP);
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
    
    CLabel outParametersLabel = factory.createCLabel(composite, "Output parameters:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(outParametersComposite, -HSPACE);
    data.top = new FormAttachment(outParametersComposite, 0, SWT.TOP);
    outParametersLabel.setLayoutData(data);
  }

  @Override
  public void refresh() {
    
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;
      
      List<IOParameter> inParameterList = ((CallActivity) bo).getInParameters();
      
      inParameterEditor.pictogramElement = pe;
      inParameterEditor.diagramEditor = getDiagramEditor();
      inParameterEditor.diagram = getDiagram();
      inParameterEditor.isInputParameters = true;
      inParameterEditor.initialize(inParameterList);
      
      List<IOParameter> outParameterList = ((CallActivity) bo).getOutParameters();
      
      outParameterEditor.pictogramElement = pe;
      outParameterEditor.diagramEditor = getDiagramEditor();
      outParameterEditor.diagram = getDiagram();
      outParameterEditor.isInputParameters = false;
      outParameterEditor.initialize(outParameterList);
   }
  }

}
