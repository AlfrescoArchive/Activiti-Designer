package org.activiti.designer.eclipse.deployment;

import org.activiti.designer.eclipse.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;

public class IncludeFilesTreeContentProvider implements ITreeContentProvider {

	public Object[] getElements(Object parent) {
		if (parent instanceof IFolder) {
			try {
				return ((IFolder) parent).members();
			} catch (CoreException e) {
				Logger.logError(e);
			}
		}
		return new Object[0];
	}

	public Object[] getChildren(Object parent) {
		try {
			if (parent instanceof IFolder) {
				return ((IFolder) parent).members();
			}
		} catch (CoreException e) {
			Logger.logError(e);
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
		if (element != null && element instanceof IResource) {
			return ((IResource) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IFolder)
			return getChildren(element).length > 0;
		return false;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		asyncRefresh(viewer);
	}

	private void asyncRefresh(final Viewer viewer) {
		Control control = viewer.getControl();
		if (!control.isDisposed()) {
			control.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (!viewer.getControl().isDisposed()) {
						viewer.refresh();
					}
				}
			});
		}
	}
	
}
