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
package org.activiti.designer.kickstart.form.property;

import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

/**
 * Abstract base-class for all property section filters, exposing the business-object
 * used for the current diagram.
 * 
 * @author Frederik heremans
 */
public abstract class AbstractKickstartFormPropertyFilter extends AbstractPropertySectionFilter {

  protected Object getBusinessObject(PictogramElement element) {
    if (element == null)
      return null;
    Diagram diagram = getContainer(element);
    if(diagram != null) {
     KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(diagram)));
      if (model != null) {
        return model.getFeatureProvider().getBusinessObjectForPictogramElement(element);
      }
    }
    return null;
  }

  private Diagram getContainer(EObject container) {
    if (container == null) {
      return null;
    }
    if (container instanceof Diagram) {
      return (Diagram) container;
    } else {
      return getContainer(container.eContainer());
    }
  }
}
