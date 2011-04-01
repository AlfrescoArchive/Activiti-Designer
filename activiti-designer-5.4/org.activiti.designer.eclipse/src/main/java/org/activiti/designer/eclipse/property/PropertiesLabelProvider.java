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

import java.util.Iterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.tabbed.ITypeMapper;

/**
 * Label provider for the title bar for the tabbed property sheet page.
 */
public class PropertiesLabelProvider extends LabelProvider {

	private ITypeMapper typeMapper;

	/**
	 * constructor.
	 */
	public PropertiesLabelProvider() {
		super();
		typeMapper = new TypeMapper();
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object objects) {

		if (objects == null || objects.equals(StructuredSelection.EMPTY)) {
			return null;
		}
		final boolean multiple[] = { false };
		Object object = getObject(objects, multiple);
		if (object == null) {
			return null;
		} else {
			if (!(object instanceof EditPart)) {
				return null;
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object objects) {
		if (objects == null || objects.equals(StructuredSelection.EMPTY)) {
			return "No items selected";//$NON-NLS-1$
		}
		final boolean multiple[] = { false };
		Object object = getObject(objects, multiple);
		if (object == null || ((IStructuredSelection) objects).size() > 1) {
			return ((IStructuredSelection) objects).size() + " items selected";//$NON-NLS-1$
		} else {
			if (object instanceof EditPart) {
				object = ((EditPart) object).getModel();
			}
			if (object instanceof EObject) {
				EObject robj = (EObject) object;
				if (robj instanceof PictogramElement) {
					PictogramElement pe = (PictogramElement) robj;
					EObject businessObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
					if (businessObject == null) {
						businessObject = robj;
					}

					// return the name of the MetamodelClass
					EObject refMetaObject = businessObject.eClass();
					if (refMetaObject instanceof ENamedElement) {
						return ((ENamedElement) refMetaObject).getName();
					}
					return ""; // no luck //$NON-NLS-1$
				} else {
					return "" + ((EModelElement) robj.eClass()).getClass().getName(); //$NON-NLS-1$
				}
			} else {
				return object.toString();
			}
		}
	}

	/**
	 * Determine if a multiple object selection has been passed to the label
	 * provider. If the objects is a IStructuredSelection, see if all the
	 * objects in the selection are the same and if so, we want to provide
	 * labels for the common selected element.
	 * 
	 * @param objects
	 *            a single object or a IStructuredSelection.
	 * @param multiple
	 *            first element in the array is true if there is multiple
	 *            unequal selected elements in a IStructuredSelection.
	 * @return the object to get labels for.
	 */
	private Object getObject(Object objects, boolean multiple[]) {
		Assert.isNotNull(objects);
		Object object = null;
		if (objects instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) objects;
			object = selection.getFirstElement();
			if (selection.size() == 1) {
				// one element selected
				multiple[0] = false;
				return object;
			}
			// multiple elements selected
			multiple[0] = true;
			Class<?> firstClass = typeMapper.mapType(object);
			// determine if all the objects in the selection are the same type
			if (selection.size() > 1) {
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					Object next = i.next();
					Class<?> nextClass = typeMapper.mapType(next);
					if (!nextClass.equals(firstClass)) {
						// two elements not equal == multiple selected unequal
						multiple[0] = false;
						object = null;
						break;
					}
				}
			}
		} else {
			multiple[0] = false;
			object = objects;
		}
		return object;
	}

}