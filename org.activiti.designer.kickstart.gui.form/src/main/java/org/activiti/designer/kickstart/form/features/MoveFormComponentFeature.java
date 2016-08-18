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
package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.diagram.layout.KickstartFormLayouter;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;

/**
 * Feature that prevent moving shapes to any arbitrary position. Rather, the actual
 * position is calculated by the parent layout. 
 * 
 * @author Frederik Heremans
 */
public class MoveFormComponentFeature extends DefaultMoveShapeFeature implements ICustomUndoableFeature {

  public MoveFormComponentFeature(KickstartFormFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    return true;
  }

  @Override
  public void moveShape(IMoveShapeContext context) {
      getFormLayouter().moveShape(
          context.getTargetContainer(), context.getSourceContainer(), context.getShape(),
          context.getX(), context.getY());
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
    getFormLayouter().relayout(((IMoveShapeContext)context).getTargetContainer());
    getFormLayouter().relayout(((IMoveShapeContext)context).getSourceContainer());
  }
  
  @Override
  public void redo(IContext context) {
    // Since the model is updated by the layout based on the actual shape order,
    // it's sufficient to force a re-layout at this point
    getFormLayouter().relayout(((IMoveShapeContext)context).getTargetContainer());
    getFormLayouter().relayout(((IMoveShapeContext)context).getSourceContainer());
  }
  
  protected KickstartFormLayouter getFormLayouter() {
    return ((KickstartFormFeatureProvider)getFeatureProvider()).getFormLayouter(); 
  }

}
