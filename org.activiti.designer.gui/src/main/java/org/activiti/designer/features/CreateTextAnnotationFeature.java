package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.TextAnnotation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateTextAnnotationFeature extends AbstractCreateFastBPMNFeature {

	public static final String FEATURE_ID_KEY = "textannotation";
	
	public CreateTextAnnotationFeature(final IFeatureProvider fp) {
		super(fp, "Annotation", "Provides additional textual information");
	}
	
	@Override
	public Object[] create(ICreateContext context) {
		final TextAnnotation ta = new TextAnnotation();
		
		ta.setText("Enter your text here");
			
		addObjectToContainer(context, ta);
		
		return new Object[] { ta };
	}

	public String getCreateImageId() {
		return PluginImage.IMG_TEXT_ANNOTATION.getImageKey();
	}
	
	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

}
