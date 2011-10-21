package org.activiti.designer.eclipse.deployment;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class IncludeFilesViewerFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof IResource) {
			IResource resource = (IResource) element;
			if("xml".equalsIgnoreCase(resource.getFileExtension())) {
				return true;
			}
		}
		return false;
	}

}
