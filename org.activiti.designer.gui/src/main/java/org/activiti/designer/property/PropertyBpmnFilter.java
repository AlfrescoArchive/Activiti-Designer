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

import org.activiti.designer.bpmn2.model.BaseElement;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyBpmnFilter extends ActivitiPropertyFilter {

  @Override
  protected boolean accept(PictogramElement pe) {
  	Object bo = getBusinessObject(pe);
  	if(bo != null && bo instanceof BaseElement && 
  	        ExtensionUtil.isCustomServiceTask(bo) == false) {
  	  
      return true;
  	}
    return false;
  }
}
