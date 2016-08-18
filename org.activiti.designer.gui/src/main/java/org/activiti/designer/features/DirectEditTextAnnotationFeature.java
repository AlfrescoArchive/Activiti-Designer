/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.features;

import org.activiti.bpmn.model.TextAnnotation;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.bpmn.BpmnExtensionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class DirectEditTextAnnotationFeature extends
		AbstractDirectEditingFeature {

	public DirectEditTextAnnotationFeature(final IFeatureProvider fp) {
	    super(fp);
    }
	
	@Override
	public int getEditingType() {
		return TYPE_MULTILINETEXT;
	}

	@Override
	public String getInitialValue(final IDirectEditingContext context) {
		
		final PictogramElement pe = context.getPictogramElement();
		final TextAnnotation annotation = (TextAnnotation) getBusinessObjectForPictogramElement(pe);	
		
		return BpmnExtensionUtil.getTextAnnotationText(annotation, ActivitiPlugin.getDefault());
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		final PictogramElement pe = context.getPictogramElement();
		final Object bo = getBusinessObjectForPictogramElement(pe);
		final GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
	
		return bo instanceof TextAnnotation && ga instanceof MultiText;
	}

	@Override
	public void setValue(String value, IDirectEditingContext context) {
		final PictogramElement pe = context.getPictogramElement();
		final TextAnnotation annotation = (TextAnnotation) getBusinessObjectForPictogramElement(pe);
		
		BpmnExtensionUtil.setTextAnnotationText(annotation, value, ActivitiPlugin.getDefault());
		updatePictogramElement(((Shape) pe).getContainer());
	}
}
