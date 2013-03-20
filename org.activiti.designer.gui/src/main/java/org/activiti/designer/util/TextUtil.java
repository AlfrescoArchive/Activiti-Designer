package org.activiti.designer.util;

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.services.GraphitiUi;


public class TextUtil {

  public static void setTextSize(MultiText textComponent) {
    IDimension textDimension = GraphitiUi.getUiLayoutService().calculateTextSize(textComponent.getValue(), textComponent.getFont());
    int lineCount = 1;
    if(textDimension.getWidth() > 95) {
      double width = textDimension.getWidth() / 95.0;
      lineCount = (int) Math.floor(width);
      if(lineCount < width) {
        lineCount++;
      }
      lineCount++;
    }
    IGaService gaService = Graphiti.getGaService();
    gaService.setSize(textComponent, 100, lineCount * textDimension.getHeight());
  }
  
  public static void setTextSize(int width, MultiText textComponent) {
    IDimension textDimension = GraphitiUi.getUiLayoutService().calculateTextSize(textComponent.getValue(), textComponent.getFont());
    int lineCount = 1;
    double textWidth;
    if (textDimension.getWidth() > width) {
      textWidth = width;
      double lines = textDimension.getWidth() / (double) width;
      lineCount = (int) Math.floor(lines);
      if(lineCount < lines) {
        lineCount++;
      }
      lineCount++;
    } else {
      textWidth = textDimension.getWidth();
    }
    
    IGaService gaService = Graphiti.getGaService();
    gaService.setSize(textComponent, (int) textWidth, lineCount * textDimension.getHeight());
  }
}
