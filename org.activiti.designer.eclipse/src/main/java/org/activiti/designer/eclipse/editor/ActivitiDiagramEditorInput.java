package org.activiti.designer.eclipse.editor;

import org.activiti.bpmn.model.SubProcess;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;

public class ActivitiDiagramEditorInput extends DiagramEditorInput {

  private IFile diagramFile;
  private IFile dataFile;
  private ActivitiDiagramEditor parentEditor;
  private SubProcess subprocess;

  public ActivitiDiagramEditorInput(URI diagramUri, String providerId) {
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

  public ActivitiDiagramEditor getParentEditor() {
    return parentEditor;
  }

  public void setParentEditor(ActivitiDiagramEditor parentEditor) {
    this.parentEditor = parentEditor;
  }

  public SubProcess getSubprocess() {
    return subprocess;
  }

  public void setSubprocess(SubProcess subprocess) {
    this.subprocess = subprocess;
  }

  @Override
  public boolean equals(Object obj) {
    boolean result = false;

    if (obj == null) {
      return result;
    }

    if (obj instanceof ActivitiDiagramEditorInput) {
      final ActivitiDiagramEditorInput otherInput = (ActivitiDiagramEditorInput) obj;

      if (diagramFile.equals(otherInput.diagramFile)) {
        result = true;
      }
    }

    return result;
  }
}
