package org.activiti.designer.features;

import java.util.List;

import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.Task;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.integration.servicetask.DiagramBaseShape;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public abstract class AddTaskFeature extends AbstractAddShapeFeature {

  private static final int IMAGE_SIZE = 16;

  public AddTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public PictogramElement add(IAddContext context) {
    final Task addedTask = (Task) context.getNewObject();
    final ContainerShape parent = context.getTargetContainer();

    // CONTAINER SHAPE WITH ROUNDED RECTANGLE
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(parent, true);
    final IGaService gaService = Graphiti.getGaService();

    DiagramBaseShape baseShape = DiagramBaseShape.ACTIVITY;

    if (ExtensionUtil.isCustomServiceTask(addedTask)) {
      final ServiceTask serviceTask = (ServiceTask) addedTask;
      final List<CustomServiceTask> customServiceTasks = ExtensionUtil.getCustomServiceTasks(ActivitiUiUtil.getProjectFromDiagram(getDiagram()));

      CustomServiceTask targetTask = null;

      for (final CustomServiceTask customServiceTask : customServiceTasks) {
        if (customServiceTask.getId().equals(ExtensionUtil.getCustomServiceTaskId(serviceTask))) {
          targetTask = customServiceTask;
          break;
        }
      }

      if (!DiagramBaseShape.ACTIVITY.equals(targetTask.getDiagramBaseShape())) {
        baseShape = targetTask.getDiagramBaseShape();
      }
    }

    int width = 0;
    int height = 0;
    GraphicsAlgorithm algorithm = null;

    switch (baseShape) {
    case ACTIVITY:
      // check whether the context has a size (e.g. from a create feature)
      // otherwise define a default size for the shape
      width = context.getWidth() <= 0 ? 105 : context.getWidth();
      height = context.getHeight() <= 0 ? 55 : context.getHeight();

      RoundedRectangle roundedRectangle; // need to access it later
      {
        // create invisible outer rectangle expanded by
        // the width needed for the anchor
        final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
        gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(), width, height);

        // create and set visible rectangle inside invisible rectangle
        roundedRectangle = gaService.createRoundedRectangle(invisibleRectangle, 20, 20);
        algorithm = roundedRectangle;
        roundedRectangle.setParentGraphicsAlgorithm(invisibleRectangle);
        roundedRectangle.setStyle(StyleUtil.getStyleForTask(getDiagram()));
        gaService.setLocationAndSize(roundedRectangle, 0, 0, width, height);

        // create link and wire it
        link(containerShape, addedTask);
      }
      break;
    case GATEWAY:
      // check whether the context has a size (e.g. from a create feature)
      // otherwise define a default size for the shape
      width = context.getWidth() <= 0 ? 60 : context.getWidth();
      height = context.getHeight() <= 0 ? 60 : context.getHeight();

      Polygon polygon;
      {
        int xy[] = new int[] { 0, 30, 30, 0, 60, 30, 30, 60, 0, 30 };

        final Polygon invisiblePolygon = gaService.createPolygon(containerShape, xy);
        invisiblePolygon.setFilled(false);
        invisiblePolygon.setLineVisible(false);
        gaService.setLocationAndSize(invisiblePolygon, context.getX(), context.getY(), width, height);

        // create and set visible circle inside invisible circle
        polygon = gaService.createPolygon(invisiblePolygon, xy);
        algorithm = polygon;
        polygon.setParentGraphicsAlgorithm(invisiblePolygon);
        polygon.setStyle(StyleUtil.getStyleForTask(getDiagram()));
        gaService.setLocationAndSize(polygon, 0, 0, width, height);

        // create link and wire it
        link(containerShape, addedTask);
      }
      break;
    case EVENT:
      // check whether the context has a size (e.g. from a create feature)
      // otherwise define a default size for the shape
      width = context.getWidth() <= 0 ? 55 : context.getWidth();
      height = context.getHeight() <= 0 ? 55 : context.getHeight();

      Ellipse circle;
      {
        final Ellipse invisibleCircle = gaService.createEllipse(containerShape);
        invisibleCircle.setFilled(false);
        invisibleCircle.setLineVisible(false);
        gaService.setLocationAndSize(invisibleCircle, context.getX(), context.getY(), width, height);

        // create and set visible circle inside invisible circle
        circle = gaService.createEllipse(invisibleCircle);
        circle.setParentGraphicsAlgorithm(invisibleCircle);
        circle.setStyle(StyleUtil.getStyleForTask(getDiagram()));
        gaService.setLocationAndSize(circle, 0, 0, width, height);

        // create link and wire it
        link(containerShape, addedTask);
      }
      break;
    }

    // SHAPE WITH TEXT
    {
      // create shape for text
      final Shape shape = peCreateService.createShape(containerShape, false);

      // create and set text graphics algorithm
      final MultiText text = gaService.createDefaultMultiText(getDiagram(), shape, addedTask.getName());
      text.setStyle(StyleUtil.getStyleForTask(getDiagram()));
      text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
      text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
      if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
        text.setFont(gaService.manageFont(getDiagram(), text.getFont().getName(), 11));
      }

      switch (baseShape) {
      case ACTIVITY:
        gaService.setLocationAndSize(text, 0, 20, width, 30);
        break;
      case GATEWAY:
        gaService.setLocationAndSize(text, 0, height + 5, width, 40);
        break;
      case EVENT:
        gaService.setLocationAndSize(text, 0, height + 5, width, 40);
        break;
      }

      // create link and wire it
      link(shape, addedTask);

      // provide information to support direct-editing directly
      // after object creation (must be activated additionally)
      final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
      // set container shape for direct editing after object creation
      directEditingInfo.setMainPictogramElement(containerShape);
      // set shape and graphics algorithm where the editor for
      // direct editing shall be opened after object creation
      directEditingInfo.setPictogramElement(shape);
      directEditingInfo.setGraphicsAlgorithm(text);
    }

    {
      final Shape shape = peCreateService.createShape(containerShape, false);
      final Image image = gaService.createImage(shape, getIcon(addedTask));

      switch (baseShape) {
      case ACTIVITY:
        gaService.setLocationAndSize(image, 5, 5, IMAGE_SIZE, IMAGE_SIZE);
        break;
      case GATEWAY:
        gaService.setLocationAndSize(image, (width - IMAGE_SIZE) / 2, (height - IMAGE_SIZE) / 2, IMAGE_SIZE, IMAGE_SIZE);
        break;
      case EVENT:
        gaService.setLocationAndSize(image, (width - IMAGE_SIZE) / 2, (height - IMAGE_SIZE) / 2, IMAGE_SIZE, IMAGE_SIZE);
        break;
      }

    }

    // add a chopbox anchor to the shape
    peCreateService.createChopboxAnchor(containerShape);

    // create an additional box relative anchor at middle-right
    final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
    boxAnchor.setRelativeWidth(1.0);
    boxAnchor.setRelativeHeight(0.51);
    boxAnchor.setReferencedGraphicsAlgorithm(algorithm);
    final Ellipse ellipse = ActivitiUiUtil.createInvisibleEllipse(boxAnchor, gaService);
    gaService.setLocationAndSize(ellipse, 0, 0, 0, 0);
    layoutPictogramElement(containerShape);

    return containerShape;
  }

  @Override
  public boolean canAdd(IAddContext context) {
    if (context.getNewObject() instanceof Task) {
      
      Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
      
      if (context.getTargetContainer() instanceof Diagram || 
              parentObject instanceof SubProcess || parentObject instanceof Lane) {
        
        return true;
      }
    }
    return false;
  }

  protected abstract String getIcon(Object bo);

}
