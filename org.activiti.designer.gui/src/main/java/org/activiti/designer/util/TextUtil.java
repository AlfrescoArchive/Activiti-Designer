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
    
    if (textDimension != null) {
      if (textDimension.getWidth() > 95) {
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
  }
  
  public static void setTextSize(int width, MultiText textComponent) {
    IDimension textDimension = GraphitiUi.getUiLayoutService().calculateTextSize(textComponent.getValue(), textComponent.getFont());
    int lineCount = 1;
    double textWidth;
    if (textDimension != null) {
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
}
