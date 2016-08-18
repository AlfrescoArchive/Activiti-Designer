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

import org.activiti.designer.controller.BusinessObjectShapeController;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Base feature to extend for adding a new BPMN element to the diagram.
 * 
 * @author Tijs Rademakers
 */
public class AddBaseElementFeature extends AbstractAddShapeFeature {

  public AddBaseElementFeature(ActivitiBPMNFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context) {
    return getBpmnFeatureProvider().hasShapeController(context.getNewObject());
  }
  
  @Override
  public PictogramElement add(IAddContext context) {
    final ContainerShape parent = context.getTargetContainer();
    
    // Get the controller, capable of creating a new shape for the business-object
    BusinessObjectShapeController shapeController = getBpmnFeatureProvider()
        .getShapeController(context.getNewObject());
   
    // Request a new shape from the controller
    final PictogramElement containerShape = shapeController.createShape(context.getNewObject(), 
        parent, context.getWidth(), context.getHeight(), context);
        
    // Create link between shape and business object
    link(containerShape, context.getNewObject());
    
    layoutPictogramElement(containerShape);
    
    return containerShape;
  }

  protected ActivitiBPMNFeatureProvider getBpmnFeatureProvider() {
    return (ActivitiBPMNFeatureProvider) getFeatureProvider();
  }
}
