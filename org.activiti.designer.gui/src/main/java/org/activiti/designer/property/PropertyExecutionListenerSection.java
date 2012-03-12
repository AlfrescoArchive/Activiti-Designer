package org.activiti.designer.property;

import java.util.List;

import org.activiti.designer.bpmn2.model.ActivitiListener;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.property.ui.ExecutionListenerEditor;
import org.activiti.designer.util.BpmnBOUtil;
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

public class PropertyExecutionListenerSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private ExecutionListenerEditor listenerEditor;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;
		
		Composite listenersComposite = factory.createComposite(composite, SWT.WRAP);
		data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		listenersComposite.setLayoutData(data);
		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.numColumns = 1;
		listenersComposite.setLayout(layout);
		listenerEditor = new ExecutionListenerEditor("executionListenerEditor", listenersComposite);
		listenerEditor.getLabelControl(listenersComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		
		CLabel listenersLabel = factory.createCLabel(composite, "Listeners:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(listenersComposite, -HSPACE);
		data.top = new FormAttachment(listenersComposite, 0, SWT.TOP);
		listenersLabel.setLayoutData(data);

	}
	
	@Override
  public void refresh() {
    
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = BpmnBOUtil.getExecutionListenerBO(pe, getDiagram());
      if (bo == null)
        return;
      
      List<ActivitiListener> executionListenerList = BpmnBOUtil.getListeners(bo);
      
      listenerEditor.pictogramElement = pe;
      listenerEditor.diagramEditor = getDiagramEditor();
      listenerEditor.diagram = getDiagram();
      boolean isSequenceFlow = false;
      if(bo instanceof SequenceFlow) {
        isSequenceFlow = true;
      }
      listenerEditor.isSequenceFlow = isSequenceFlow;
      listenerEditor.initialize(executionListenerList);
   }
  }
}