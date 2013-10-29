package org.activiti.designer.property;

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.designer.util.TextUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
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

public class PropertySequenceFlowSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	
  protected Text flowLabelWidthText;
  protected Text conditionExpressionText;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		flowLabelWidthText = getWidgetFactory().createText(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, -HSPACE);
    data.top = new FormAttachment(0, VSPACE);
    flowLabelWidthText.setLayoutData(data);
    flowLabelWidthText.addFocusListener(listener);
    CLabel widthLabel = factory.createCLabel(composite, "Label width (50-500)"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(flowLabelWidthText, -HSPACE);
    data.top = new FormAttachment(flowLabelWidthText, 0, SWT.TOP);
    widthLabel.setLayoutData(data);
		
		conditionExpressionText = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
		data = new FormData(SWT.DEFAULT, 100);
		data.left = new FormAttachment(0, 160);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(flowLabelWidthText, VSPACE);
		conditionExpressionText.setLayoutData(data);
		conditionExpressionText.addFocusListener(listener);

		CLabel scriptLabel = factory.createCLabel(composite, "Condition:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(conditionExpressionText, -HSPACE);
		data.top = new FormAttachment(conditionExpressionText, 0, SWT.TOP);
		scriptLabel.setLayoutData(data);

	}

	@Override
	public void refresh() {
	  flowLabelWidthText.removeFocusListener(listener);
	  conditionExpressionText.removeFocusListener(listener);
	  
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = getBusinessObject(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;

			SequenceFlow sequenceFlow = ((SequenceFlow) bo);
			
      EList<ConnectionDecorator> decoratorList = ((FreeFormConnection) getSelectedPictogramElement()).getConnectionDecorators();
      for (ConnectionDecorator decorator : decoratorList) {
        if (decorator.getGraphicsAlgorithm() instanceof org.eclipse.graphiti.mm.algorithms.MultiText) {
          org.eclipse.graphiti.mm.algorithms.MultiText text = (org.eclipse.graphiti.mm.algorithms.MultiText) decorator.getGraphicsAlgorithm();
          flowLabelWidthText.setText("" + text.getWidth());
          break;
        }
      }
			
			if(sequenceFlow.getConditionExpression() != null) {
				
				conditionExpressionText.removeFocusListener(listener);
				String condition = sequenceFlow.getConditionExpression();
				conditionExpressionText.setText(condition);
				conditionExpressionText.addFocusListener(listener);
			} else {
				conditionExpressionText.setText("");
			}
		}
		
		flowLabelWidthText.addFocusListener(listener);
    conditionExpressionText.addFocusListener(listener);
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				final Object bo = getBusinessObject(pe);
				if (bo instanceof SequenceFlow) {
					updateSequenceFlowField((SequenceFlow) bo, e.getSource());
				}
			}
		}
	};
	
	protected void updateSequenceFlowField(final SequenceFlow flow, final Object source) {
	  if (source == conditionExpressionText) {
      String oldValue = flow.getConditionExpression();
      final String newValue = conditionExpressionText.getText();
      
      if ((StringUtils.isEmpty(oldValue) && StringUtils.isNotEmpty(newValue)) || (StringUtils.isNotEmpty(oldValue) && newValue.equals(oldValue) == false)) {
        IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
          
          @Override
          public void execute(IContext context) {
            flow.setConditionExpression(newValue);
          }
          
          @Override
          public boolean canExecute(IContext context) {
            return true;
          }
        };
        CustomContext context = new CustomContext();
        execute(feature, context);
      }
	  } else if (source == flowLabelWidthText) {
	    if (!(getSelectedPictogramElement() instanceof FreeFormConnection)) {
        return;
      }
      EList<ConnectionDecorator> decoratorList = ((FreeFormConnection) getSelectedPictogramElement()).getConnectionDecorators();
      for (ConnectionDecorator decorator : decoratorList) {
        if (decorator.getGraphicsAlgorithm() instanceof org.eclipse.graphiti.mm.algorithms.MultiText) {
          final org.eclipse.graphiti.mm.algorithms.MultiText text = (org.eclipse.graphiti.mm.algorithms.MultiText) decorator.getGraphicsAlgorithm();
          final String widthText = flowLabelWidthText.getText();
          
          IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
            
            @Override
            public void execute(IContext context) {
              if (NumberUtils.isNumber(widthText)) {
                long width = Long.valueOf(widthText);
                if (width >= 50 || width <= 500) {
                  TextUtil.setTextSize((int) width, text);
                }
              }
              flowLabelWidthText.setText("" + text.getWidth());
            }
            
            @Override
            public boolean canExecute(IContext context) {
              return true;
            }
          };
          CustomContext context = new CustomContext();
          execute(feature, context);
          
          break;
        }
      }
	  }
  }

}
