/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.eclipse.outline;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.graphiti.internal.pref.GFPreferences;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

// The generic outline uses internal functionality of Graphiti. For concrete
// tool implementations this should not be necessary 
public class ContentOutlinePageAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTERS = new Class[] { IContentOutlinePage.class };

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (GFPreferences.getInstance().isGenericOutlineActive()) {
			if (IContentOutlinePage.class.equals(adapterType)) {
				if (adaptableObject instanceof DiagramEditor) {
					DiagramEditor diagramEditor = (DiagramEditor) adaptableObject;
					if (diagramEditor.getConfigurationProvider() != null) { // diagram editor initialized?
						GraphicsEditorOutlinePage outlinePage = new GraphicsEditorOutlinePage(new TreeViewer(), diagramEditor
								.getGraphicalViewer(), diagramEditor.getActionRegistryInternal(), diagramEditor.getEditDomain(),
								diagramEditor.getCommonKeyHandler(), diagramEditor.getAdapter(ZoomManager.class), diagramEditor
										.getSelectionSynchronizerInternal(), diagramEditor);
						return outlinePage;
					}
				}
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return ADAPTERS;
	}
}
