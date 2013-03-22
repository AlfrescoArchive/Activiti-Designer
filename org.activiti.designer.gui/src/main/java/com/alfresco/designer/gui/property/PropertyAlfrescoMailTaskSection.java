package com.alfresco.designer.gui.property;

import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
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

public class PropertyAlfrescoMailTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text toText;
	private Text toManyText;
	private Text fromText;
	private Text subjectText;
	private Text htmlText;
	private Text nonHtmlText;
	private Text templateText;
	private Text templateModelText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		
		toText = createControl(composite, null, false);
		createLabel(composite, "To:", toText); //$NON-NLS-1$
		toManyText = createControl(composite, toText, false);
		createLabel(composite, "To many:", toManyText); //$NON-NLS-1$
		fromText = createControl(composite, toManyText, false);
		createLabel(composite, "From:", fromText); //$NON-NLS-1$
		subjectText = createControl(composite, fromText, false);
		createLabel(composite, "Subject:", subjectText); //$NON-NLS-1$
		templateText = createControl(composite, subjectText, false);
		createLabel(composite, "Template:", templateText); //$NON-NLS-1$
		templateModelText = createControl(composite, templateText, false);
		createLabel(composite, "Template model:", templateModelText); //$NON-NLS-1$
		htmlText = createControl(composite, templateModelText, true);
		createLabel(composite, "Html:", htmlText); //$NON-NLS-1$
		nonHtmlText = createControl(composite, htmlText, true);
		createLabel(composite, "Text:", nonHtmlText); //$NON-NLS-1$
	}
	
	private Text createControl(Composite composite, Text otherTextControl, boolean multi) {
		Text textControl = null;
		FormData data = null;
		if(multi == true) {
			textControl = getWidgetFactory().createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			data = new FormData(SWT.DEFAULT, 100);
		} else {
			textControl = getWidgetFactory().createText(composite, "", SWT.NONE);
			data = new FormData();
		}
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		if(otherTextControl == null) {
			data.top = new FormAttachment(0, VSPACE);
		} else {
			data.top = new FormAttachment(otherTextControl, VSPACE);
		}
		textControl.setLayoutData(data);
		textControl.addFocusListener(listener);
		return textControl;
	}
	
	private CLabel createLabel(Composite composite, String labelName, Text textControl) {
		CLabel labelControl = getWidgetFactory().createCLabel(composite, labelName); //$NON-NLS-1$
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(textControl, -HSPACE);
		data.top = new FormAttachment(textControl, 0, SWT.CENTER);
		labelControl.setLayoutData(data);
		return labelControl;
	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			toText.removeFocusListener(listener);
			toManyText.removeFocusListener(listener);
			fromText.removeFocusListener(listener);
			subjectText.removeFocusListener(listener);
			templateText.removeFocusListener(listener);
			templateModelText.removeFocusListener(listener);
			htmlText.removeFocusListener(listener);
			nonHtmlText.removeFocusListener(listener);
			Object bo = getBusinessObject(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;

			ServiceTask mailTask = (ServiceTask)  bo;
			String to = getFieldString("mail.parameters.to", mailTask);
			toText.setText(to == null ? "" : to);
			String toMany = getFieldString("mail.parameters.to_many", mailTask);
			toManyText.setText(toMany == null ? "" : toMany);
			String from = getFieldString("mail.parameters.from", mailTask);
			fromText.setText(from == null ? "" : from);
			String subject = getFieldString("mail.parameters.subject", mailTask);
			subjectText.setText(subject == null ? "" : subject);
			String template = getFieldString("mail.parameters.template", mailTask);
			templateText.setText(template == null ? "" : template);
			String templateModel = getFieldString("mail.parameters.template_model", mailTask);
			templateModelText.setText(templateModel == null ? "" : templateModel);
			String html = getFieldString("mail.parameters.html", mailTask);
			htmlText.setText(html == null ? "" : html);
			String text = getFieldString("mail.parameters.text", mailTask);
			nonHtmlText.setText(text == null ? "" : text);
			
			toText.addFocusListener(listener);
			toManyText.addFocusListener(listener);
			fromText.addFocusListener(listener);
			subjectText.addFocusListener(listener);
			templateText.addFocusListener(listener);
			templateModelText.addFocusListener(listener);
			htmlText.addFocusListener(listener);
			nonHtmlText.addFocusListener(listener);
		}
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				final Object bo = getBusinessObject(pe);
				if (bo instanceof ServiceTask) {
				  updateMailField((ServiceTask) bo, e.getSource());
				}
			}
		}
	};
	
	protected void updateMailField(final ServiceTask mailTask, final Object source) {
    String oldValue = null;
    final String newValue = ((Text) source).getText();
    if (source == toText) {
      oldValue = getFieldString("mail.parameters.to", mailTask);
    } else if (source == toManyText) {
      oldValue = getFieldString("mail.parameters.to_many", mailTask);
    } else if (source == fromText) {
      oldValue = getFieldString("mail.parameters.from", mailTask);
    } else if (source == subjectText) {
      oldValue = getFieldString("mail.parameters.subject", mailTask);
    } else if (source == templateText) {
      oldValue = getFieldString("mail.parameters.template", mailTask);
    } else if (source == templateModelText) {
      oldValue = getFieldString("mail.parameters.template_model", mailTask);
    } else if (source == htmlText) {
      oldValue = getFieldString("mail.parameters.html", mailTask);
    } else if (source == nonHtmlText) {
      oldValue = getFieldString("mail.parameters.text", mailTask);
    }
    
    if ((StringUtils.isEmpty(oldValue) && StringUtils.isNotEmpty(newValue)) || (StringUtils.isNotEmpty(oldValue) && newValue.equals(oldValue) == false)) {
      IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
        
        @Override
        public void execute(IContext context) {
          if (source == toText) {
            setFieldString("mail.parameters.to", newValue, mailTask);
          } else if (source == toManyText) {
            setFieldString("mail.parameters.to_many", newValue, mailTask);
          } else if (source == fromText) {
            setFieldString("mail.parameters.from", newValue, mailTask);
          } else if (source == subjectText) {
            setFieldString("mail.parameters.subject", newValue, mailTask);
          } else if (source == templateText) {
            setFieldString("mail.parameters.template", newValue, mailTask);
          } else if (source == templateModelText) {
            setFieldString("mail.parameters.template_model", newValue, mailTask);
          } else if (source == htmlText) {
            setFieldString("mail.parameters.html", newValue, mailTask);
          } else if (source == nonHtmlText) {
            setFieldString("mail.parameters.text", newValue, mailTask);
          }
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
