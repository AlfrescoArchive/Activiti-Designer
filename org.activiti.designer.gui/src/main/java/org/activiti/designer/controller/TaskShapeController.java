package org.activiti.designer.controller;

import java.util.List;

import org.activiti.bpmn.model.BusinessRuleTask;
import org.activiti.bpmn.model.ManualTask;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.ReceiveTask;
import org.activiti.bpmn.model.ScriptTask;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.UserTask;
import org.activiti.bpmn.model.alfresco.AlfrescoMailTask;
import org.activiti.bpmn.model.alfresco.AlfrescoScriptTask;
import org.activiti.bpmn.model.alfresco.AlfrescoUserTask;
import org.activiti.designer.PluginImage;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.integration.servicetask.DiagramBaseShape;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.context.IAddContext;
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

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for {@link Task} objects.
 *  
 * @author Tijs Rademakers
 */
public class TaskShapeController extends AbstractBusinessObjectShapeController {
  
  public static final int IMAGE_SIZE = 16;
  public static final int MI_IMAGE_SIZE = 12;
  
  public TaskShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof AlfrescoUserTask || businessObject instanceof AlfrescoMailTask || businessObject instanceof AlfrescoScriptTask) {
      return false;
    } else if (businessObject instanceof ServiceTask) {
      ServiceTask serviceTask = (ServiceTask) businessObject;
      if (AlfrescoScriptTask.ALFRESCO_SCRIPT_DELEGATE.equalsIgnoreCase(serviceTask.getImplementation())) {
        return false;
      } else {
        return true;
      }
      
    } else if (businessObject instanceof UserTask || businessObject instanceof BusinessRuleTask || businessObject instanceof ManualTask ||
            businessObject instanceof ReceiveTask || businessObject instanceof ScriptTask) {
      
      return true;
    } else {
      return false;
    }
  }

  @Override
  public PictogramElement createShape(Object businessObject, ContainerShape layoutParent, int width, int height, IAddContext context) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(layoutParent, true);
    final IGaService gaService = Graphiti.getGaService();
    
    Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
    
    Task addedTask = (Task) businessObject;

    DiagramBaseShape baseShape = DiagramBaseShape.ACTIVITY;

    if (ExtensionUtil.isCustomServiceTask(addedTask)) {
      final ServiceTask serviceTask = (ServiceTask) addedTask;
      final List<CustomServiceTask> customServiceTasks = ExtensionUtil.getCustomServiceTasks(ActivitiUiUtil.getProjectFromDiagram(diagram));

      CustomServiceTask targetTask = null;

      for (final CustomServiceTask customServiceTask : customServiceTasks) {
        if (customServiceTask.getId().equals(serviceTask.getExtensionId())) {
          targetTask = customServiceTask;
          break;
        }
      }

      if (!DiagramBaseShape.ACTIVITY.equals(targetTask.getDiagramBaseShape())) {
        baseShape = targetTask.getDiagramBaseShape();
      }
    }

    GraphicsAlgorithm algorithm = null;

    switch (baseShape) {
    case ACTIVITY:
      // check whether the context has a size (e.g. from a create feature)
      // otherwise define a default size for the shape
      width = width <= 0 ? 105 : width;
      height = height <= 0 ? 55 : height;

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
        roundedRectangle.setStyle(StyleUtil.getStyleForTask(diagram));
        gaService.setLocationAndSize(roundedRectangle, 0, 0, width, height);
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
        polygon.setStyle(StyleUtil.getStyleForTask(diagram));
        gaService.setLocationAndSize(polygon, 0, 0, width, height);
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
        circle.setStyle(StyleUtil.getStyleForTask(diagram));
        gaService.setLocationAndSize(circle, 0, 0, width, height);
      }
      break;
    }

    // SHAPE WITH TEXT
    // create shape for text
    final Shape shape = peCreateService.createShape(containerShape, false);

    // create and set text graphics algorithm
    final MultiText text = gaService.createDefaultMultiText(diagram, shape, addedTask.getName());
    text.setStyle(StyleUtil.getStyleForTask(diagram));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      text.setFont(gaService.manageFont(diagram, text.getFont().getName(), 11));
    }

    switch (baseShape) {
    case ACTIVITY:
      gaService.setLocationAndSize(text, 0, 20, width, height - 32);
      break;
    case GATEWAY:
      gaService.setLocationAndSize(text, 0, height + 5, width, 40);
      break;
    case EVENT:
      gaService.setLocationAndSize(text, 0, height + 5, width, 40);
      break;
    }

    // create link and wire it
    getFeatureProvider().link(shape, addedTask);

    // provide information to support direct-editing directly
    // after object creation (must be activated additionally)
    final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
    // set container shape for direct editing after object creation
    directEditingInfo.setMainPictogramElement(containerShape);
    // set shape and graphics algorithm where the editor for
    // direct editing shall be opened after object creation
    directEditingInfo.setPictogramElement(shape);
    directEditingInfo.setGraphicsAlgorithm(text);

    final Shape imageShape = peCreateService.createShape(containerShape, false);
    final Image image = gaService.createImage(imageShape, getIcon(addedTask));

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
    
    if (baseShape == DiagramBaseShape.ACTIVITY) {
      MultiInstanceLoopCharacteristics multiInstanceObject = addedTask.getLoopCharacteristics();
      if (multiInstanceObject != null) {
      
        if (StringUtils.isNotEmpty(multiInstanceObject.getLoopCardinality()) ||
            StringUtils.isNotEmpty(multiInstanceObject.getInputDataItem()) ||
            StringUtils.isNotEmpty(multiInstanceObject.getCompletionCondition())) {
          
          final Shape miShape = peCreateService.createShape(containerShape, false);
          Image miImage = null;
          if (multiInstanceObject.isSequential()) {
            miImage = gaService.createImage(miShape, PluginImage.IMG_MULTIINSTANCE_SEQUENTIAL.getImageKey());
          } else {
            miImage = gaService.createImage(miShape, PluginImage.IMG_MULTIINSTANCE_PARALLEL.getImageKey());
          }
          gaService.setLocationAndSize(miImage, (width - MI_IMAGE_SIZE) / 2, (height - MI_IMAGE_SIZE), MI_IMAGE_SIZE, MI_IMAGE_SIZE);
        }
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

    return containerShape;
  }
  
  protected String getIcon(Object bo) {
    if (bo instanceof ServiceTask) {
      ServiceTask serviceTask = (ServiceTask) bo;
      if (ServiceTask.MAIL_TASK.equalsIgnoreCase(serviceTask.getType())) {
        return PluginImage.IMG_MAILTASK.getImageKey();
      } else {
        return PluginImage.IMG_SERVICETASK.getImageKey();
      }
    
    } else if (bo instanceof UserTask) {
      return PluginImage.IMG_USERTASK.getImageKey();
    
    } else if (bo instanceof BusinessRuleTask) {
      return PluginImage.IMG_BUSINESSRULETASK.getImageKey();
    
    } else if (bo instanceof ManualTask) {
      return PluginImage.IMG_MANUALTASK.getImageKey();
    
    } else if (bo instanceof ReceiveTask) {
      return PluginImage.IMG_RECEIVETASK.getImageKey();
    
    } else if (bo instanceof ScriptTask) {
      return PluginImage.IMG_SCRIPTTASK.getImageKey();
    
    } else {
      // fallback
      return PluginImage.IMG_SERVICETASK.getImageKey();
    }
  }
}
