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
package org.activiti.designer.property;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyBpmnFilter extends AbstractPropertySectionFilter {

  @Override
  protected boolean accept(PictogramElement pe) {
    EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
    if (bo instanceof FlowElement) {
      // && !ExtensionUtil.isCustomServiceTask(bo)
      return true;
    }
    return false;
  }
}
