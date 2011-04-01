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

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.mm.StyleContainer;
import org.eclipse.graphiti.mm.algorithms.styles.AdaptedGradientColoredAreas;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.GradientColoredArea;
import org.eclipse.graphiti.mm.algorithms.styles.GradientColoredAreas;
import org.eclipse.graphiti.mm.algorithms.styles.LocationType;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.ColorUtil;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.graphiti.util.IGradientType;
import org.eclipse.graphiti.util.IPredefinedRenderingStyle;

public class StyleUtil {

	private static final IColorConstant BPMN_CLASS_FOREGROUND = new ColorConstant(0, 0, 0);
	
	private static final IColorConstant BOUNDARY_EVENT_FOREGROUND = new ColorConstant(0, 0, 0);
	
	private static final IColorConstant EMBEDDED_PROCESS_FOREGROUND = new ColorConstant(0, 0, 0);
	
	private static Color blackColor = null;
	
	public static Style getStyleForTask(Diagram diagram) {
    final String styleId = "TASK"; //$NON-NLS-1$

    Style style = findStyle(diagram, styleId);
    if (style == null) { // style not found - create new style
      IGaService gaService = Graphiti.getGaService();
      style = gaService.createStyle(diagram, styleId);
      style.setForeground(gaService.manageColor(diagram, BPMN_CLASS_FOREGROUND));
      gaService.setRenderingStyle(style, getDefaultTaskColor());
      style.setLineWidth(20);
    }
    return style;
  }
	
