/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
