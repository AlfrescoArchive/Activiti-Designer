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
import org.activiti.designer.kickstart.process.layout.KickstartProcessLayouter;
import org.activiti.workflow.simple.definition.ListConditionStepDefinition;
import org.activiti.workflow.simple.definition.ListStepDefinition;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;

/**
 * Feature that prevent moving shapes to any arbitrary position. Rather, the actual
 * position is calculated by the parent layout. 
 * 
 * @author Tijs Rademakers
 */
public class MoveStepDefinitionFeature extends DefaultMoveShapeFeature implements ICustomUndoableFeature {

  public MoveStepDefinitionFeature(KickstartProcessFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return ! (bo instanceof ListConditionStepDefinition<?>) && ! (bo instanceof ListStepDefinition<?>);
  }

  @Override
  public void moveShape(IMoveShapeContext context) {
    getProcessLayouter().moveShape((KickstartProcessFeatureProvider) getFeatureProvider(), context.getTargetContainer(), 
        context.getSourceContainer(), context.getShape(),
        context.getX(), context.getY(), true);
  }
  
  @Override
  public boolean canRedo(IContext context) {
    return true;
  }
  
  @Override
  public boolean canUndo(IContext context) {
    return true;
  }

  @Override
  public void undo(IContext context) {
    // Since the model is updated by the layout based on the actual shape order,
    // it's sufficient to force a re-layout at this point
    getProcessLayouter().relayout(((IMoveShapeContext)context).getTargetContainer(), (KickstartProcessFeatureProvider) getFeatureProvider());
    getProcessLayouter().relayout(((IMoveShapeContext)context).getSourceContainer(), (KickstartProcessFeatureProvider) getFeatureProvider());
  }
  
  @Override
  public void redo(IContext context) {
    // Since the model is updated by the layout based on the actual shape order,
    // it's sufficient to force a re-layout at this point
    getProcessLayouter().relayout(((IMoveShapeContext)context).getTargetContainer(), (KickstartProcessFeatureProvider) getFeatureProvider());
    getProcessLayouter().relayout(((IMoveShapeContext)context).getSourceContainer(), (KickstartProcessFeatureProvider) getFeatureProvider());
  }
  
  protected KickstartProcessLayouter getProcessLayouter() {
    return ((KickstartProcessFeatureProvider)getFeatureProvider()).getProcessLayouter();
  }

}
