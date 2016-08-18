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
package org.activiti.designer.kickstart.process.features;

import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.designer.kickstart.process.diagram.shape.BusinessObjectShapeController;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

/**
 * Update feature that uses corresponding {@link BusinessObjectShapeController} to update
 * a shape for a business object.
 * 
 * @author Tijs Rademakers
 */
public class UpdateStepDefinitionFeature extends AbstractUpdateFeature {

  public UpdateStepDefinitionFeature(KickstartProcessFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return bo instanceof StepDefinition && ((KickstartProcessFeatureProvider)getFeatureProvider()).hasShapeController(bo);
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    if (context.getPictogramElement() instanceof ContainerShape) {
      Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
      // Ignore shapes without a business-object
      if(bo != null) {
        BusinessObjectShapeController controller = ((KickstartProcessFeatureProvider)getFeatureProvider()).getShapeController(bo);
        if(controller.isShapeUpdateNeeded((ContainerShape) context.getPictogramElement(), bo)) {
          return Reason.createTrueReason();
        }
      }
    }
    return Reason.createFalseReason();
  }

  @Override
  public boolean update(IUpdateContext context) {
    if (context.getPictogramElement() instanceof ContainerShape) {
      Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
      
      // Use shape-controller to perform update
      BusinessObjectShapeController controller = ((KickstartProcessFeatureProvider)getFeatureProvider()).getShapeController(bo);
      controller.updateShape((ContainerShape) context.getPictogramElement(), bo, -1, -1);
      return true;
    }
    return false;
  }
}
