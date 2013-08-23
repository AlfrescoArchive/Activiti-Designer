package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.SingleColumnFormLayout;
import org.activiti.designer.kickstart.form.util.FormComponentStyles;
import org.activiti.workflow.simple.definition.form.TextPropertyDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public class AddTextInputPropertyFeature extends AbstractAddShapeFeature {

  public AddTextInputPropertyFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context) {
    return context.getNewObject() instanceof TextPropertyDefinition;
  }

  @Override
  public PictogramElement add(IAddContext context) {
    final ContainerShape parent = context.getTargetContainer();
    final TextPropertyDefinition textInput = (TextPropertyDefinition) context.getNewObject();
    
    // CONTAINER SHAPE WITH ROUNDED RECTANGLE
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(parent, true);
    final IGaService gaService = Graphiti.getGaService();

    int width = 0;
    int height = 0;
    GraphicsAlgorithm algorithm = null;

    // check whether the context has a size (e.g. from a create feature)
    // otherwise define a default size for the shape
    width = context.getWidth() <= 0 ? 300 : context.getWidth();
    height = context.getHeight() <= 0 ? 25 : context.getHeight();

    RoundedRectangle rectangle; // need to access it later
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, 0, 0, width, height);

    // create and set visible rectangle inside invisible rectangle
    rectangle = gaService.createRoundedRectangle(invisibleRectangle, 2, 2);
    algorithm = rectangle;
    rectangle.setStyle(FormComponentStyles.getInputFieldStyle(getDiagram()));
    rectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    gaService.setLocationAndSize(rectangle, 0, 0, width, height);

    // create link and wire it
    link(containerShape, textInput);
    
    // Set the correct location based on layout
    ContainerShape layoutParent = getFirstLayoutContainerShape(parent);
    if(layoutParent instanceof Diagram) {
      // TODO: use shared instance for diagram?
      SingleColumnFormLayout layout = new SingleColumnFormLayout();
      layout.moveShape(layoutParent, layoutParent, containerShape, context.getX(), context.getY());
    }
    

    return containerShape;
  }
  
  protected ContainerShape getFirstLayoutContainerShape(ContainerShape containerShape) {
    if(containerShape instanceof Diagram) {
      return containerShape;
    } else if(containerShape.getContainer() != null) {
      // Go one level up the hierarchy to find a container that is able to do layout
      return getFirstLayoutContainerShape(containerShape.getContainer());
    }
    return null;
  }

}
