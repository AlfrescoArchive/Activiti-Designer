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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyAlfrescoScriptTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text scriptText;
	private Text runAsText;
	private Text scriptProcessorText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		scriptText = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
		data = new FormData(SWT.DEFAULT, 100);
		data.left = new FormAttachment(0, 160);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		scriptText.setLayoutData(data);
		scriptText.addFocusListener(listener);

		createLabel(composite, "Script", scriptText, factory);

		runAsText = createText(composite, factory, scriptText);
		createLabel(composite, "Run as", runAsText, factory);
		
		scriptProcessorText = createText(composite, factory, runAsText);
    createLabel(composite, "Script processor", scriptProcessorText, factory);
	}

	@Override
	public void refresh() {
	  scriptText.removeFocusListener(listener);
	  runAsText.removeFocusListener(listener);
	  scriptProcessorText.removeFocusListener(listener);
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = getBusinessObject(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;
			
			ServiceTask scriptTask = (ServiceTask) bo;
			
			String script = getFieldString("script", scriptTask);
			scriptText.setText(script == null ? "" : script);
			String runAs = getFieldString("runAs", scriptTask);
			runAsText.setText(runAs == null ? "" : runAs);
			String scriptProcessor = getFieldString("scriptProcessor", scriptTask);
			scriptProcessorText.setText(scriptProcessor == null ? "" : scriptProcessor);
		}
		scriptText.addFocusListener(listener);
    runAsText.addFocusListener(listener);
    scriptProcessorText.addFocusListener(listener);
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				final Object bo = getBusinessObject(pe);
				if (bo instanceof ServiceTask) {
					updateScriptTaskField((ServiceTask) bo, e.getSource());
				}
			}
		}
	};
	
	protected void updateScriptTaskField(final ServiceTask scriptTask, final Object source) {
    String oldValue = null;
    final String newValue = ((Text) source).getText();
    if (source == scriptText) {
      oldValue = getFieldString("script", scriptTask);
    } else if (source == runAsText) {
      oldValue = getFieldString("runAs", scriptTask);
    } else if (source == scriptProcessorText) {
      oldValue = getFieldString("scriptProcessor", scriptTask);
    }
    
    if ((StringUtils.isEmpty(oldValue) && StringUtils.isNotEmpty(newValue)) || (StringUtils.isNotEmpty(oldValue) && newValue.equals(oldValue) == false)) {
      IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
        
        @Override
        public void execute(IContext context) {
          if (source == scriptText) {
            setFieldString("script", newValue, scriptTask);
          } else if (source == runAsText) {
            setFieldString("runAs", newValue, scriptTask);
          } else if (source == scriptProcessorText) {
            setFieldString("scriptProcessor", newValue, scriptTask);
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
	
	private Text createText(Composite parent, TabbedPropertySheetWidgetFactory factory, Control top) {
    Text text = factory.createText(parent, ""); //$NON-NLS-1$
    FormData data = new FormData();
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, -HSPACE);
    data.top = new FormAttachment(top, VSPACE);
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
