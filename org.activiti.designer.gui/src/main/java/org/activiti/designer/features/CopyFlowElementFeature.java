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
/**
 * 
 */
package org.activiti.designer.features;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;

/**
 * Copy feature for flow elements.
 * 
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 * 
 */
public class CopyFlowElementFeature extends AbstractCopyFeature {

  public CopyFlowElementFeature(IFeatureProvider fp) {
    super(fp);
  }

  public boolean canCopy(ICopyContext context) {
    final PictogramElement[] pes = context.getPictogramElements();
    if (pes == null || pes.length == 0) { // nothing selected
      return false;
    }

    // return true, if all selected elements are a Flow elements
    for (PictogramElement pe : pes) {

      final Object bo = getBusinessObjectForPictogramElement(pe);
      if (!(bo instanceof FlowElement)) {
        return false;
      }
    }
    return true;
  }

  public void copy(ICopyContext context) {

    // get the business objects for all pictogram elements
    // we already verified that all business objects are FlowElements
    PictogramElement[] pes = context.getPictogramElements();
    List<FlowElement> copyList = new ArrayList<FlowElement>();
    for (int i = 0; i < pes.length; i++) {

      PictogramElement pe = pes[i];
      copyList.add((FlowElement) getBusinessObjectForPictogramElement(pe));
    }
    // put all business objects to our own clipboard (default one only supports EObjects
    ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).setClipboard(copyList);
  }
}
