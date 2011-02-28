package org.activiti.designer.eclipse.deployment;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class IncludeClassesViewerFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof IPackageFragment) {
			IPackageFragment packageFragment = (IPackageFragment) element;
			if("diagrams".equalsIgnoreCase(packageFragment.getElementName()) == true) {
				return false;
			}
		}
		return true;
	}

}
