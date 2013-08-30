package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.util.FormComponentStyles;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * @author Frederik Heremans
 */
public class AddFormGroupFeature extends AbstractAddFormComponentFeature {

  protected static final int DEFAULT_COMPONENT_WIDTH = 510;
  protected static final int DEFAULT_LABEL_HEIGHT = 25;
  protected static final int DEFAULT_BOX_HEIGHT = 60;

  public AddFormGroupFeature(KickstartFormFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context) {
    return context.getNewObject() instanceof FormPropertyGroup;
  }

  @Override
  protected ContainerShape createContainerShape(Object newObject, ContainerShape layoutParent, int width, int height) {

    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(layoutParent, true);
    final IGaService gaService = Graphiti.getGaService();

    FormPropertyGroup group = (FormPropertyGroup) newObject;

    // If no size has been supplied, revert to the default sizes
    if (width < 0) {
      width = DEFAULT_COMPONENT_WIDTH;
    }
    height = DEFAULT_LABEL_HEIGHT + DEFAULT_BOX_HEIGHT;

    final Rectangle rectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(rectangle, 0, 0, width, height);
    
    // Add label
    final Shape shape = peCreateService.createShape(containerShape, false);
    final MultiText text = gaService.createDefaultMultiText(getDiagram(), shape, group.getTitle());
    text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
    text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
    text.setFont(gaService.manageFont(getDiagram(), text.getFont().getName(), 15));
    text.setLineWidth(0);
    
    gaService.setLocationAndSize(shape.getGraphicsAlgorithm(), 0, 0, width, DEFAULT_LABEL_HEIGHT);
    gaService.setLocationAndSize(text, 5, 0, width - 10, DEFAULT_LABEL_HEIGHT);
    
    Rectangle groupMarkerRectangle = gaService.createPlainRectangle(rectangle);
    groupMarkerRectangle.setBackground(FormComponentStyles.getGroupBorderColor(getDiagram()));
    groupMarkerRectangle.setLineVisible(false);
    groupMarkerRectangle.setLineWidth(0);
    gaService.setLocationAndSize(groupMarkerRectangle, 0, 0, 1, 1000);
    
    
    final Polygon topLine = gaService.createPlainPolygon(rectangle, new int[]
        {5, DEFAULT_LABEL_HEIGHT, width - 5, DEFAULT_LABEL_HEIGHT});
    topLine.setForeground(FormComponentStyles.getDefaultForegroundColor(getDiagram()));
    
    
    

    // Allow quick-edit
    final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
    // set container shape for direct editing after object creation
    directEditingInfo.setMainPictogramElement(containerShape);
    // set shape and graphics algorithm where the editor for
    // direct editing shall be opened after object creation
    directEditingInfo.setPictogramElement(containerShape);
    directEditingInfo.setGraphicsAlgorithm(text);

    // Create link and wire it
    link(containerShape, newObject);
    return containerShape;
  }
}
