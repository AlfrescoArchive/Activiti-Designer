package org.activiti.designer.kickstart.process.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

/**
 * Class containing style constants and component sizes.
 * 
 * @author Tijs Rademakers
 */
public final class StepDefinitionStyles {

  // Style ID's
  private static final String STEP_DEFINITION_RECTANGLE_STYLE_ID = "step-definition-rectangle";
  
  // Colors
  private static final IColorConstant DEFAULT_FOREGROUND_COLOR = new ColorConstant(0, 0, 0);
  private static final IColorConstant SUBTLE_FOREGROUND_COLOR = new ColorConstant(200, 200, 200);
  private static final IColorConstant DEFAULT_BACKGROUND_COLOR = new ColorConstant(255, 255, 255);
  private static final IColorConstant SUBTLE_BACKGROUND_COLOR = new ColorConstant(240, 240, 240);
  private static final IColorConstant SEVERE_BACKGROUND_COLOR = new ColorConstant(211, 95, 95);

  // Sizes for components
  public static final int DEFAULT_COMPONENT_WIDTH = 600;
  public static final int DEFAULT_LABEL_HEIGHT = 20;
  public static final int DEFAULT_COMPONENT_BOX_HEIGHT = 40;
  public static final int DEFAULT_PARALLEL_BOX_HEIGHT = 60;

  private static Map<String, Style> styleMap = new HashMap<String, Style>();
  
  private StepDefinitionStyles() {
    // Private constructor to prevent instantiation
  }
  
  /**
   * @return style that should be used for the rectangle of an input-field.
   */
  public static Style getStepDefinitionStyle(Diagram diagram) {
    Style style = styleMap.get(STEP_DEFINITION_RECTANGLE_STYLE_ID);
    if(style == null) {
      IGaService gaService = Graphiti.getGaService();
      style = gaService.createStyle(diagram, STEP_DEFINITION_RECTANGLE_STYLE_ID);
      style.setForeground(getDefaultForegroundColor(diagram));
      style.setBackground(gaService.manageColor(diagram, DEFAULT_BACKGROUND_COLOR));
      style.setLineWidth(20);
      styleMap.put(STEP_DEFINITION_RECTANGLE_STYLE_ID, style);
    }
    return style;
  }
  
  public static Color getDefaultForegroundColor(Diagram diagram) {
    return Graphiti.getGaService().manageColor(diagram, DEFAULT_FOREGROUND_COLOR);
  }
  
  public static Color getSubtleForegroundColor(Diagram diagram) {
    return Graphiti.getGaService().manageColor(diagram, SUBTLE_FOREGROUND_COLOR);
  }
  
  public static Color getSubtleBackgroundColor(Diagram diagram) {
    return Graphiti.getGaService().manageColor(diagram, SUBTLE_BACKGROUND_COLOR);
  }
  
  public static Color getDefaultBackgroundColor(Diagram diagram) {
    return Graphiti.getGaService().manageColor(diagram, DEFAULT_BACKGROUND_COLOR);
  }

  public static Color getSevereBackgroundColor(Diagram diagram) {
    // TODO Auto-generated method stub
    return Graphiti.getGaService().manageColor(diagram, SEVERE_BACKGROUND_COLOR);
  }
  
}
