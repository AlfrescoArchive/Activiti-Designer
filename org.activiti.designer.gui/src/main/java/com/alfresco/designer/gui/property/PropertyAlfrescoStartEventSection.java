package com.alfresco.designer.gui.property;

import java.util.List;

import org.activiti.bpmn.model.StartEvent;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyAlfrescoStartEventSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	
  private CCombo formTypeCombo;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		formTypeCombo = factory.createCCombo(composite, SWT.NONE); //$NON-NLS-1$
		List<String> formTypes = PreferencesUtil.getStringArray(Preferences.ALFRESCO_FORMTYPES_STARTEVENT);
		for (String formType : formTypes) {
		  formTypeCombo.add(formType);
    }
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, VSPACE);
    formTypeCombo.setLayoutData(data);
    formTypeCombo.addFocusListener(listener);

    CLabel formKeyLabel = factory.createCLabel(composite, "Form key:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(formTypeCombo, -HSPACE);
    data.top = new FormAttachment(formTypeCombo, 0, SWT.TOP);
    formKeyLabel.setLayoutData(data);

	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = getBusinessObject(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;

			StartEvent startEvent = ((StartEvent) bo);
			if(startEvent.getFormKey() != null) {
				
			  formTypeCombo.removeFocusListener(listener);
				String condition = startEvent.getFormKey();
				formTypeCombo.setText(condition);
				formTypeCombo.addFocusListener(listener);
			} else {
			  formTypeCombo.setText("");
			}
		}
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				final Object bo = getBusinessObject(pe);
				if (bo instanceof StartEvent) {
					IDiagramContainer diagramContainer = getDiagramContainer();
					TransactionalEditingDomain editingDomain = diagramContainer.getDiagramBehavior().getEditingDomain();
					ActivitiUiUtil.runModelChange(new Runnable() {
						public void run() {
							StartEvent startEvent = (StartEvent) bo;
							String formKey = formTypeCombo.getText();
							if (formKey != null && formKey.length() > 0) {
							  startEvent.setFormKey(formKey);
								
							} else {
								startEvent.setFormKey("");
							}
						}
					}, editingDomain, "Model Update");
				}

			}
		}
	};

	protected void updateStartEventField(final StartEvent startEvent, final Object source) {
    String oldValue = startEvent.getFormKey();
    final String newValue = ((CCombo) source).getText();
    
    if ((StringUtils.isEmpty(oldValue) && StringUtils.isNotEmpty(newValue)) || (StringUtils.isNotEmpty(oldValue) && newValue.equals(oldValue) == false)) {
      IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
        
        @Override
        public void execute(IContext context) {
          startEvent.setFormKey(newValue);
        }
        
        @Override
        public boolean canExecute(IContext context) {
          return true;
        }
      };
      CustomContext context = new CustomContext();
      execute(feature, context);
    }
  }
}
