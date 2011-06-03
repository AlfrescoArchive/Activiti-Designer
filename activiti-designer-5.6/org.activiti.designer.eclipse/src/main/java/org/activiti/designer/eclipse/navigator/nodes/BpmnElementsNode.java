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
package org.activiti.designer.eclipse.navigator.nodes;

import org.activiti.designer.eclipse.navigator.nodes.base.AbstractInstancesOfTypeContainerNode;
import org.activiti.designer.eclipse.util.Util;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

/**
 * EClassesNode should display the EClasses of the currently activated diagram
 * editor.
 * 
 */
public class BpmnElementsNode extends AbstractInstancesOfTypeContainerNode {
	private static final String NAME = "Bpmn2Elements";

	private ResourceSetImpl rSet;

	public BpmnElementsNode(Object parent, IProject project, Viewer viewer) {
		super(parent, project);
		rSet = new ResourceSetImpl();

	}

	@Override
	protected String getContainerName() {
		return NAME;
	}

	@Override
	public Object[] getChildren() {
		return Util.getAllBpmnElements(getProject(), rSet);
	}

	@Override
	public Image getImage() {
		return super.getImage(); // ImagePool.getImage(ImagePool.ROOT_FOLDER_FOR_IMG);
	}

	/**
	 * @return the rSet
	 */
	public ResourceSet getResourceSet() {
		return rSet;
	}
}