	public static Style getStyleForEvent(Diagram diagram) {
    final String styleId = "EVENT"; //$NON-NLS-1$

    Style style = findStyle(diagram, styleId);
    if (style == null) { // style not found - create new style
      IGaService gaService = Graphiti.getGaService();
      style = gaService.createStyle(diagram, styleId);
      style.setForeground(gaService.manageColor(diagram, BPMN_CLASS_FOREGROUND));
      gaService.setRenderingStyle(style, getDefaultEventColor());
      style.setLineWidth(20);
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
	
	private static AdaptedGradientColoredAreas getDefaultTaskColor() {
	  final AdaptedGradientColoredAreas agca = StylesFactory.eINSTANCE.createAdaptedGradientColoredAreas();
    agca.setDefinedStyleId("bpmnTaskStyle");
    agca.setGradientType(IGradientType.VERTICAL);
    final GradientColoredAreas defaultGradientColoredAreas = StylesFactory.eINSTANCE.createGradientColoredAreas();
    defaultGradientColoredAreas.setStyleAdaption(IPredefinedRenderingStyle.STYLE_ADAPTATION_DEFAULT);
    final EList<GradientColoredArea> gcas = defaultGradientColoredAreas.getGradientColor();
    addGradientColoredArea(gcas, "FAFBFC", 0, LocationType.LOCATION_TYPE_ABSOLUTE_START, "FFFFCC", 0, //$NON-NLS-1$ //$NON-NLS-2$
        LocationType.LOCATION_TYPE_ABSOLUTE_END);
    agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_DEFAULT, defaultGradientColoredAreas);
    
    final GradientColoredAreas primarySelectedGradientColoredAreas = StylesFactory.eINSTANCE.createGradientColoredAreas();
    primarySelectedGradientColoredAreas.setStyleAdaption(IPredefinedRenderingStyle.STYLE_ADAPTATION_DEFAULT);
    final EList<GradientColoredArea> selectedGcas = primarySelectedGradientColoredAreas.getGradientColor();
    addGradientColoredArea(selectedGcas, "E5E5C2", 0, LocationType.LOCATION_TYPE_ABSOLUTE_START, "E5E5C2", 0, //$NON-NLS-1$ //$NON-NLS-2$
        LocationType.LOCATION_TYPE_ABSOLUTE_END);
    
    agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_PRIMARY_SELECTED,
            primarySelectedGradientColoredAreas);
    
    final GradientColoredAreas secondarySelectedGradientColoredAreas = StylesFactory.eINSTANCE.createGradientColoredAreas();
    secondarySelectedGradientColoredAreas.setStyleAdaption(IPredefinedRenderingStyle.STYLE_ADAPTATION_DEFAULT);
    final EList<GradientColoredArea> secondarySelectedGcas = secondarySelectedGradientColoredAreas.getGradientColor();
    addGradientColoredArea(secondarySelectedGcas, "E5E5C2", 0, LocationType.LOCATION_TYPE_ABSOLUTE_START, "E5E5C2", 0, //$NON-NLS-1$ //$NON-NLS-2$
        LocationType.LOCATION_TYPE_ABSOLUTE_END);
    
    agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_SECONDARY_SELECTED,
            secondarySelectedGradientColoredAreas);
    return agca;
	}
	
	private static AdaptedGradientColoredAreas getDefaultEventColor() {
    final AdaptedGradientColoredAreas agca = StylesFactory.eINSTANCE.createAdaptedGradientColoredAreas();
    agca.setDefinedStyleId("bpmnEventStyle");
    agca.setGradientType(IGradientType.VERTICAL);
    final GradientColoredAreas defaultGradientColoredAreas = StylesFactory.eINSTANCE.createGradientColoredAreas();
    defaultGradientColoredAreas.setStyleAdaption(IPredefinedRenderingStyle.STYLE_ADAPTATION_DEFAULT);
    final EList<GradientColoredArea> gcas = defaultGradientColoredAreas.getGradientColor();
    addGradientColoredArea(gcas, "FAFBFC", 0, LocationType.LOCATION_TYPE_ABSOLUTE_START, "FAFBFC", 0, //$NON-NLS-1$ //$NON-NLS-2$
        LocationType.LOCATION_TYPE_ABSOLUTE_END);
    agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_DEFAULT, defaultGradientColoredAreas);
    
    final GradientColoredAreas primarySelectedGradientColoredAreas = StylesFactory.eINSTANCE.createGradientColoredAreas();
    primarySelectedGradientColoredAreas.setStyleAdaption(IPredefinedRenderingStyle.STYLE_ADAPTATION_DEFAULT);
    final EList<GradientColoredArea> selectedGcas = primarySelectedGradientColoredAreas.getGradientColor();
    addGradientColoredArea(selectedGcas, "E5E5C2", 0, LocationType.LOCATION_TYPE_ABSOLUTE_START, "E5E5C2", 0, //$NON-NLS-1$ //$NON-NLS-2$
        LocationType.LOCATION_TYPE_ABSOLUTE_END);
    
    agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_PRIMARY_SELECTED,
            primarySelectedGradientColoredAreas);
    
    final GradientColoredAreas secondarySelectedGradientColoredAreas = StylesFactory.eINSTANCE.createGradientColoredAreas();
    secondarySelectedGradientColoredAreas.setStyleAdaption(IPredefinedRenderingStyle.STYLE_ADAPTATION_DEFAULT);
    final EList<GradientColoredArea> secondarySelectedGcas = secondarySelectedGradientColoredAreas.getGradientColor();
    addGradientColoredArea(secondarySelectedGcas, "E5E5C2", 0, LocationType.LOCATION_TYPE_ABSOLUTE_START, "E5E5C2", 0, //$NON-NLS-1$ //$NON-NLS-2$
        LocationType.LOCATION_TYPE_ABSOLUTE_END);
    
    agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_SECONDARY_SELECTED,
            secondarySelectedGradientColoredAreas);
    return agca;
  }
	
	protected static void addGradientColoredArea(EList<GradientColoredArea> gcas, String colorStart, int locationValueStart,
	        LocationType locationTypeStart, String colorEnd, int locationValueEnd, LocationType locationTypeEnd) {
	      final GradientColoredArea gca = StylesFactory.eINSTANCE.createGradientColoredArea();
	      gcas.add(gca);
	      gca.setStart(StylesFactory.eINSTANCE.createGradientColoredLocation());
	      gca.getStart().setColor(StylesFactory.eINSTANCE.createColor());
	      gca.getStart().getColor().setBlue(ColorUtil.getBlueFromHex(colorStart));
	      gca.getStart().getColor().setGreen(ColorUtil.getGreenFromHex(colorStart));
	      gca.getStart().getColor().setRed(ColorUtil.getRedFromHex(colorStart));
	      gca.getStart().setLocationType(locationTypeStart);
	      gca.getStart().setLocationValue(locationValueStart);
	      gca.setEnd(StylesFactory.eINSTANCE.createGradientColoredLocation());
	      gca.getEnd().setColor(StylesFactory.eINSTANCE.createColor());
	      gca.getEnd().getColor().setBlue(ColorUtil.getBlueFromHex(colorEnd));
	      gca.getEnd().getColor().setGreen(ColorUtil.getGreenFromHex(colorEnd));
	      gca.getEnd().getColor().setRed(ColorUtil.getRedFromHex(colorEnd));
	      gca.getEnd().setLocationType(locationTypeEnd);
	      gca.getEnd().setLocationValue(locationValueEnd);
	    }
}
