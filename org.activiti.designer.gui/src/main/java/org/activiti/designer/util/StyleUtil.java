/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.util;

import java.util.Collection;

import org.eclipse.graphiti.mm.StyleContainer;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.graphiti.util.PredefinedColoredAreas;

public class StyleUtil {

	private static final IColorConstant BPMN_CLASS_TEXT_FOREGROUND = new ColorConstant(51, 51, 153);

	// Midnight Blue
	private static final IColorConstant BPMN_CLASS_FOREGROUND = new ColorConstant(25, 25, 112);
	
	private static final IColorConstant BOUNDARY_EVENT_FOREGROUND = new ColorConstant(0, 0, 0);
	
	private static final IColorConstant EMBEDDED_PROCESS_FOREGROUND = new ColorConstant(0, 0, 0);

	public static Style getStyleForEClass(Diagram diagram) {
		final String styleId = "BPMN-CLASS"; //$NON-NLS-1$

		Style style = findStyle(diagram, styleId);
		if (style == null) { // style not found - create new style
		  IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, BPMN_CLASS_FOREGROUND));
			gaService.setRenderingStyle(style, PredefinedColoredAreas.getBlueWhiteGlossAdaptions());
			style.setLineWidth(2);
		}
		return style;
	}
	
	public static Style getStyleForCallActivity(Diagram diagram) {
    final String styleId = "CALL-ACTIVITY"; //$NON-NLS-1$

    Style style = findStyle(diagram, styleId);
    if (style == null) { // style not found - create new style
      IGaService gaService = Graphiti.getGaService();
      style = gaService.createStyle(diagram, styleId);
      style.setForeground(gaService.manageColor(diagram, BPMN_CLASS_FOREGROUND));
      gaService.setRenderingStyle(style, PredefinedColoredAreas.getBlueWhiteGlossAdaptions());
      style.setLineWidth(20);
    }
    return style;
  }
	
	public static Style getStyleForBoundaryEvent(Diagram diagram) {
    final String styleId = "BOUNDARY-EVENT"; //$NON-NLS-1$

    Style style = findStyle(diagram, styleId);
    if (style == null) { // style not found - create new style
      IGaService gaService = Graphiti.getGaService();
      style = gaService.createStyle(diagram, styleId);
      style.setForeground(gaService.manageColor(diagram, BOUNDARY_EVENT_FOREGROUND));
      gaService.setRenderingStyle(style, PredefinedColoredAreas.getSilverWhiteGlossAdaptions());
      style.setLineWidth(2);
    }
    return style;
  }
	
	public static Style getStyleForEmbeddedProcess(Diagram diagram) {
    final String styleId = "EMBEDDED-PROCESS"; //$NON-NLS-1$

    Style style = findStyle(diagram, styleId);
    if (style == null) { // style not found - create new style
      IGaService gaService = Graphiti.getGaService();
      style = gaService.createStyle(diagram, styleId);
      style.setForeground(gaService.manageColor(diagram, EMBEDDED_PROCESS_FOREGROUND));
      gaService.setRenderingStyle(style, PredefinedColoredAreas.getLightGrayAdaptions());
      style.setLineWidth(2);
    }
    return style;
  }

	public static Style getStyleForPolygon(Diagram diagram) {
		final String styleId = "BPMN-POLYGON-ARROW"; //$NON-NLS-1$

		Style style = findStyle(diagram, styleId);

		if (style == null) { // style not found - create new style
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, IColorConstant.BLACK));
			style.setBackground(gaService.manageColor(diagram, IColorConstant.BLACK));
			style.setLineWidth(1);
		}
		return style;
	}

	public static Style getStyleForEClassText(Diagram diagram) {
		final String styleId = "BPMNCLASS-TEXT"; //$NON-NLS-1$

		// this is a child style of the e-class-style
		Style parentStyle = getStyleForEClass(diagram);
		Style style = findStyle(parentStyle, styleId);

		if (style == null) { // style not found - create new style
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			// "overwrites" values from parent style
			style.setForeground(gaService.manageColor(diagram, BPMN_CLASS_TEXT_FOREGROUND));
		}
		return style;
	}

	// find the style with a given id in the style-container, can return null
	private static Style findStyle(StyleContainer styleContainer, String id) {
		// find and return style
		Collection<Style> styles = styleContainer.getStyles();
		if (styles != null) {
			for (Style style : styles) {
				if (id.equals(style.getId())) {
					return style;
				}
			}
		}
		return null;
	}
}
