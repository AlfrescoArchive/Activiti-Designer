package org.activiti.designer.kickstart.process.property;

import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
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
public abstract class AbstractKickstartProcessPropertyFilter extends AbstractPropertySectionFilter {

  protected Object getBusinessObject(PictogramElement element) {
    if (element == null)
      return null;
    Diagram diagram = getContainer(element);
    if(diagram != null) {
     KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(diagram)));
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
