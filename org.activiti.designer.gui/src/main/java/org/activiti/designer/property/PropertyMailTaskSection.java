package org.activiti.designer.property;

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

public class PropertyMailTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text toText;
	private Text fromText;
	private Text subjectText;
	private Text ccText;
	private Text bccText;
	private Text charsetText;
	private Text htmlText;
	private Text nonHtmlText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		
		toText = createControl(composite, null, false);
		createLabel(composite, "To:", toText); //$NON-NLS-1$
		fromText = createControl(composite, toText, false);
		createLabel(composite, "From:", fromText); //$NON-NLS-1$
		subjectText = createControl(composite, fromText, false);
		createLabel(composite, "Subject:", subjectText); //$NON-NLS-1$
		ccText = createControl(composite, subjectText, false);
		createLabel(composite, "Cc:", ccText); //$NON-NLS-1$
		bccText = createControl(composite, ccText, false);
		createLabel(composite, "Bcc:", bccText); //$NON-NLS-1$
		charsetText = createControl(composite, bccText, false);
		createLabel(composite, "Charset:", charsetText); //$NON-NLS-1$
		htmlText = createControl(composite, charsetText, true);
		createLabel(composite, "Html text:", htmlText); //$NON-NLS-1$
		nonHtmlText = createControl(composite, htmlText, true);
		createLabel(composite, "Non-Html text:", nonHtmlText); //$NON-NLS-1$

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
			fromText.removeFocusListener(listener);
			subjectText.removeFocusListener(listener);
			ccText.removeFocusListener(listener);
			bccText.removeFocusListener(listener);
			charsetText.removeFocusListener(listener);
			htmlText.removeFocusListener(listener);
			nonHtmlText.removeFocusListener(listener);
			Object bo = getBusinessObject(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;

			ServiceTask mailTask = (ServiceTask)  bo;
			
			toText.setText(getFieldString("to", mailTask));
			fromText.setText(getFieldString("from", mailTask));
			subjectText.setText(getFieldString("subject", mailTask));
			ccText.setText(getFieldString("cc", mailTask));
			bccText.setText(getFieldString("bcc", mailTask));
			charsetText.setText(getFieldString("charset", mailTask));
			htmlText.setText(getFieldString("html", mailTask));
			nonHtmlText.setText(getFieldString("text", mailTask));
			
			toText.addFocusListener(listener);
			fromText.addFocusListener(listener);
			subjectText.addFocusListener(listener);
			ccText.addFocusListener(listener);
			bccText.addFocusListener(listener);
			charsetText.addFocusListener(listener);
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
					ServiceTask mailTask = (ServiceTask) bo;
					updateMailField(mailTask, e.getSource());
				}
			}
		}
	};
	
	protected void updateMailField(final ServiceTask mailTask, final Object source) {
    String oldValue = null;
    final String newValue = ((Text) source).getText();
    if (source == toText) {
      oldValue = getFieldString("to", mailTask);
    } else if (source == fromText) {
      oldValue = getFieldString("from", mailTask);
    } else if (source == subjectText) {
      oldValue = getFieldString("subject", mailTask);
    } else if (source == ccText) {
      oldValue = getFieldString("cc", mailTask);
    } else if (source == bccText) {
      oldValue = getFieldString("bcc", mailTask);
    } else if (source == charsetText) {
      oldValue = getFieldString("charset", mailTask);
    } else if (source == htmlText) {
      oldValue = getFieldString("html", mailTask);
    } else if (source == nonHtmlText) {
      oldValue = getFieldString("text", mailTask);
    }
    
    if ((StringUtils.isEmpty(oldValue) && StringUtils.isNotEmpty(newValue)) || (StringUtils.isNotEmpty(oldValue) && newValue.equals(oldValue) == false)) {
      IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
        
        @Override
        public void execute(IContext context) {
          if (source == toText) {
            setFieldString("to", toText.getText(), mailTask);
          } else if (source == fromText) {
            setFieldString("from", fromText.getText(), mailTask);
          } else if (source == subjectText) {
            setFieldString("subject", subjectText.getText(), mailTask);
          } else if (source == ccText) {
            setFieldString("cc", ccText.getText(), mailTask);
          } else if (source == bccText) {
            setFieldString("bcc", bccText.getText(), mailTask);
          } else if (source == charsetText) {
            setFieldString("charset", charsetText.getText(), mailTask);
          } else if (source == htmlText) {
            setFieldString("html", htmlText.getText(), mailTask);
          } else if (source == nonHtmlText) {
            setFieldString("text", nonHtmlText.getText(), mailTask);
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
