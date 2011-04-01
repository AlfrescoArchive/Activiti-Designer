package org.activiti.designer.eclipse.deployment;

import java.util.ArrayList;
import java.util.Iterator;

import org.activiti.designer.eclipse.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;

public class IncludeClassesTreeContentProvider implements ITreeContentProvider {

	public Object[] getElements(Object parent) {
		if (parent instanceof IJavaProject) {
			return getPackageFragmentRoots((IJavaProject)parent);
		}
		return new Object[0];
	}
	
	private IPackageFragmentRoot[] getPackageFragmentRoots(IJavaProject project) {
		try {
			ArrayList<IPackageFragmentRoot> list = new ArrayList<IPackageFragmentRoot>();
			IClasspathEntry[] entries = project.getRawClasspath();
			for (int i = 0; i < entries.length; i++) {
				if (entries[i].getEntryKind() != IClasspathEntry.CPE_CONTAINER && entries[i].getEntryKind() != IClasspathEntry.CPE_VARIABLE) {
					IPackageFragmentRoot[] roots = project.findPackageFragmentRoots(entries[i]);
					for (int j = 0; j < roots.length; j++) {
						list.add(roots[j]);
					}
				}
			}
			Iterator<IPackageFragmentRoot> itPackage = list.iterator();
			while(itPackage.hasNext()) {
				IPackageFragmentRoot packageRoot = itPackage.next();
				if(packageRoot.getPath().toOSString().contains("src/test")) {
					itPackage.remove();
				}
			}
			return (IPackageFragmentRoot[])list.toArray(new IPackageFragmentRoot[list.size()]);
		}
		catch (JavaModelException e) {
			Logger.logError(e);
		}
		return new IPackageFragmentRoot[0];
	}
	
	public Object[] getChildren(Object parent) {
		ArrayList list = new ArrayList();
		try {
			if (parent instanceof IPackageFragmentRoot) {
				list.addAll(toArrayList(((IPackageFragmentRoot)parent).getNonJavaResources()));
				list.addAll(getNonEmptyPackageFragments((IPackageFragmentRoot) parent));
			} else if (parent instanceof IPackageFragment) {
				list.addAll(toArrayList(((IPackageFragment)parent).getNonJavaResources()));
				list.addAll(toArrayList(((IPackageFragment)parent).getChildren()));
			}
		} catch (JavaModelException e) {
			Logger.logError(e);
		}
		return list.toArray();
	}
	
	private ArrayList toArrayList(Object[] resources){
		ArrayList list = new ArrayList();
		for (int i = 0; i < resources.length; i++) {
			list.add(resources[i]);
		}
		return list;
	}
	
	private ArrayList getNonEmptyPackageFragments(IPackageFragmentRoot parent) {
		ArrayList list = new ArrayList();
		try {
			Object[] children = parent.getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i] instanceof IPackageFragment && hasChildren(children, i)) {
					list.add(children[i]);
				}
			}
		} catch (JavaModelException e) {
			Logger.logError(e);
		}
		return list;
	}

	private boolean hasChildren(Object[] children, int i) throws JavaModelException {
		return (((IPackageFragment)children[i]).getChildren().length != 0) 
			|| (((IPackageFragment)children[i]).getNonJavaResources().length != 0);
	}

	public Object getParent(Object element) {
		if (element == null) {
			return null;
		} else if (element instanceof IJavaElement) {
			return ((IJavaElement)element).getParent();
		} else if (element instanceof IResource) {
			IJavaElement javaElement = JavaCore.create(((IResource) element).getParent());
			if (javaElement != null) {
				return javaElement;
			} else {
				return ((IResource)element).getParent();
			}
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IPackageFragmentRoot) {
			return getChildren(element).length > 0;
		} else if (element instanceof IPackageFragment) {
			return getChildren(element).length > 0;
		}
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
