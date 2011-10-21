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
package org.activiti.designer.eclipse.util.uiprovider;

import org.activiti.designer.eclipse.util.Util;

/**
 * A simple container, which just wraps two objects. Its main purpose is to overwrite equals() and hashCode() accordingly.
 */
public class TwoObjectsContainer {

	private Object _one;

	private Object _two;

	public TwoObjectsContainer(Object one, Object two) {
		_one = one;
		_two = two;
	}

	public final Object getOne() {
		return _one;
	}

	public final Object getTwo() {
		return _two;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) // quick check
			return true;
		if (!(o instanceof TwoObjectsContainer))
			return false;
		TwoObjectsContainer other = (TwoObjectsContainer) o;
		if (Util.equalsWithNull(other.getOne(), getOne()) && Util.equalsWithNull(other.getTwo(), getTwo()))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		if (getOne() != null)
			hashCode ^= getOne().hashCode();
		if (getTwo() != null)
			hashCode ^= getTwo().hashCode();
		return hashCode;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + getOne() + ", " + getTwo() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
