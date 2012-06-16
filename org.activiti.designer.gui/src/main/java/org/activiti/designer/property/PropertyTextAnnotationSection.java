package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.TextAnnotation;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.platform.IDiagramEditor;
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

public class PropertyTextAnnotationSection extends ActivitiPropertySection
		implements ITabbedPropertyConstants {

	private Text text;

	private FocusListener listener = new FocusListener() {

		@Override
		public void focusGained(FocusEvent e) {
			// intentionally left blank
		}

		@Override
		public void focusLost(FocusEvent e) {
			final PictogramElement pe = getSelectedPictogramElement();
			
			if (pe != null) {
				final Object bo = getBusinessObject(pe);
				
				if (bo instanceof TextAnnotation) {
					final IDiagramEditor diagramEditor = getDiagramEditor();
					final TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
					
					ActivitiUiUtil.runModelChange(new Runnable() {

						@Override
						public void run() {
							if (!(bo instanceof TextAnnotation)) {
								return;
							}
							
							final TextAnnotation ta = (TextAnnotation) bo;
							final String annotationText = text.getText();
							
							ta.setText(annotationText);
							
							final UpdateContext updateContext = new UpdateContext(pe);
							final IUpdateFeature updateFeature = getFeatureProvider(pe).getUpdateFeature(updateContext);
							if (updateFeature != null) {
								updateFeature.update(updateContext);
							}
							
							if (pe instanceof ContainerShape) {
								final ContainerShape cs = (ContainerShape) pe;
								
								for (final Shape shape : cs.getChildren()) {
									final GraphicsAlgorithm graphicsAlgorithm = shape.getGraphicsAlgorithm();
									if (graphicsAlgorithm instanceof AbstractText) {
										final AbstractText textShape = (AbstractText) graphicsAlgorithm;
										
										textShape.setValue(annotationText);
									}
								}
							}
						}
						
					}, editingDomain, "Model Update");
				}
			}
		}
		
	};
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		final TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		final Composite composite = factory.createFlatFormComposite(parent);
		
		text = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		
		FormData data = new FormData(SWT.DEFAULT, 100);
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		text.setLayoutData(data);
		text.addFocusListener(listener);
		
		CLabel textLabel = factory.createCLabel(composite, "Text:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(text, -HSPACE);
		data.top = new FormAttachment(text, 0, SWT.TOP);
		textLabel.setLayoutData(data);
	}

	@Override
	public void refresh() {
		final PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			final Object bo = getBusinessObject(pe);
			
			if (!(bo instanceof TextAnnotation)) {
				return;
			}
			
			final TextAnnotation ta = (TextAnnotation) bo;
			String annotationText = ta.getText();
			if (annotationText != null) {
				text.removeFocusListener(listener);
				text.setText(annotationText);
				text.addFocusListener(listener);
			} else {
				text.setText("");
			}
		}
	}
}
