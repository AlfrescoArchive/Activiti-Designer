package org.activiti.designer.eclipse.editor;

import org.activiti.designer.eclipse.util.FileService;
import org.eclipse.core.resources.IFile;
import org.eclipse.graphiti.ui.editor.DiagramEditorMatchingStrategy;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

public class ActivitiDiagramMatchingStrategy implements IEditorMatchingStrategy {

  @Override
  public boolean matches(final IEditorReference editorRef, final IEditorInput input) {

    try {
      final IFile newDataFile = FileService.getDataFileForInput(input);
      final IFile openEditorDataFile = FileService.getDataFileForInput(editorRef.getEditorInput());

      if (null != newDataFile && newDataFile.equals(openEditorDataFile)) {
        return true;
      }
    } catch (PartInitException exception) {
      exception.printStackTrace();
    }

    return new DiagramEditorMatchingStrategy().matches(editorRef, input);
  }
}
