package org.activiti.designer.eclipse.ui;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.ui.editor.DiagramEditorContextMenuProvider;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Point;

public class ActivitiEditorContextMenuProvider extends DiagramEditorContextMenuProvider {
	public ActivitiEditorContextMenuProvider(EditPartViewer viewer, 
										ActionRegistry registry, 
										IDiagramTypeProvider diagramTypeProvider) {
		super(viewer, registry, diagramTypeProvider);
	}
		

	protected void addDefaultMenuGroupEdit(IMenuManager manager, Point menuLocation) {
	}
	
	protected void addActionToMenuIfAvailable(IMenuManager manager, String actionId, String menuGroup) {
		//super.addActionToMenuIfAvailable(manager, actionId, menuGroup);
	}
	
	
}
