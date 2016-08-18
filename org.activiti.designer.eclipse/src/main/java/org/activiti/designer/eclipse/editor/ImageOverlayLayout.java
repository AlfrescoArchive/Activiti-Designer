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

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

public class ImageOverlayLayout {

  private static final int BOX_MARGIN = 10;
  private static final int BOX_PADDING = 10;
  private static final int TEXT_LINE_MARGIN = 6;
  private static final int RECT_ARC = 10;

  private final ImageOverlayContext context;

  private Point topLeft;
  private Point topRight;
  private Point bottomRight;
  private Point bottomLeft;

  private int textLineHeight;

  public ImageOverlayLayout(final ImageOverlayContext context) {
    super();
    this.context = context;

    processContext();
  }

  public int getTextLineHeight() {
    return textLineHeight;
  }

  public int getTextLeftAlign() {
    return topLeft.x + BOX_PADDING;
  }

  public int getTextLineMargin() {
    return TEXT_LINE_MARGIN;
  }

  public int getBoxPadding() {
    return BOX_PADDING;
  }

  public Color getTextColor() {
    return getColorPreference(Preferences.SAVE_IMAGE_ADD_OVERLAY_TEXT_COLOR);
  }

  public Color getBorderColor() {
    return getColorPreference(Preferences.SAVE_IMAGE_ADD_OVERLAY_BORDER_COLOR);
  }

  public Color getBackgroundColor() {
    return getColorPreference(Preferences.SAVE_IMAGE_ADD_OVERLAY_BACKGROUND_COLOR);
  }

  private void processContext() {
    // Determine max width
    int maxWidth = determineMaxWidth();

    // Determine height
    int numberOfLines = determineNumberOfLines();
    textLineHeight = context.getImageGC().textExtent(context.getProcessNameDisplay()).y;
    int totalHeight = numberOfLines * textLineHeight + (numberOfLines - 1) * TEXT_LINE_MARGIN;

    // Based on position, determine all 4 corner points.
    determineCornerPoints(maxWidth, totalHeight);

  }
  private void determineCornerPoints(int maxWidth, int totalHeight) {

    final int canvasWidth = context.getImageGC().getClipping().width;
    final int canvasHeight = context.getImageGC().getClipping().height;

    final int width = BOX_MARGIN * 2 + BOX_PADDING * 2 + maxWidth;
    final int height = BOX_MARGIN * 2 + BOX_PADDING * 2 + totalHeight;

    if (context.getCornerPreference() == 1) {
      topLeft = new Point(BOX_MARGIN, BOX_MARGIN);
      topRight = new Point(width - BOX_MARGIN, BOX_MARGIN);
      bottomRight = new Point(width - BOX_MARGIN, height - BOX_MARGIN);
      bottomLeft = new Point(BOX_MARGIN, height - BOX_MARGIN);
    } else if (context.getCornerPreference() == 2) {
      topLeft = new Point(canvasWidth - width + BOX_MARGIN, BOX_MARGIN);
      topRight = new Point(canvasWidth - BOX_MARGIN, BOX_MARGIN);
      bottomRight = new Point(canvasWidth - BOX_MARGIN, height - BOX_MARGIN);
      bottomLeft = new Point(canvasWidth - width + BOX_MARGIN, height - BOX_MARGIN);
    } else if (context.getCornerPreference() == 3) {
      topLeft = new Point(canvasWidth - width + BOX_MARGIN, canvasHeight - height + BOX_MARGIN);
      topRight = new Point(canvasWidth - BOX_MARGIN, canvasHeight - height + BOX_MARGIN);
      bottomRight = new Point(canvasWidth - BOX_MARGIN, canvasHeight - BOX_MARGIN);
      bottomLeft = new Point(canvasWidth - width + BOX_MARGIN, canvasHeight - BOX_MARGIN);
    } else if (context.getCornerPreference() == 4) {
      topLeft = new Point(BOX_MARGIN, canvasHeight - height + BOX_MARGIN);
      topRight = new Point(width - BOX_MARGIN, canvasHeight - height + BOX_MARGIN);
      bottomRight = new Point(width - BOX_MARGIN, canvasHeight - BOX_MARGIN);
      bottomLeft = new Point(BOX_MARGIN, canvasHeight - BOX_MARGIN);
    }

  }

  private int determineMaxWidth() {

    int maxX = 0;

    Point point = context.getImageGC().textExtent(context.getProcessNameDisplay());

    maxX = Math.max(maxX, point.x);

    if (context.isFilenameEnabled()) {
      point = context.getImageGC().textExtent(context.getFilenameDisplay());
      maxX = Math.max(maxX, point.x);
    }

    if (context.isDateEnabled()) {
      point = context.getImageGC().textExtent(context.getDateDisplay());
      maxX = Math.max(maxX, point.x);
    }

    if (context.isKeyEnabled()) {
      point = context.getImageGC().textExtent(context.getProcessKeyDisplay());
      maxX = Math.max(maxX, point.x);
    }

    if (context.isNamespaceEnabled()) {
      point = context.getImageGC().textExtent(context.getProcessNamespaceDisplay());
      maxX = Math.max(maxX, point.x);
    }

    if (context.isRevisionEnabled()) {
      point = context.getImageGC().textExtent(context.getRevisionDisplay());
      maxX = Math.max(maxX, point.x);
    }

    return maxX;
  }

  private int determineNumberOfLines() {

    int count = 1;

    if (context.isFilenameEnabled()) {
      count++;
    }

    if (context.isDateEnabled()) {
      count++;
    }

    if (context.isKeyEnabled()) {
      count++;
    }

    if (context.isNamespaceEnabled()) {
      count++;
    }

    if (context.isRevisionEnabled()) {
      count++;
    }

    return count;
  }

  private Color getColorPreference(final Preferences preference) {
    final String fontPreference = PreferencesUtil.getStringPreference(preference, ActivitiPlugin.getDefault());
    final String[] rgbArray = fontPreference.split(",");

    return new Color(context.getImageGC().getDevice(), new RGB(Integer.parseInt(rgbArray[0]), Integer.parseInt(rgbArray[1]), Integer.parseInt(rgbArray[2])));
  }

  public Point getTopLeft() {
    return topLeft;
  }

  public Point getTopRight() {
    return topRight;
  }

  public Point getBottomRight() {
    return bottomRight;
  }

  public Point getBottomLeft() {
    return bottomLeft;
  }

  public int getRectArc() {
    return RECT_ARC;
  }

}
