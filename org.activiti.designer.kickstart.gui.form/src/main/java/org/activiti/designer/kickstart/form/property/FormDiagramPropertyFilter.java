package org.activiti.designer.kickstart.form.property;

import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class FormDiagramPropertyFilter extends AbstractPropertySectionFilter {

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    return pictogramElement instanceof Diagram;
  }

}
