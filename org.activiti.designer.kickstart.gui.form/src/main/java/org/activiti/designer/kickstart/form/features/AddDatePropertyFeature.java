package org.activiti.designer.kickstart.form.features;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.util.FormComponentStyles;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * @author Frederik Heremans
 */
public class AddDatePropertyFeature extends AbstractAddFormComponentFeature {

  protected static final int DEFAULT_COMPONENT_WIDTH = 500;
  protected static final int DEFAULT_LABEL_HEIGHT = 20;
  protected static final int DEFAULT_BOX_HEIGHT = 25;
  protected static final int TEXTAREA_DECORATION_SIZE = 10;

  public AddDatePropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context) {
    return context.getNewObject() instanceof DatePropertyDefinition;
  }

  @Override
  protected ContainerShape createContainerShape(Object newObject, ContainerShape layoutParent, int width, int height) {

    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(layoutParent, true);
    final IGaService gaService = Graphiti.getGaService();

    DatePropertyDefinition definition = (DatePropertyDefinition) newObject;

    // If no size has been supplied, revert to the default sizes
    if (width < 0) {
      width = DEFAULT_COMPONENT_WIDTH;
    }
    height = DEFAULT_LABEL_HEIGHT + DEFAULT_BOX_HEIGHT;

    RoundedRectangle rectangle;
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, 0, 0, width, height);

    // Create and set visible rectangle inside invisible rectangle
    rectangle = gaService.createRoundedRectangle(invisibleRectangle, 2, 2);
    rectangle.setStyle(FormComponentStyles.getInputFieldStyle(getDiagram()));
    rectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    gaService.setLocationAndSize(rectangle, 0, DEFAULT_LABEL_HEIGHT, width, height - DEFAULT_LABEL_HEIGHT);

    // Add label
    final Shape shape = peCreateService.createShape(containerShape, false);
    final MultiText text = gaService.createDefaultMultiText(getDiagram(), shape, getNameTextValue(definition));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
    text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      text.setFont(gaService.manageFont(getDiagram(), text.getFont().getName(), 11));
    }

    gaService.setLocationAndSize(text, 0, 0, width, DEFAULT_LABEL_HEIGHT);
    gaService.setLocationAndSize(shape.getGraphicsAlgorithm(), 0, 0, width, DEFAULT_LABEL_HEIGHT);

    // Add date-text
    Shape innerTextShape = peCreateService.createShape(containerShape, false);

    String dateValue = null;
    if (definition.isShowTime()) {
      Calendar noon = Calendar.getInstance();
      noon.set(Calendar.HOUR_OF_DAY, 12);
      noon.set(Calendar.MINUTE, 0);
      noon.set(Calendar.SECOND, 0);
      dateValue = DateFormat.getDateTimeInstance().format(noon.getTime());
    } else {
      dateValue = DateFormat.getDateInstance().format(new Date());
    }

    Text innerText = gaService.createPlainText(innerTextShape, dateValue);
    innerText.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
    innerText.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
    innerText.setBackground(FormComponentStyles.getCalandarDecorationColor(getDiagram()));
    innerText.setLineWidth(0);
    gaService.setLocationAndSize(innerText, 3, DEFAULT_LABEL_HEIGHT + 1, width - 4, height - DEFAULT_LABEL_HEIGHT - 2);

    // Add calendar decoration
    Shape decoration = peCreateService.createShape(containerShape, false);
    Rectangle decorationRect = gaService.createRectangle(decoration);
    decorationRect.setBackground(FormComponentStyles.getCalandarDecorationColor(getDiagram()));
    decorationRect.setForeground(FormComponentStyles.getDefaultForegroundColor(getDiagram()));
    gaService.setLocationAndSize(decorationRect, width - 20, DEFAULT_LABEL_HEIGHT + 4, 16, 16);

    Shape decorationInner = peCreateService.createShape(containerShape, false);
    Rectangle decorationInnerRect = gaService.createRectangle(decorationInner);
    decorationInnerRect.setBackground(FormComponentStyles.getCalandarTopDecorationColor(getDiagram()));
    decorationInnerRect.setLineWidth(0);
    decorationInnerRect.setLineVisible(false);
    gaService.setLocationAndSize(decorationInnerRect, width - 19, DEFAULT_LABEL_HEIGHT + 5, 14, 5);

    Shape decorationText = peCreateService.createShape(containerShape, false);
    Text dayText = gaService.createPlainText(decorationText,
        StringUtils.leftPad(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "", 2, '0'));
    dayText.setVerticalAlignment(Orientation.ALIGNMENT_MIDDLE);
    dayText.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    dayText.setBackground(FormComponentStyles.getCalandarDecorationColor(getDiagram()));
    dayText.setForeground(FormComponentStyles.getDefaultForegroundColor(getDiagram()));
    dayText.setFont(gaService.manageFont(getDiagram(), text.getFont().getName(), 8));
    dayText.setLineWidth(0);
    gaService.setLocationAndSize(dayText, width - 19, DEFAULT_LABEL_HEIGHT + 10, 14, 8);

    // Allow quick-edit
    final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
    // set container shape for direct editing after object creation
    directEditingInfo.setMainPictogramElement(containerShape);
    // set shape and graphics algorithm where the editor for
    // direct editing shall be opened after object creation
    directEditingInfo.setPictogramElement(containerShape);
    directEditingInfo.setGraphicsAlgorithm(innerText);

    // Create link and wire it
    link(containerShape, newObject);
    return containerShape;
  }

  protected String getNameTextValue(FormPropertyDefinition definition) {
    String value = definition.getName() != null ? definition.getName() : "";
    if (definition.isMandatory()) {
      value = value + " *";
    }
    return value;
  }

}
