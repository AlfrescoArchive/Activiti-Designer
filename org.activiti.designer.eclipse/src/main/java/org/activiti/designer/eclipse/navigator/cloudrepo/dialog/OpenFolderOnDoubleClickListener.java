package org.activiti.designer.eclipse.navigator.cloudrepo.dialog;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

public class OpenFolderOnDoubleClickListener implements IDoubleClickListener {
	
	public void doubleClick(DoubleClickEvent event) {
    TreeViewer viewer = (TreeViewer) event.getViewer();
    IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection(); 
    Object selectedNode = thisSelection.getFirstElement(); 
    viewer.setExpandedState(selectedNode,
        !viewer.getExpandedState(selectedNode));
  }

}
