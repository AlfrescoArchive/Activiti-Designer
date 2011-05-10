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

import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyMultiInstanceSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Button yesButton;
  private Button noButton;
	private Text loopCardinaltyText;
	private Text collectionText;
	private Text elementVariableText;
	private Text completionConditionText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		
		Composite radioTypeComposite = new Composite(composite, SWT.NULL);
    radioTypeComposite.setBackground(composite.getBackground());
    FormData data = new FormData();
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, 0);
    radioTypeComposite.setLayoutData(data);
    radioTypeComposite.setLayout(new RowLayout());
      
    yesButton = new Button(radioTypeComposite, SWT.RADIO);
    yesButton.setText("Yes");
    yesButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent event) {
        saveSequential(true);
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent event) {
        //
      }
      
    });
    noButton = new Button(radioTypeComposite, SWT.RADIO);
    noButton.setText("No");
    noButton.setSelection(true);
    noButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent event) {
        saveSequential(false);
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent event) {
        //
      }
      
    });
    
    createLabel("Sequential", composite, factory, radioTypeComposite);

		loopCardinaltyText = createText(composite, factory, radioTypeComposite);
		createLabel("Loop cardinality", composite, factory, loopCardinaltyText);

		collectionText = createText(composite, factory, loopCardinaltyText);
		createLabel("Collection", composite, factory, collectionText);
		
		elementVariableText = createText(composite, factory, collectionText);
    createLabel("Element variable", composite, factory, elementVariableText);
    
    completionConditionText = createText(composite, factory, elementVariableText);
    createLabel("Completion condition", composite, factory, completionConditionText);
	}

	@Override
	public void refresh() {
	  PictogramElement pe = getSelectedPictogramElement();
    if (pe == null) return;
    
    Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
    if (bo == null || bo instanceof Activity == false)
      return;
    
    Activity activity = (Activity) bo;
    if(activity.getLoopCharacteristics() == null) return;
    MultiInstanceLoopCharacteristics multiInstanceDef = (MultiInstanceLoopCharacteristics) activity.getLoopCharacteristics();
    
	  loopCardinaltyText.removeFocusListener(listener);
	  collectionText.removeFocusListener(listener);
	  elementVariableText.removeFocusListener(listener);
	  completionConditionText.removeFocusListener(listener);
	  
    
	  if(multiInstanceDef.isIsSequential() == true && yesButton.getSelection() == false) {
	    yesButton.setSelection(true);
	    noButton.setSelection(false);
    }
	  
	  if(multiInstanceDef.isIsSequential() == false && yesButton.getSelection() == true) {
      yesButton.setSelection(false);
      noButton.setSelection(true);
    }
	  
    if(multiInstanceDef.getLoopCardinality() != null) {
      loopCardinaltyText.setText(multiInstanceDef.getLoopCardinality());
    }
		
    if(multiInstanceDef.getInputDataItem() != null) {
      collectionText.setText(multiInstanceDef.getInputDataItem());
    }
    
    if(multiInstanceDef.getElementVariable() != null) {
      elementVariableText.setText(multiInstanceDef.getElementVariable());
    }
    
    if(multiInstanceDef.getCompletionCondition() != null) {
      completionConditionText.setText(multiInstanceDef.getCompletionCondition());
    }
    
		loopCardinaltyText.addFocusListener(listener);
		collectionText.addFocusListener(listener);
		elementVariableText.addFocusListener(listener);
		completionConditionText.addFocusListener(listener);
	}
	
	private void saveSequential(final boolean sequential) {
	  PictogramElement pe = getSelectedPictogramElement();
    if (pe == null) return;
    
    Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
    if (bo instanceof Activity == false) return;
    final Activity activity = (Activity) bo;
    
    
    DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
    TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
    ActivitiUiUtil.runModelChange(new Runnable() {
      public void run() {
        Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
        if (bo == null) {
          return;
        }
        getMultiInstanceDef(activity).setIsSequential(sequential);
      }
    }, editingDomain, "Model Update");
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
		  PictogramElement pe = getSelectedPictogramElement();
      if (pe == null) return;
      
      Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo instanceof Activity == false) return;
      final Activity activity = (Activity) bo;
      
			DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
			TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
			ActivitiUiUtil.runModelChange(new Runnable() {
				public void run() {
				  
					if (loopCardinaltyText.getText() != null) {
					  getMultiInstanceDef(activity).setLoopCardinality(loopCardinaltyText.getText());
					}
					
					if (collectionText.getText() != null) {
            getMultiInstanceDef(activity).setInputDataItem(collectionText.getText());
          }
					
					if (elementVariableText.getText() != null) {
            getMultiInstanceDef(activity).setElementVariable(elementVariableText.getText());
          }
					
					if (completionConditionText.getText() != null) {
            getMultiInstanceDef(activity).setCompletionCondition(completionConditionText.getText());
          }
				}
			}, editingDomain, "Model Update");
		}
	};
	
	private MultiInstanceLoopCharacteristics getMultiInstanceDef(Activity activity) {
	  if(activity.getLoopCharacteristics() == null) {
	    activity.setLoopCharacteristics(Bpmn2Factory.eINSTANCE.createMultiInstanceLoopCharacteristics());
	  }
	  return (MultiInstanceLoopCharacteristics) activity.getLoopCharacteristics();
	}
	
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
	
	private CLabel createLabel(String text, Composite parent, TabbedPropertySheetWidgetFactory factory, Control control) {
	  CLabel label = factory.createCLabel(parent, text);
	  FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(control, -HSPACE);
    data.top = new FormAttachment(control, 0, SWT.CENTER);
    label.setLayoutData(data);
    return label;
	}

}
