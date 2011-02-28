package org.activiti.designer.property;

import org.activiti.designer.eclipse.editor.ActivitiMultiPageEditor;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.ui.internal.editor.DiagramEditorInternal;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IContributedContentsView;

public abstract class ActivitiPropertySection extends GFPropertySection {
	
	/**
	 * @return the {@link IDiagramEditor} diagram editor.
	 */
	protected IDiagramEditor getDiagramEditor() {
		IWorkbenchPart part = getPart();
		if (part instanceof ActivitiMultiPageEditor) {
			return ((ActivitiMultiPageEditor)part).getActivitiDiagramEditor();
		}
		if (part instanceof DiagramEditorInternal) {
			return (DiagramEditorInternal) part;
		}
		IContributedContentsView contributedView = (IContributedContentsView) part
				.getAdapter(IContributedContentsView.class);
		if (contributedView != null) {
			part = contributedView.getContributingPart();
		}
		if (part instanceof DiagramEditorInternal) {
			return (DiagramEditorInternal) part;
		}

		return null;
	}

}
