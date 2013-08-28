package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.diagram.KickstartFormLayouter;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Base feature to extend for adding a new form-component to the diagram.
 * 
 * @author Frederik Heremans
 */
public abstract class AbstractAddFormComponentFeature extends AbstractAddShapeFeature {

  public AbstractAddFormComponentFeature(KickstartFormFeatureProvider fp) {
    super(fp);
  }

  @Override
  public PictogramElement add(IAddContext context) {
    final ContainerShape parent = context.getTargetContainer();

    // Get actual shape from subclass
    final ContainerShape containerShape = createContainerShape(context.getNewObject(), parent, -1, -1);

    // Since new shape has already been added to the parent, trigger a move to have it
    // positioned based on the layout implementation, taking into account X and Y from add-context
    getFormLayouter().moveShape(parent, parent, containerShape, context.getX(), context.getY());
    
    return containerShape;
  }

  /**
   * @return the shape representing the added form-component. After the shape is created, it will be positioned in the
   *         parent, no need to add position details in this method.
   */
  protected abstract ContainerShape createContainerShape(Object newObject, ContainerShape layoutParent, int width,
      int height);

  protected KickstartFormLayouter getFormLayouter() {
    return ((KickstartFormFeatureProvider) getFeatureProvider()).getFormLayouter();
  }

}
