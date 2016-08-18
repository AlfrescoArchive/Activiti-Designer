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
package com.alfresco.designer.gui.controller;

import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.alfresco.AlfrescoMailTask;
import org.activiti.bpmn.model.alfresco.AlfrescoScriptTask;
import org.activiti.bpmn.model.alfresco.AlfrescoUserTask;
import org.activiti.designer.PluginImage;
import org.activiti.designer.controller.AbstractBusinessObjectShapeController;
import org.activiti.designer.controller.BusinessObjectShapeController;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.MultiText;
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
public class AlfrescoTaskShapeController extends AbstractBusinessObjectShapeController {
  
  private static final int IMAGE_SIZE = 16;
  
  public AlfrescoTaskShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof ServiceTask) {
      ServiceTask serviceTask = (ServiceTask) businessObject;
      if (AlfrescoScriptTask.ALFRESCO_SCRIPT_DELEGATE.equalsIgnoreCase(serviceTask.getImplementation())) {
        return true;
      } else {
        return false;
      }
      
    } else if (businessObject instanceof AlfrescoUserTask || businessObject instanceof AlfrescoMailTask || 
            businessObject instanceof AlfrescoScriptTask) {
      
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

    GraphicsAlgorithm algorithm = null;

    // check whether the context has a size (e.g. from a create feature)
    // otherwise define a default size for the shape
    width = width <= 0 ? 105 : width;
    height = height <= 0 ? 55 : height;

    // create invisible outer rectangle expanded by
    // the width needed for the anchor
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(), width, height);

    // create and set visible rectangle inside invisible rectangle
    RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(invisibleRectangle, 20, 20);
    algorithm = roundedRectangle;
    roundedRectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    roundedRectangle.setStyle(StyleUtil.getStyleForTask(diagram));
    gaService.setLocationAndSize(roundedRectangle, 0, 0, width, height);

    // create shape for text
    Shape shape = peCreateService.createShape(containerShape, false);

    // create and set text graphics algorithm
    final MultiText text = gaService.createDefaultMultiText(diagram, shape, addedTask.getName());
    text.setStyle(StyleUtil.getStyleForTask(diagram));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      text.setFont(gaService.manageFont(diagram, text.getFont().getName(), 11));
    }

    gaService.setLocationAndSize(text, 0, 20, width, 30);
   
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
    
    shape = peCreateService.createShape(containerShape, false);
    Image image = gaService.createImage(shape, getIcon(addedTask));
    gaService.setLocationAndSize(image, 5, 5, IMAGE_SIZE, IMAGE_SIZE);
    
    shape = peCreateService.createShape(containerShape, false);
    image = gaService.createImage(shape, PluginImage.IMG_ALFRESCO_LOGO.getImageKey());

    gaService.setLocationAndSize(image, 85, 3, IMAGE_SIZE, IMAGE_SIZE);

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
    if (bo instanceof AlfrescoUserTask) {
      return PluginImage.IMG_USERTASK.getImageKey();
      
    } else if (bo instanceof AlfrescoScriptTask) {
      return PluginImage.IMG_SCRIPTTASK.getImageKey();
      
    } else if (bo instanceof AlfrescoMailTask) {
      return PluginImage.IMG_MAILTASK.getImageKey();
      
    } else if (bo instanceof ServiceTask) {
      ServiceTask serviceTask = (ServiceTask) bo;
      if (AlfrescoScriptTask.ALFRESCO_SCRIPT_DELEGATE.equalsIgnoreCase(serviceTask.getImplementation())) {
        boolean isMailTask = false;
        for (FieldExtension fieldExtension : serviceTask.getFieldExtensions()) {
          if ("script".equalsIgnoreCase(fieldExtension.getFieldName())) {
            if(fieldExtension.getStringValue() != null) {
              if (fieldExtension.getStringValue().contains("mail.execute(bpm_package);")) {
                isMailTask = true;
              }
            } else if(fieldExtension.getExpression() != null) {
              if (fieldExtension.getExpression().contains("mail.execute(bpm_package);")) {
                isMailTask = true;
              }
            }
          }
        }
        if (isMailTask) {
          return PluginImage.IMG_MAILTASK.getImageKey();
        } else {
          return PluginImage.IMG_SCRIPTTASK.getImageKey();
        }
        
      } else {
        // fallback
        return PluginImage.IMG_SERVICETASK.getImageKey();
      }
      
    } else {
      // fallback
      return PluginImage.IMG_SERVICETASK.getImageKey();
    }
  }
}
