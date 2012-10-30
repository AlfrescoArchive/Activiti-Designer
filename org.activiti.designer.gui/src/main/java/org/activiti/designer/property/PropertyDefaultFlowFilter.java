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

import java.util.List;

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.activiti.designer.bpmn2.model.InclusiveGateway;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyDefaultFlowFilter extends ActivitiPropertyFilter {

  @Override
  protected boolean accept(PictogramElement pe) {
  	Object bo = getBusinessObject(pe);
  	 if (bo instanceof Activity || bo instanceof ExclusiveGateway ||
             bo instanceof InclusiveGateway) {
       
       List<SequenceFlow> flowList = null;
       if(bo instanceof Activity) {
         flowList = ((Activity) bo).getOutgoing();
       } else if(bo instanceof ExclusiveGateway) {
         flowList = ((ExclusiveGateway) bo).getOutgoing();
       } else {
         flowList = ((InclusiveGateway) bo).getOutgoing();
       }
       if(flowList != null && flowList.size() > 1) {
         return true;
       }
  	 }
    return false;
  }
}
