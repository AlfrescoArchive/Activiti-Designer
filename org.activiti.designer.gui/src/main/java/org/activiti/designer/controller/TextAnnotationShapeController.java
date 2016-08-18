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
package org.activiti.designer.controller;

import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.TextAnnotation;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.bpmn.BpmnExtensionUtil;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
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
public class TextAnnotationShapeController extends AbstractBusinessObjectShapeController {
  
  public TextAnnotationShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof TextAnnotation) {
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
    
    final TextAnnotation annotation = (TextAnnotation) businessObject;
    
    height = Math.max(50, height);
    width = Math.max(100, width);
    final int commentEdge = 20;
    
    final Rectangle rect = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(rect, context.getX(), context.getY(), width, height);
    
    final Shape lineShape = peCreateService.createShape(containerShape, false);
    final Polyline line = gaService.createPolyline(lineShape, new int[] { 
        commentEdge, 0, 0, 0, 0, height, commentEdge, height });
    line.setStyle(StyleUtil.getStyleForTask(diagram));
    line.setLineWidth(2);
    gaService.setLocationAndSize(line, 0, 0, commentEdge, height);
    
    final Shape textShape = peCreateService.createShape(containerShape, false);
    String annotationText = BpmnExtensionUtil.getTextAnnotationText(annotation, ActivitiPlugin.getDefault());
    final MultiText text = gaService.createDefaultMultiText(diagram, textShape, annotationText);
    text.setStyle(StyleUtil.getStyleForTask(diagram));
    text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      text.setFont(gaService.manageFont(diagram, text.getFont().getName(), 11));
    }
    gaService.setLocationAndSize(text, 5, 5, width - 5, height - 5);
    
    getFeatureProvider().link(textShape, annotation);
    
    peCreateService.createChopboxAnchor(containerShape);

    return containerShape;
  }
}
