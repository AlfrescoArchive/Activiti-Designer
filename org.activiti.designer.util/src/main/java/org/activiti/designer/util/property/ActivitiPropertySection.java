package org.activiti.designer.util.property;

import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IContributedContentsView;

public abstract class ActivitiPropertySection extends GFPropertySection {
	
	/**
	 * @return the {@link IDiagramEditor} diagram editor.
	 */
	protected IDiagramEditor getDiagramEditor() {
		IWorkbenchPart part = getPart();
		if (part instanceof IContributedContentsView) {
		  IContributedContentsView contributedView = (IContributedContentsView) part
          .getAdapter(IContributedContentsView.class);
      if (contributedView != null) {
        part = contributedView.getContributingPart();
      }
		}
		
		if (part instanceof IDiagramEditor) {
			return (IDiagramEditor) part;
		}
		return null;
	}

}
