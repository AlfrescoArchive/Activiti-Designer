package org.activiti.designer.eclipse.editor;

import java.util.List;

import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.internal.util.T;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.graphiti.ui.internal.util.ReflectionUtil;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

public class ActivitiDiagramMatchingStrategy implements IEditorMatchingStrategy {

	public boolean matches(IEditorReference editorRef, IEditorInput input) {
		try {
			IFile file = ReflectionUtil.getFile(input);
			if (file != null) {
				// check whether the given input comes with a file which is already
				// opened in the diagram editor.
				List<String> modelURIList = ModelHandler.getModelURIList();
				for(String fileURI : modelURIList) {
					if(fileURI.equals(file.getRawLocationURI().toString())) {
						return true;
					}
				}
			} else if (input instanceof URIEditorInput) {
				final URIEditorInput uriInput = (URIEditorInput) input;
				URI uri = uriInput.getURI();
				uri = GraphitiUiInternal.getEmfService().mapDiagramFileUriToDiagramUri(
				    uri);

				// Check whether the given file contains a diagram as its
				// root element. If yes, compare it with the given editor's
				// diagram.
				final IEditorInput editorInput = editorRef.getEditorInput();
				if (editorInput instanceof DiagramEditorInput) {
					final DiagramEditorInput diagInput = (DiagramEditorInput) editorInput;

					// We do not compare diagram object but diagram files
					// only.
					// Reason is that if editorRef points to a not yet
					// realized editor, its input's diagram is null (not yet
					// created), thus we can only get its diagram file.
					final String uriString = diagInput.getUriString();
					final URI diagramUri = URI.createURI(uriString);
					if (diagramUri != null) {
						if (uri.equals(diagramUri)) {
							return true;
						}
					}
				}
			} else if (input instanceof DiagramEditorInput) {
				// normal case: check for input equality
				final IEditorInput editorInput = editorRef.getEditorInput();
				if (input.equals(editorInput)) {
					return true;
				}
			}
		} catch (final PartInitException e) {
			T.racer().error(e.getMessage(), e);
		}
		return false;
	}
}
