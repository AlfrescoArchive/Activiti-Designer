package org.activiti.designer.property;

import java.util.List;

import org.activiti.designer.bpmn2.model.Signal;
import org.activiti.designer.property.ui.SignalEditor;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.eclipse.emf.ecore.util.EcoreUtil;
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

public class PropertySignalSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private SignalEditor signalEditor;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;
		
		Composite signalComposite = factory.createComposite(composite, SWT.WRAP);
		data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		signalComposite.setLayoutData(data);
		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.numColumns = 1;
		signalComposite.setLayout(layout);
		signalEditor = new SignalEditor("signalEditor", signalComposite);
		signalEditor.getLabelControl(signalComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		
		CLabel signalLabel = factory.createCLabel(composite, "Signals:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(signalComposite, -HSPACE);
		data.top = new FormAttachment(signalComposite, 0, SWT.TOP);
		signalLabel.setLayoutData(data);

	}
	
	@Override
  public void refresh() {
		final Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
    if (model == null) {
      return;
    }
    
    List<Signal> signalList = model.getSignals();
    
    signalEditor.diagramEditor = getDiagramEditor();
    signalEditor.diagram = getDiagram();
    signalEditor.initialize(signalList);
  }
}