package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public class AddBoundaryTimerFeature extends AbstractAddShapeFeature {
  
  private static final int IMAGE_SIZE = 30;
	
	public AddBoundaryTimerFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
  public PictogramElement add(IAddContext context) {
    final BoundaryEvent addedEvent = (BoundaryEvent) context.getNewObject();
    ContainerShape parent = context.getTargetContainer();
    int x = context.getX();
    int y = context.getY();
    
    Object parentObject = getBusinessObjectForPictogramElement(parent);
    if (parentObject instanceof SubProcess) {
      if (addedEvent.eResource() == null) {
        getDiagram().eResource().getContents().add(addedEvent);
      }
      
      x += parent.getGraphicsAlgorithm().getX();
      y += parent.getGraphicsAlgorithm().getY();
      
      parent = getDiagram();
      
    } else if(parent.getContainer() != null && 
            parent.getContainer() instanceof Diagram == false) {
      
      x += parent.getGraphicsAlgorithm().getX();
      y += parent.getGraphicsAlgorithm().getY();
      
      Object containerObject = getBusinessObjectForPictogramElement(parent.getContainer());
      if (containerObject instanceof SubProcess) {
        parent = parent.getContainer();
      }
      
      if (addedEvent.eResource() == null) {
        ((SubProcess) parentObject).getFlowElements().add(addedEvent);
      }
      
    } else {
      
      x += parent.getGraphicsAlgorithm().getX();
      y += parent.getGraphicsAlgorithm().getY();
      
      parent = getDiagram();
      
      if (addedEvent.eResource() == null) {
        getDiagram().eResource().getContents().add(addedEvent);
      }
    }

    // CONTAINER SHAPE WITH CIRCLE
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(parent, true);

    // check whether the context has a size (e.g. from a create feature)
    // otherwise define a default size for the shape
    final int width = context.getWidth() <= 0 ? IMAGE_SIZE : context.getWidth();
    final int height = context.getHeight() <= 0 ? IMAGE_SIZE : context.getHeight();

    final IGaService gaService = Graphiti.getGaService();

    Ellipse circle;
    {
      final Ellipse invisibleCircle = gaService.createEllipse(containerShape);
      invisibleCircle.setFilled(false);
      invisibleCircle.setLineVisible(false);
      gaService.setLocationAndSize(invisibleCircle, x, y, width, height);

      // create and set visible circle inside invisible circle
      circle = gaService.createEllipse(invisibleCircle);
      circle.setParentGraphicsAlgorithm(invisibleCircle);
      circle.setStyle(StyleUtil.getStyleForEvent(getDiagram()));
      gaService.setLocationAndSize(circle, 0, 0, width, height);

      // create link and wire it
      link(containerShape, addedEvent);
    }
    
    {
      Ellipse secondCircle = gaService.createEllipse(circle);
      secondCircle.setParentGraphicsAlgorithm(circle);
      secondCircle.setStyle(StyleUtil.getStyleForEvent(getDiagram()));
      gaService.setLocationAndSize(secondCircle, 3, 3, width - 6, height - 6);
    }

    {
      final Shape shape = peCreateService.createShape(containerShape, false);
      final Image image = gaService.createImage(shape, ActivitiImageProvider.IMG_BOUNDARY_TIMER);
      image.setStretchH(true);
      image.setStretchV(true);
      image.setWidth(IMAGE_SIZE);
      image.setHeight(IMAGE_SIZE);
      
      gaService.setLocationAndSize(image, (width - IMAGE_SIZE) / 2, (height - IMAGE_SIZE) / 2, IMAGE_SIZE, IMAGE_SIZE);
    }
    
    // add a chopbox anchor to the shape
    peCreateService.createChopboxAnchor(containerShape);
    layoutPictogramElement(containerShape);

    return containerShape;
  }

  @Override
  public boolean canAdd(IAddContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess == false && parentObject instanceof Task == false) {
      return false;
    }
    if (context.getNewObject() instanceof BoundaryEvent == false) {
      return false;
    }
    return true;
  }
}