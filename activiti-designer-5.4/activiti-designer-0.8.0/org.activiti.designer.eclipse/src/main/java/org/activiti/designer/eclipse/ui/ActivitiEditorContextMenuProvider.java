package org.activiti.designer.eclipse.ui;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.graphiti.ui.editor.DiagramEditorContextMenuProvider;
import org.eclipse.graphiti.ui.internal.config.IConfigurationProvider;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Point;

public class ActivitiEditorContextMenuProvider extends DiagramEditorContextMenuProvider {
	public ActivitiEditorContextMenuProvider(EditPartViewer viewer, 
										ActionRegistry registry, 
										IConfigurationProvider configurationProvider) {
		super(viewer, registry, configurationProvider);
	}
		

	protected void addDefaultMenuGroupEdit(IMenuManager manager, Point menuLocation) {
	}
	
	protected void addActionToMenuIfAvailable(IMenuManager manager, String actionId, String menuGroup) {
		//super.addActionToMenuIfAvailable(manager, actionId, menuGroup);
	}
	
	
}
