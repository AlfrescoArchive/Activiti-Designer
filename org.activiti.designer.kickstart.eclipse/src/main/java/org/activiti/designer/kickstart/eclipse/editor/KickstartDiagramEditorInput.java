package org.activiti.designer.kickstart.eclipse.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;

public class KickstartDiagramEditorInput extends DiagramEditorInput {

  private IFile diagramFile;
  private IFile dataFile;

  public KickstartDiagramEditorInput(URI diagramUri, String providerId) {
    super(diagramUri, providerId);
  }

  public IFile getDiagramFile() {
    return diagramFile;
  }

  public void setDiagramFile(IFile diagramFileName) {
    this.diagramFile = diagramFileName;
  }

  public IFile getDataFile() {
    return dataFile;
  }

  public void setDataFile(IFile dataFileName) {
    this.dataFile = dataFileName;
  }

  @Override
  public boolean equals(Object obj) {
    boolean result = false;

    if (obj == null) {
      return result;
    }

    if (obj instanceof KickstartDiagramEditorInput) {
      final KickstartDiagramEditorInput otherInput = (KickstartDiagramEditorInput) obj;

      if (diagramFile.equals(otherInput.diagramFile)) {
        result = true;
      }
    }

    return result;
  }
}
