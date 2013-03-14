package org.activiti.designer.property;

import java.util.Arrays;
import java.util.List;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.SignalEventDefinition;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyBoundarySignalSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private CCombo cancelActivityCombo;
	private List<String> cancelFormats = Arrays.asList("true", "false");
	private Text signalText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;
		
		cancelActivityCombo = factory.createCCombo(composite, SWT.NONE);
		cancelActivityCombo.setItems((String[]) cancelFormats.toArray());
		data = new FormData();
		data.left = new FormAttachment(0, 160);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		cancelActivityCombo.setLayoutData(data);
		cancelActivityCombo.addFocusListener(listener);
		
		createLabel(composite, "Cancel activity", cancelActivityCombo, factory); //$NON-NLS-1$

		signalText = getWidgetFactory().createText(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, -HSPACE);
    data.top = new FormAttachment(cancelActivityCombo, VSPACE);
    signalText.setLayoutData(data);
    signalText.addFocusListener(listener);

		createLabel(composite, "Signal ref", signalText, factory); //$NON-NLS-1$
	}

	@Override
	public void refresh() {
		cancelActivityCombo.removeFocusListener(listener);
		signalText.removeFocusListener(listener);

		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = getBusinessObject(pe);
			if (bo == null)
				return;
			
			final Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
	    if (model == null) {
	      return;
	    }
			
			boolean cancelActivity = ((BoundaryEvent) bo).isCancelActivity();
			if(cancelActivity == false) {
				cancelActivityCombo.select(1);
			} else {
				cancelActivityCombo.select(0);
			}
			
			
			if(bo instanceof BoundaryEvent) {
  			BoundaryEvent boundaryEvent = (BoundaryEvent) bo;
  			if(boundaryEvent.getEventDefinitions().get(0) != null) {
  			  SignalEventDefinition signalDefinition = (SignalEventDefinition) boundaryEvent.getEventDefinitions().get(0);
          if(StringUtils.isNotEmpty(signalDefinition.getSignalRef())) {
          	signalText.setText(signalDefinition.getSignalRef());
          }
  			}
			}
		}
		cancelActivityCombo.addFocusListener(listener);
		signalText.addFocusListener(listener);
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			final Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
	    if (model == null) {
	      return;
	    }
	    
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				final Object bo = getBusinessObject(pe);
				if (bo instanceof BoundaryEvent) {
				  updateBoundaryEvent((BoundaryEvent) bo, e.getSource());
				}
			}
		}
	};
	
	protected void updateBoundaryEvent(final BoundaryEvent boundaryEvent, final Object source) {
    final SignalEventDefinition signalDefinition = (SignalEventDefinition) boundaryEvent.getEventDefinitions().get(0);
    String oldValue = null;
    String tempNewValue = null;
    if (source == cancelActivityCombo) {
      oldValue = "" + boundaryEvent.isCancelActivity();
      tempNewValue = ((CCombo) source).getText();
    } else if (source == signalText) {
      oldValue = signalDefinition.getSignalRef();
      tempNewValue = ((Text) source).getText();
    }
    
    final String newValue = tempNewValue;
    
    if ((StringUtils.isEmpty(oldValue) && StringUtils.isNotEmpty(newValue)) || (StringUtils.isNotEmpty(oldValue) && newValue.equals(oldValue) == false)) {
      IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
        
        @Override
        public void execute(IContext context) {
          if (source == cancelActivityCombo) {
            if ("true".equalsIgnoreCase(newValue)) {
              boundaryEvent.setCancelActivity(true);
            } else {
              boundaryEvent.setCancelActivity(false);
            }
          } else if (source == signalText) {
            signalDefinition.setSignalRef(newValue);
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
