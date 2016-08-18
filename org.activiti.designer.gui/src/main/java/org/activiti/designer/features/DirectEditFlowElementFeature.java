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

import org.activiti.bpmn.model.FlowElement;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.bpmn.BpmnExtensionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class DirectEditFlowElementFeature extends AbstractDirectEditingFeature {
	
	private boolean isMultiLine = false;

	public DirectEditFlowElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	public int getEditingType() {
		if(isMultiLine) {
			return TYPE_MULTILINETEXT;
		} else {
			return TYPE_TEXT;
		}
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		if (bo instanceof FlowElement && ga instanceof MultiText) {
			isMultiLine = true;
			return true;
		} else if (bo instanceof FlowElement && ga instanceof Text) {
			isMultiLine = false;
			return true;
		}
		// direct editing not supported in all other cases
		return false;
	}

	public String getInitialValue(IDirectEditingContext context) {
		// return the current name of the EClass
		PictogramElement pe = context.getPictogramElement();
		FlowElement flowElement = (FlowElement) getBusinessObjectForPictogramElement(pe);
		return BpmnExtensionUtil.getFlowElementName(flowElement, ActivitiPlugin.getDefault());
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		if (value.length() < 1)
			return "Please enter any text."; //$NON-NLS-1$
		if (isMultiLine == false && value.contains("\n")) //$NON-NLS-1$
			return "Line breakes are not allowed."; //$NON-NLS-1$

		// null means, that the value is valid
		return null;
	}

	public void setValue(String value, IDirectEditingContext context) {
		// set the new name for the EClass
		PictogramElement pe = context.getPictogramElement();
		FlowElement flowElement = (FlowElement) getBusinessObjectForPictogramElement(pe);
		BpmnExtensionUtil.setFlowElementName(flowElement, value, ActivitiPlugin.getDefault());

		// Explicitly update the shape to display the new value in the diagram
		// Note, that this might not be necessary in future versions of the GFW
		// (currently in discussion)

		// we know, that pe is the Shape of the Text, so its container is the
		// main shape of the EClass
		updatePictogramElement(((Shape) pe).getContainer());
	}
}
