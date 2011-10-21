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
/*
 * Created on 28.06.2005
 */
package org.activiti.designer.eclipse.outline.tree;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.designer.eclipse.util.uiprovider.TwoObjectsContainer;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * 
 */
public class AbstractGraphicsTreeEditPart extends AbstractTreeEditPart {

	private Map<TwoObjectsContainer, Image> _imageRegistry = new HashMap<TwoObjectsContainer, Image>(); // ImageDescriptor

	/**
	 * The Constant TEXT_TYPE_DEFAULT.
	 */
	public static final String TEXT_TYPE_DEFAULT = null;

	/**
	 * The Constant ARRAY_TEXT_START.
	 */
	public static final String ARRAY_TEXT_START = "["; //$NON-NLS-1$

	/**
	 * The Constant ARRAY_TEXT_END.
	 */
	public static final String ARRAY_TEXT_END = "]"; //$NON-NLS-1$

	/**
	 * The Constant ARRAY_TEXT_SEPARATOR.
	 */
	public static final String ARRAY_TEXT_SEPARATOR = "; "; //$NON-NLS-1$

	/**
	 * The Constant FORMAT_JAVA_SQL_DATE.
	 */
	public static final DateFormat FORMAT_JAVA_SQL_DATE = DateFormat.getDateInstance(DateFormat.SHORT);

	/**
	 * The Constant FORMAT_JAVA_SQL_TIME.
	 */
	public static final DateFormat FORMAT_JAVA_SQL_TIME = DateFormat.getTimeInstance(DateFormat.SHORT);

	/**
	 * The Constant FORMAT_JAVA_UTIL_DATE.
	 */
	public static final DateFormat FORMAT_JAVA_UTIL_DATE = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

	/**
	 * The Constant TUPLE_TEXT_END.
	 */
	public static final String TUPLE_TEXT_END = ")"; //$NON-NLS-1$

	/**
	 * The Constant TUPLE_TEXT_SEPARATOR.
	 */
	public static final String TUPLE_TEXT_SEPARATOR = ", "; //$NON-NLS-1$

	/**
	 * The Constant TUPLE_TEXT_START.
	 */
	public static final String TUPLE_TEXT_START = "("; //$NON-NLS-1$


	/**
	 * @param model
	 */
	public AbstractGraphicsTreeEditPart(Object model) {
		super(model);
	}

	/**
	 * This method is called from refreshVisuals(), to display the image of the
	 * TreeItem.
	 * <p>
	 * By default this method displays the image of the FIRST attribute of the
	 * ModelObject as the TreeItem.
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected Image getImage() {
		Image result = getImage(getModel(), null);
		return result;
	}

	/**
	 * This method is called by refreshVisuals(), to display the text of the
	 * TreeItem.
	 * <p>
	 * By default this method displays the FIRST attribute of the model Object
	 * as the TreeItem.
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected String getText() {
		String text = getText(getModel(), TEXT_TYPE_DEFAULT);
		return (text == null) ? "" : text; //$NON-NLS-1$
	}

	/**
	 * Adds the all elements if not null.
	 * 
	 * @param resultList
	 *            the result list
	 * @param collection
	 *            the collection
	 */
	protected void addAllElementsIfNotNull(List<Object> resultList, Collection<?> collection) {
		for (Object element : collection) {
			if (element != null) {
				resultList.add(element);
			}
		}
	}

