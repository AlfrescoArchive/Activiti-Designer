package org.activiti.designer.eclipse.editor;

import java.util.List;

import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.graphiti.ui.editor.DiagramEditorMatchingStrategy;
import org.eclipse.graphiti.ui.internal.util.ReflectionUtil;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;

public class ActivitiDiagramMatchingStrategy implements IEditorMatchingStrategy {

	public boolean matches(IEditorReference editorRef, IEditorInput input) {
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
		} else {
			return new DiagramEditorMatchingStrategy().matches(editorRef, input);
		}
		return false;
	}
}
