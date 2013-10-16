package org.activiti.designer.kickstart.form.diagram.shape;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.util.FormComponentStyles;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for
 * {@link FormPropertyGroup} objects.
 *  
 * @author Frederik Heremans
 */
public class PropertyGroupShapeController extends AbstractBusinessObjectShapeController {
  
  public PropertyGroupShapeController(KickstartFormFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    return businessObject instanceof FormPropertyGroup;
  }

  @Override
  public ContainerShape createShape(Object businessObject, ContainerShape layoutParent, int width, int height) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(layoutParent, true);
    final IGaService gaService = Graphiti.getGaService();

    FormPropertyGroup group = (FormPropertyGroup) businessObject;
    
    Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
    String label = getLabelTextValue(group);

    // If no size has been supplied, revert to the default sizes
    if (width < 0) {
      width = FormComponentStyles.DEFAULT_COMPONENT_WIDTH + 10;
    }
    if(label.isEmpty()) {
      height = FormComponentStyles.DEFAULT_GROUP_HEIGHT;
    } else {
      height = FormComponentStyles.DEFAULT_GROUP_LABEL_HEIGHT + FormComponentStyles.DEFAULT_GROUP_HEIGHT;
    }

    final Rectangle rectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(rectangle, 0, 0, width, height);
    
    // Add label
    final Shape shape = peCreateService.createShape(containerShape, false);
    final MultiText text = gaService.createDefaultMultiText(diagram, shape, label);
    text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
    text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
    text.setFont(gaService.manageFont(diagram, text.getFont().getName(), 15));
    text.setLineWidth(0);
    
    gaService.setLocationAndSize(shape.getGraphicsAlgorithm(), 0, 0, width, FormComponentStyles.DEFAULT_GROUP_LABEL_HEIGHT);
    gaService.setLocationAndSize(text, 5, 0, width - 10, FormComponentStyles.DEFAULT_GROUP_LABEL_HEIGHT);
    
    Rectangle groupMarkerRectangle = gaService.createPlainRectangle(rectangle);
    groupMarkerRectangle.setBackground(FormComponentStyles.getGroupBorderColor(diagram));
    groupMarkerRectangle.setLineVisible(false);
    groupMarkerRectangle.setLineWidth(0);
    gaService.setLocationAndSize(groupMarkerRectangle, 0, 0, 2, 1000);
    
    final Polygon topLine = gaService.createPlainPolygon(rectangle, new int[]
        {5, FormComponentStyles.DEFAULT_GROUP_LABEL_HEIGHT, width, FormComponentStyles.DEFAULT_GROUP_LABEL_HEIGHT});
    topLine.setForeground(FormComponentStyles.getDefaultForegroundColor(diagram));
    topLine.setLineVisible(!label.isEmpty());
    
    
    // Allow quick-edit
    final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
    // set container shape for direct editing after object creation
    directEditingInfo.setMainPictogramElement(containerShape);
    // set shape and graphics algorithm where the editor for
    // direct editing shall be opened after object creation
    directEditingInfo.setPictogramElement(containerShape);
    directEditingInfo.setGraphicsAlgorithm(text);
    directEditingInfo.setActive(true);
    
    return containerShape;
  }

  @Override
  public void updateShape(ContainerShape shape, Object businessObject, int width, int height) {
    FormPropertyGroup group = (FormPropertyGroup) businessObject;
    String label = getLabelTextValue(group);

    
    // Update the label
    MultiText labelText = findNameMultiText(shape);
    if(labelText != null) {
      if(!StringUtils.equals(labelText.getValue(), label)) {
        labelText.setValue(label);
      }
    }
    
    // Hide line if no title is used
    Polygon line = findPolygon(shape.getGraphicsAlgorithm());
    if(line != null) {
      if(line.getLineVisible() == label.isEmpty()) {
        ((KickstartFormFeatureProvider) getFeatureProvider()).getFormLayouter().relayout(shape);
      }
      line.setLineVisible(!label.isEmpty());
    }
  }
  
  @Override
  public boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject) {
    FormPropertyGroup group = (FormPropertyGroup) businessObject;
    
    // Check label text
    String currentLabel = (String) extractShapeData(LABEL_DATA_KEY, shape);
    String newLabel = getLabelTextValue(group);
    if(!StringUtils.equals(currentLabel, newLabel)) {
      return true;
    }
    return false;
  }
  
  @Override
  public GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(ContainerShape container) {
    return container.getChildren().get(0).getGraphicsAlgorithm();
  }
  
  protected Polygon findPolygon(GraphicsAlgorithm ga) {
    if (ga instanceof Polygon) {
      return (Polygon) ga;
    }
    Polygon foundPolygon = null;
    for (EObject child : ga.eContents()) {
      if (child instanceof GraphicsAlgorithm) {
        foundPolygon = findPolygon((GraphicsAlgorithm) child);

        if (foundPolygon != null) {
          return foundPolygon;
        }

      }
    }
    return null;
  }
}
