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
package org.activiti.designer.eclipse.property;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.ui.views.properties.tabbed.ITypeMapper;

public class TypeMapper implements ITypeMapper {

	@Override
	public Class<? extends Object> mapType(Object object) {

		Class<? extends Object> type = object.getClass();
		if (object instanceof EditPart) {

			Object model = ((EditPart) object).getModel();

			type = model.getClass();
			if (model instanceof PictogramElement) {
				PictogramElement pe = (PictogramElement) model;

				EObject businessObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
				if (businessObject == null) {
					return pe.getClass();
				} else {
					return businessObject.eClass().getClass();
				}
			}

		}
		return type;
	}
}