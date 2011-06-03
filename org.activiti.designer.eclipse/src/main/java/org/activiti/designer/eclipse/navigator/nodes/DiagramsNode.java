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

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.navigator.nodes.base.AbstractInstancesOfTypeContainerNode;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class DiagramsNode extends AbstractInstancesOfTypeContainerNode {

	private static final String NAME = "Diagrams";

	public DiagramsNode(Object parent, IProject project) {
		super(parent, project);
	}

	@Override
	protected String getContainerName() {
		return NAME;
	}

	@Override
	public Object[] getChildren() {
		IProject project = getProject();
		if (project != null) {
			ResourceSet rSet = new ResourceSetImpl();
			return getAllDiagramFiles(project, rSet).toArray();
		}
		return null;
	}

	private List<IFile> getFiles(IContainer folder) {
		List<IFile> ret = new ArrayList<IFile>();
		try {
			IResource[] members = folder.members();
			for (IResource resource : members) {
				if (resource instanceof IContainer) {
					ret.addAll(getFiles((IContainer) resource));
				} else if (resource instanceof IFile) {
					IFile file = (IFile) resource;
					if (file.getName().endsWith(ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION)) { //$NON-NLS-1$
						ret.add(file);
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private List<IFile> getAllDiagramFiles(IProject project, ResourceSet rSet) {
		List<IFile> files = getFiles(project);

		List<IFile> ret = new ArrayList<IFile>();
		for (IFile file : files) {
			// The following call extracts the diagram from the
			// given file. For the Tutorial, diagrams always reside
			// in a file of their own and are the first root object.
			// This may of course be different in a concrete tool
			// implementation, so tool builders should use their own
			// way of retrieval here
			Diagram diagram = org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal.getEmfService()
					.getDiagramFromFile(file, rSet);
			if (diagram != null) {
				ret.add(file);
			}
		}
		return ret;
	}
}
