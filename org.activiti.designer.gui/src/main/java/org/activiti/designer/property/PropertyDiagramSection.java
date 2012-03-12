/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyDiagramSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text idText;
	private Text nameText;
	private Text namespaceText;
	private Text documentationText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		idText = createText(composite, factory, null);
		createLabel(composite, "Id:", idText, factory); //$NON-NLS-1$
		
		nameText = createText(composite, factory, idText);
		createLabel(composite, "Name:", nameText, factory); //$NON-NLS-1$
		
		namespaceText = createText(composite, factory, nameText);
		createLabel(composite, "Namespace:", namespaceText, factory); //$NON-NLS-1$
		
		documentationText = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
		data = new FormData(SWT.DEFAULT, 100);
		data.left = new FormAttachment(0, 160);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(namespaceText, VSPACE);
		documentationText.setLayoutData(data);
		documentationText.addFocusListener(listener);

		createLabel(composite, "Documentation:", documentationText, factory); //$NON-NLS-1$
	}

	@Override
	public void refresh() {
	  idText.removeFocusListener(listener);
		nameText.removeFocusListener(listener);
		namespaceText.removeFocusListener(listener);
		documentationText.removeFocusListener(listener);
		Process process = ActivitiUiUtil.getProcessObject(getDiagram());
		idText.setText(process.getId());
		nameText.setText(process.getName());
		if(StringUtils.isNotEmpty(process.getNamespace())) {
			namespaceText.setText(process.getNamespace());
		} else {
			namespaceText.setText("http://www.activiti.org/test");
		}
		if(StringUtils.isNotEmpty(process.getDocumentation())) {
			documentationText.setText(process.getDocumentation());
		} else {
			documentationText.setText("");
		}
		
		idText.addFocusListener(listener);
		nameText.addFocusListener(listener);
		namespaceText.addFocusListener(listener);
		documentationText.addFocusListener(listener);
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
			TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
			ActivitiUiUtil.runModelChange(new Runnable() {
				public void run() {
					Process process = ActivitiUiUtil.getProcessObject(getDiagram());
					if (process == null) {
						return;
					}
					
					String id = idText.getText();
					if (id != null) {
					  process.setId(id);
					} else {
						process.setId("");
					}
					
					String name = nameText.getText();
					if (name != null) {
						process.setName(name);
					} else {
						process.setName("");
					}
					
					String namespace = namespaceText.getText();
					if (namespace != null) {
						process.setNamespace(namespace);
					} else {
						process.setNamespace("");
					}
					
					String documentation = documentationText.getText();
					if (documentation != null) {
						process.setDocumentation(documentation);
					} else {
						process.setDocumentation("");
					}
				}
			}, editingDomain, "Model Update");
		}
	};
	
	private Text createText(Composite parent, TabbedPropertySheetWidgetFactory factory, Control top) {
    Text text = factory.createText(parent, ""); //$NON-NLS-1$
    FormData data = new FormData();
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, -HSPACE);
    if(top == null) {
      data.top = new FormAttachment(0, VSPACE);
    } else {
      data.top = new FormAttachment(top, VSPACE);
    }
    text.setLayoutData(data);
    text.addFocusListener(listener);
    return text;
  }
  
  private CLabel createLabel(Composite parent, String text, Control control, TabbedPropertySheetWidgetFactory factory) {
    CLabel label = factory.createCLabel(parent, text); //$NON-NLS-1$
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(control, -HSPACE);
    data.top = new FormAttachment(control, 0, SWT.TOP);
    label.setLayoutData(data);
    return label;
  }

}
