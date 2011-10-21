package org.activiti.designer.eclipse.deployment;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Tree;

public class IncludeInDeploymentTreeViewer extends CheckboxTreeViewer {

	public IncludeInDeploymentTreeViewer(Tree tree) {
		super(tree);
		addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				handleCheckStateChanged(event);				
			}			
		});
	}
	
	private void handleCheckStateChanged(CheckStateChangedEvent event) {
		updateChecks(event.getElement(), event.getChecked());
	}

	public boolean setChecked(final Object element, final boolean state) {
		boolean result = super.setChecked(element, state);
		if (result) {
			updateChecks(element, state);
		}				
		return result;
	}
	
	public void setCheckedElements(final Object[] elements) {
		if(elements == null) return;
		super.setCheckedElements(elements);
		for (int i = 0; i < elements.length; i++) {
			updateChecks(elements[i], true);
		}
	}
	
	private void updateChecks(Object object, boolean state) {
		updateChecksForChildren(object, state);
		updateChecksForParents(object, state);
	}
	
	private void updateChecksForChildren(Object object, boolean state) {
		setGrayed(object, false);
		Object[] children = ((ITreeContentProvider)getContentProvider()).getChildren(object);
		for (int i = 0; i < children.length; i++) {
			if (getChecked(children[i]) != state) {
				super.setChecked(children[i], state);
				updateChecksForChildren(children[i], state);
			}
		}
	}
	
	private void updateChecksForParents(Object object, boolean state) {
		ITreeContentProvider provider = (ITreeContentProvider)getContentProvider();
		Object child = object;
		Object parent = provider.getParent(child);
		boolean change = true;
		while (parent != null && change) {
			Object[] siblings = provider.getChildren(parent);
			int numberChecked = 0;
			boolean grayed = false;
			change = false;
			for (int i = 0; i < siblings.length; i++) {
				if (getChecked(siblings[i])) numberChecked++;
				if (getGrayed(siblings[i])) grayed = true;
			}
			if (numberChecked == 0) {
				if (getChecked(parent) || getGrayed(parent)) change = true;
				setGrayChecked(parent, false);
			}
			else if (numberChecked == siblings.length) {
				if (!getChecked(parent) || getGrayed(parent) != grayed) change = true;
				setGrayed(parent, false);
				setChecked(parent, true);
			}
			else {
				if (!getChecked(parent) || !getGrayed(parent)) change = true;
				setGrayChecked(parent, true);
			}
			child = parent;
			parent = provider.getParent(child);
		}
		
	}
}