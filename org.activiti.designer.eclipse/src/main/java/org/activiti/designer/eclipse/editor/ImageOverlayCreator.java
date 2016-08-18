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
package org.activiti.designer.eclipse.editor;

import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;

public class ImageOverlayCreator {

  private final GC imageGC;

  public ImageOverlayCreator(final GC imageGC) {
    super();
    this.imageGC = imageGC;
  }

  public void addOverlay(final String modelFileName, final BpmnMemoryModel model) {

    final String processName = model.getBpmnModel().getMainProcess().getName();
    final String processKey = model.getBpmnModel().getMainProcess().getId();
    final String processNamespace = model.getBpmnModel().getTargetNamespace();

    final ImageOverlayContext context = new ImageOverlayContext(model.getModelFile());
    context.setImageGC(imageGC);
    context.setProcessName(processName);
    context.setProcessKey(processKey);
    context.setProcessNamespace(processNamespace);

    final ImageOverlayLayout layoutData = new ImageOverlayLayout(context);

    drawOverlay(context, layoutData);
  }
  private void drawOverlay(final ImageOverlayContext context, final ImageOverlayLayout layoutData) {

    imageGC.setLineWidth(2);

    imageGC.setForeground(layoutData.getBorderColor());
    imageGC.setBackground(layoutData.getBackgroundColor());

    final int rectTopX = layoutData.getTopLeft().x;
    final int rectTopY = layoutData.getTopLeft().y;
    final int rectWidth = layoutData.getBottomRight().x - layoutData.getTopLeft().x;
    final int rectHeight = layoutData.getBottomRight().y - layoutData.getTopLeft().y;
    final int rectArc = layoutData.getRectArc();

    imageGC.fillRoundRectangle(rectTopX, rectTopY, rectWidth, rectHeight, rectArc, rectArc);
    imageGC.drawRoundRectangle(layoutData.getTopLeft().x, layoutData.getTopLeft().y, layoutData.getBottomRight().x - layoutData.getTopLeft().x,
            layoutData.getBottomRight().y - layoutData.getTopLeft().y, 5, 5);

    imageGC.setForeground(layoutData.getTextColor());

    final Font originalFont = imageGC.getFont();
    final Font boldFont = createBoldFont(originalFont);

    final int lineHeight = layoutData.getTextLineHeight();

    int yPos = layoutData.getTopLeft().y + layoutData.getBoxPadding();

    imageGC.setFont(boldFont);
    imageGC.drawText(context.getProcessNameDisplay(), layoutData.getTextLeftAlign(), yPos, false);
    yPos = yPos + lineHeight + layoutData.getTextLineMargin();

    imageGC.setFont(originalFont);
    if (context.isFilenameEnabled()) {
      imageGC.drawText(context.getFilenameDisplay(), layoutData.getTextLeftAlign(), yPos, false);
      yPos = yPos + lineHeight + layoutData.getTextLineMargin();
    }

    if (context.isDateEnabled()) {
      imageGC.drawText(context.getDateDisplay(), layoutData.getTextLeftAlign(), yPos, false);
      yPos = yPos + lineHeight + layoutData.getTextLineMargin();
    }

    if (context.isKeyEnabled()) {
      imageGC.drawText(context.getProcessKeyDisplay(), layoutData.getTextLeftAlign(), yPos, false);
      yPos = yPos + lineHeight + layoutData.getTextLineMargin();
    }

    if (context.isNamespaceEnabled()) {
      imageGC.drawText(context.getProcessNamespaceDisplay(), layoutData.getTextLeftAlign(), yPos, false);
      yPos = yPos + lineHeight + layoutData.getTextLineMargin();
    }

    if (context.isRevisionEnabled()) {
      imageGC.drawText(context.getRevisionDisplay(), layoutData.getTextLeftAlign(), yPos, false);
      yPos = yPos + lineHeight + layoutData.getTextLineMargin();
    }

    yPos = yPos - layoutData.getTextLineMargin();

    boldFont.dispose();
  }

  private Font createBoldFont(Font originalFont) {
    FontData[] fontData = originalFont.getFontData();
    for (int i = 0; i < fontData.length; i++) {
      fontData[i].setStyle(SWT.BOLD);
    }
    return new Font(originalFont.getDevice(), fontData);
  }

}