	private String getText(Object element, Object textType) {
		// get inner objects from 'own' wrappers
		// if (element instanceof PropertyForAttribute) {
		// PropertyForAttribute casted = (PropertyForAttribute) element;
		// element = casted.getValue();
		// }
		// if (element instanceof PropertyTypeForAttribute) {
		// PropertyTypeForAttribute casted = (PropertyTypeForAttribute) element;
		// element = casted.getAttribute();
		// }

		// // special handlings
		// if (element instanceof Attribute) {
		// Attribute casted = (Attribute) element;
		// if (Util.equalsWithNull(textType, TEXT_TYPE_DEFAULT) ||
		// Util.equalsWithNull(textType, TEXT_TYPE_NAME))
		// return casted.getName();
		// if (Util.equalsWithNull(textType, TEXT_TYPE_TOOLTIP))
		// return "The attribute" + " " + casted.getName();
		// if (Util.equalsWithNull(textType, TEXT_TYPE_PROPERTYSHEET_CATEGORY))
		// {
		// if
		// (casted.refOutermostComposite().refGetValue("name").equals("pictograms"))
		// {
		// return "Graphics Extension";
		// } else {
		// return "Business Model";
		// }
		// }
		// }

		if (element instanceof EObject) {
			EObject casted = (EObject) element;
			if (casted != null && casted.eResource() != null) {
				final EObject eObject = casted.eClass();
				if (eObject instanceof EClass && eObject.eResource() != null) {
					EClass eClass = (EClass) eObject;
					String className = eClass.getName();
					return className + " (" + casted.toString() + ")";
				}
			}
		}

		if (element == null)
			return ""; //$NON-NLS-1$

		// Collection: convert to array and call recursively
		if (element instanceof Collection<?>) {
			Collection<?> collection = (Collection<?>) element;
			return getText(collection.toArray(), textType);
		}

		// Array: build comma-separated List with recursive calls
		if (element.getClass().isArray()) {
			StringBuffer result = new StringBuffer();
			result.append(ARRAY_TEXT_START);
			boolean afterFirstElement = false;
			for (int i = 0; i < Array.getLength(element); i++) {
				if (afterFirstElement)
					result.append(ARRAY_TEXT_SEPARATOR);
				Object next = Array.get(element, i); // this automatically
				// converts primitive
				// types to Object types
				result.append(getText(next, textType));
				afterFirstElement = true;
			}
			result.append(ARRAY_TEXT_END);
			return result.toString();
		}

		// Date/Time: use DateFormatter
		if (element instanceof java.sql.Date)
			return FORMAT_JAVA_SQL_DATE.format((java.sql.Date) element);
		if (element instanceof java.sql.Time)
			return FORMAT_JAVA_SQL_TIME.format((java.sql.Time) element);
		if (element instanceof java.util.Date) // java.uti.Date must be the
			// last, because the java.sql.*
			// extend this class
			return FORMAT_JAVA_UTIL_DATE.format((java.util.Date) element);

		// Rectangle/Point
		if (element instanceof Rectangle) {
			Rectangle casted = (Rectangle) element;
			StringBuffer result = new StringBuffer();
			result.append(TUPLE_TEXT_START);
			result.append(casted.x).append(TUPLE_TEXT_SEPARATOR).append(casted.y).append(TUPLE_TEXT_SEPARATOR).append(casted.width)
					.append(TUPLE_TEXT_SEPARATOR).append(casted.height);
			result.append(TUPLE_TEXT_END);
			return result.toString();
		}
		if (element instanceof Point) {
			Point casted = (Point) element;
			StringBuffer result = new StringBuffer();
			result.append(TUPLE_TEXT_START);
			result.append(casted.x).append(TUPLE_TEXT_SEPARATOR).append(casted.y);
			result.append(TUPLE_TEXT_END);
			return result.toString();
		}

		// default
		return element.toString();
	}

	private Image getImage(Object element, Object imageType) {
		if (element != null) {
			ImageDescriptor descriptor = getImageDescriptor(element, imageType);
			TwoObjectsContainer container = new TwoObjectsContainer(descriptor, imageType);
			if (descriptor != null) {
				Image image = getImageRegistry().get(container);
				if (image == null) {
					image = descriptor.createImage();
					getImageRegistry().put(container, image);
				}
				return image;
			}
		}
		return null;
	}

	private ImageDescriptor getImageDescriptor(Object element, Object imageType) {
		return null;
	}

	private Map<TwoObjectsContainer, Image> getImageRegistry() {
		return _imageRegistry;
	}
}