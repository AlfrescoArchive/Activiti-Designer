package org.activiti.designer.property;

import java.util.Arrays;
import java.util.List;

import org.activiti.bpmn.model.ScriptTask;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
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

public class PropertyScriptTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private CCombo scriptFormatCombo;
	private List<String> scriptFormats = Arrays.asList("javascript", "groovy");
	private Text scriptText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		scriptFormatCombo = factory.createCCombo(composite, SWT.NONE);
		scriptFormatCombo.setItems((String[]) scriptFormats.toArray());
		data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		scriptFormatCombo.setLayoutData(data);
		scriptFormatCombo.addFocusListener(listener);

		CLabel languageLabel = factory.createCLabel(composite, "Script Language:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(scriptFormatCombo, -HSPACE);
		data.top = new FormAttachment(scriptFormatCombo, 0, SWT.CENTER);
		languageLabel.setLayoutData(data);

		scriptText = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
		data = new FormData(SWT.DEFAULT, 100);
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(scriptFormatCombo, VSPACE);
		scriptText.setLayoutData(data);
		scriptText.addFocusListener(listener);

		CLabel scriptLabel = factory.createCLabel(composite, "Script:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(scriptText, -HSPACE);
		data.top = new FormAttachment(scriptText, 0, SWT.TOP);
		scriptLabel.setLayoutData(data);

	}

	@Override
	public void refresh() {
		scriptFormatCombo.removeFocusListener(listener);

		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = getBusinessObject(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;

			String scriptFormat = ((ScriptTask) bo).getScriptFormat();
			int scriptIndex = scriptFormats.indexOf(scriptFormat);
			scriptFormatCombo.select(scriptIndex == -1 ? 0 : scriptIndex);
			scriptFormatCombo.addFocusListener(listener);

			String script = ((ScriptTask) bo).getScript();
			scriptText.setText(script == null ? "" : script);
		}
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				final Object bo = getBusinessObject(pe);
				if (bo instanceof ScriptTask) {
				   updateScriptTaskField((ScriptTask) bo, e.getSource());
				}
			}
		}
	};
	
	protected void updateScriptTaskField(final ScriptTask scriptTask, final Object source) {
    String oldValue = null;
    String tempNewValue = null;
    if (source == scriptFormatCombo) {
      oldValue = scriptTask.getScriptFormat();
      tempNewValue = ((CCombo) source).getText();
    } else if (source == scriptText) {
      oldValue = scriptTask.getScript();
      tempNewValue = ((Text) source).getText();
    }
    
    final String newValue = tempNewValue;
    
    if ((StringUtils.isEmpty(oldValue) && StringUtils.isNotEmpty(newValue)) || (StringUtils.isNotEmpty(oldValue) && newValue.equals(oldValue) == false)) {
      IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
        
        @Override
        public void execute(IContext context) {
          if (source == scriptFormatCombo) {
            scriptTask.setScriptFormat(newValue);
          } else if (source == scriptText) {
            scriptTask.setScript(newValue);
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
