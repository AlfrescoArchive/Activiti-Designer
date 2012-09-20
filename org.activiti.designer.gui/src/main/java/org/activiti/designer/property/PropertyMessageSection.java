package org.activiti.designer.property;

/**
 * @author Saeid Mirzaei
 */
import java.util.List;

import org.activiti.designer.bpmn2.model.Message;
import org.activiti.designer.property.ui.MessageEditor;
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

public class PropertyMessageSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private MessageEditor messageEditor;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    Composite composite = factory.createFlatFormComposite(parent);
    FormData data;
    
    Composite messageComposite = factory.createComposite(composite, SWT.WRAP);
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, VSPACE);
    messageComposite.setLayoutData(data);
    GridLayout layout = new GridLayout();
    layout.marginTop = 0;
    layout.numColumns = 1;
    
    messageComposite.setLayout(layout);
    messageEditor = new MessageEditor("messageEditor", messageComposite);
    messageEditor.getLabelControl(messageComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    
    CLabel messageLabel = factory.createCLabel(composite, "Messages:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(messageComposite, -HSPACE);
    data.top = new FormAttachment(messageComposite, 0, SWT.TOP);
    messageLabel.setLayoutData(data);

	}
	
	@Override
  public void refresh() {
	  final Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
    if (model == null) {
      return;
    }
    
    List<Message> messageList = model.getMessages();
    
    messageEditor.diagramEditor = getDiagramEditor();
    messageEditor.diagram = getDiagram();
    messageEditor.initialize(messageList);
    
    
  }
}