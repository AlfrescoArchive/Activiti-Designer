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

import java.util.List;

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.BaseElement;
import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.Gateway;
import org.activiti.designer.bpmn2.model.InclusiveGateway;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.util.TextUtil;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyBpmnSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Composite composite;
  private Text idText;
  private Text nameText;
  private CCombo asyncCombo;
  private CCombo defaultCombo;
  private CLabel defaultLabel;
  private CLabel asyncLabel;
  private CCombo exclusiveCombo;
  private CLabel exclusiveLabel;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    composite = factory.createFlatFormComposite(parent);
    FormData data;

    idText = factory.createText(composite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, -HSPACE);
    data.top = new FormAttachment(0, VSPACE);
    idText.setLayoutData(data);

    CLabel idLabel = factory.createCLabel(composite, "Id:", SWT.WRAP); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(idText, -HSPACE);
    data.top = new FormAttachment(idText, 0, SWT.CENTER);
    idLabel.setLayoutData(data);

    nameText = factory.createText(composite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, -HSPACE);
    data.top = new FormAttachment(idText, VSPACE);
    nameText.setLayoutData(data);
    nameText.addFocusListener(listener);

    CLabel valueLabel = factory.createCLabel(composite, "Name:", SWT.WRAP); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(nameText, -HSPACE);
    data.top = new FormAttachment(nameText, 0, SWT.CENTER);
    valueLabel.setLayoutData(data);
  }

  @Override
  public void refresh() {
    nameText.removeFocusListener(listener);
    idText.removeFocusListener(listener);
    if(defaultCombo != null) {
    	defaultCombo.removeFocusListener(listener);
    }
    if(asyncCombo != null) {
    	asyncCombo.removeFocusListener(listener);
    }

    PictogramElement pe = getSelectedPictogramElement();

    if (pe != null) {
      Object bo = getBusinessObject(pe);
      // the filter assured, that it is a EClass
      if (bo == null)
        return;
      
      String name = null;
      if (bo instanceof FlowElement) {
        name = ((FlowElement) bo).getName();
      } else if (bo instanceof Pool) {
        name = ((Pool) bo).getName();
      } else if (bo instanceof Lane) {
        name = ((Lane) bo).getName();
      }
      String id = ((BaseElement) bo).getId();
      nameText.setText(name == null ? "" : name);
      idText.setText(id == null ? "" : id);
      
      boolean disableDefault = true;
      
      if (bo instanceof Activity || bo instanceof ExclusiveGateway ||
              bo instanceof InclusiveGateway) {
        
        List<SequenceFlow> flowList = null;
        if(bo instanceof Activity) {
          flowList = ((Activity) bo).getOutgoing();
        } else if(bo instanceof ExclusiveGateway) {
          flowList = ((ExclusiveGateway) bo).getOutgoing();
        } else {
          flowList = ((InclusiveGateway) bo).getOutgoing();
        }
        if(flowList != null && flowList.size() > 1) {
          
        	if(defaultCombo == null) {
        		defaultCombo = getWidgetFactory().createCCombo(composite, SWT.NONE);
            FormData data = new FormData();
            data.left = new FormAttachment(0, 120);
            data.right = new FormAttachment(100, 0);
            data.top = new FormAttachment(nameText, VSPACE);
            defaultCombo.setLayoutData(data);
            defaultCombo.setVisible(false);
            defaultCombo.addFocusListener(listener);

            defaultLabel = getWidgetFactory().createCLabel(composite, "Default flow:"); //$NON-NLS-1$
            data = new FormData();
            data.left = new FormAttachment(0, 0);
            data.right = new FormAttachment(defaultCombo, -HSPACE);
            data.top = new FormAttachment(defaultCombo, 0, SWT.CENTER);
            defaultLabel.setLayoutData(data);
            defaultLabel.setVisible(false);
        	}
        	
        	defaultCombo.removeAll();
        	
          for (SequenceFlow flow : flowList) {
            defaultCombo.add(flow.getId());
          }
          
          String defaultFlow = null;
          if(bo instanceof Activity) {
            defaultFlow = ((Activity) bo).getDefaultFlow();
          } else if(bo instanceof Gateway) {
            defaultFlow = ((Gateway) bo).getDefaultFlow();
          }
          
          if(defaultFlow != null) {
            defaultCombo.select(defaultCombo.indexOf(defaultFlow));
          }
          disableDefault = false;
        }
      }
      
      if(defaultCombo != null) {
	      if(disableDefault == true) {
	        defaultCombo.setVisible(false);
	        defaultLabel.setVisible(false);
	        
	      } else {
	        defaultCombo.setVisible(true);
	        defaultLabel.setVisible(true);
	      }
      }
      
      if (bo instanceof Activity) {
      	
      	if(asyncCombo == null) {
      		asyncCombo = getWidgetFactory().createCCombo(composite, SWT.DROP_DOWN | SWT.BORDER);
          asyncCombo.add("");
          asyncCombo.add("True");
          FormData data = new FormData();
          data.left = new FormAttachment(0, 120);
          data.right = new FormAttachment(50, 0);
          if(defaultCombo != null) {
          	data.top = new FormAttachment(defaultCombo, VSPACE);
          } else {
          	data.top = new FormAttachment(nameText, VSPACE);
          }
          asyncCombo.setLayoutData(data);
          asyncCombo.setVisible(false);
          asyncCombo.addFocusListener(listener);
          
          asyncLabel = getWidgetFactory().createCLabel(composite, "Asynchronous:"); //$NON-NLS-1$
          data = new FormData();
          data.left = new FormAttachment(0, 0);
          data.right = new FormAttachment(asyncCombo, -HSPACE);
          data.top = new FormAttachment(asyncCombo, 0, SWT.CENTER);
          asyncLabel.setLayoutData(data);
          asyncLabel.setVisible(false);
      	}
      	
      	Activity activity = (Activity) bo;
      	if(activity.isAsynchronous()) {
      		asyncCombo.select(1);
      	} else {
      		asyncCombo.select(0);
      	}
      	
      	asyncCombo.setVisible(true);
      	asyncLabel.setVisible(true);
      	
      	if(exclusiveCombo == null) {
      	  exclusiveCombo = getWidgetFactory().createCCombo(composite, SWT.DROP_DOWN | SWT.BORDER);
      	  exclusiveCombo.add("");
      	  exclusiveCombo.add("False");
          FormData data = new FormData();
          data.left = new FormAttachment(0, 120);
          data.right = new FormAttachment(50, 0);
          data.top = new FormAttachment(asyncCombo, VSPACE);
          exclusiveCombo.setLayoutData(data);
          exclusiveCombo.setVisible(false);
          exclusiveCombo.addFocusListener(listener);
          
          exclusiveLabel = getWidgetFactory().createCLabel(composite, "Exclusive:"); //$NON-NLS-1$
          data = new FormData();
          data.left = new FormAttachment(0, 0);
          data.right = new FormAttachment(exclusiveCombo, -HSPACE);
          data.top = new FormAttachment(exclusiveCombo, 0, SWT.CENTER);
          exclusiveLabel.setLayoutData(data);
          exclusiveLabel.setVisible(false);
        }
        
        if(activity.isNotExclusive()) {
          exclusiveCombo.select(1);
        } else {
          exclusiveCombo.select(0);
        }
        
        exclusiveCombo.setVisible(true);
        exclusiveLabel.setVisible(true);
      
      } else {
        if(asyncCombo != null) {
          asyncCombo.setVisible(false);
          asyncLabel.setVisible(false);
        }
        
        if(exclusiveCombo != null) {
          exclusiveCombo.setVisible(false);
          exclusiveLabel.setVisible(false);
        }
      }
      
      idText.addFocusListener(listener);
      nameText.addFocusListener(listener);
      if(defaultCombo != null) {
      	defaultCombo.addFocusListener(listener);
      }
      if(asyncCombo != null) {
      	asyncCombo.addFocusListener(listener);
      }
    }
  }

  private FocusListener listener = new FocusListener() {

    public void focusGained(final FocusEvent e) {
    }

    public void focusLost(final FocusEvent e) {
      final PictogramElement pe = getSelectedPictogramElement();
      if (pe == null)
        return;
      final Object bo = getBusinessObject(pe);

      if (!(bo instanceof BaseElement))
        return;

      DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
      TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
      ActivitiUiUtil.runModelChange(new Runnable() {

        public void run() {
          String id = idText.getText();
          ((BaseElement) bo).setId(id);
          
          String name = nameText.getText();
          if (bo instanceof FlowElement) {
            ((FlowElement) bo).setName(name);
          } else if (bo instanceof Pool) {
            ((Pool) bo).setName(name);
          } else if (bo instanceof Lane) {
            ((Lane) bo).setName(name);
          }
          
          UpdateContext updateContext = new UpdateContext(pe);
          IUpdateFeature updateFeature = getFeatureProvider(pe).getUpdateFeature(updateContext);
          if(updateFeature != null) {
            updateFeature.update(updateContext);
          }
          
          if (pe instanceof ContainerShape) {
            ContainerShape cs = (ContainerShape) pe;
            for (Shape shape : cs.getChildren()) {
              if (shape.getGraphicsAlgorithm() instanceof Text) {
                org.eclipse.graphiti.mm.algorithms.Text text = (org.eclipse.graphiti.mm.algorithms.Text) shape.getGraphicsAlgorithm();
                text.setValue(name);
              }
              if (shape.getGraphicsAlgorithm() instanceof MultiText) {
                MultiText text = (MultiText) shape.getGraphicsAlgorithm();
                text.setValue(name);
              }
            }
          }
          
          if((bo instanceof Activity || bo instanceof ExclusiveGateway || bo instanceof InclusiveGateway)
                  && defaultCombo != null && defaultCombo.isVisible() == true) {
            
            String defaultValue = defaultCombo.getText();
            if(bo instanceof Activity) {
              ((Activity) bo).setDefaultFlow(defaultValue);
            } else if(bo instanceof Gateway) {
              ((Gateway) bo).setDefaultFlow(defaultValue);
            }
          }
          
          if(bo instanceof Activity && asyncCombo != null) {
          	Activity activity = (Activity) bo;
          	if("true".equalsIgnoreCase(asyncCombo.getText())) {
          		activity.setAsynchronous(true);
          	} else {
          		activity.setAsynchronous(false);
          	}
          }
          
          if(bo instanceof Activity && exclusiveCombo != null) {
            Activity activity = (Activity) bo;
            if("false".equalsIgnoreCase(exclusiveCombo.getText())) {
              activity.setNotExclusive(true);
            } else {
              activity.setNotExclusive(false);
            }
          }
          
          if (!(getSelectedPictogramElement() instanceof FreeFormConnection))
            return;
          EList<ConnectionDecorator> decoratorList = ((FreeFormConnection) getSelectedPictogramElement()).getConnectionDecorators();
          for (ConnectionDecorator decorator : decoratorList) {
            if (decorator.getGraphicsAlgorithm() instanceof org.eclipse.graphiti.mm.algorithms.MultiText) {
              org.eclipse.graphiti.mm.algorithms.MultiText text = (org.eclipse.graphiti.mm.algorithms.MultiText) decorator.getGraphicsAlgorithm();
              text.setValue(name);
              TextUtil.setTextSize(name, text);
            }
          }
        }
      }, editingDomain, "Model Update");
    }
  };

}
