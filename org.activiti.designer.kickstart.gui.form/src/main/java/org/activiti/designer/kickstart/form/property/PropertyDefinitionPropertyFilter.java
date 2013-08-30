package org.activiti.designer.kickstart.form.property;

import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyDefinitionPropertyFilter extends AbstractPropertySectionFilter {

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    Object bo = getBusinessObject(pictogramElement);
    return bo instanceof FormPropertyDefinition;
  }

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
