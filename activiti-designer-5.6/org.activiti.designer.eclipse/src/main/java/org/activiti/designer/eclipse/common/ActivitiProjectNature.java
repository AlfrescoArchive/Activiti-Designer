package org.activiti.designer.eclipse.common;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class ActivitiProjectNature implements IProjectNature {

	public static final String NATURE_ID = "org.activiti.designer.nature";

	IProject p;

	@Override
	public void configure() throws CoreException {

	}

	@Override
	public void deconfigure() throws CoreException {

	}

	@Override
	public IProject getProject() {
		return p;
	}

	@Override
	public void setProject(IProject project) {
		p = project;

	}

}
