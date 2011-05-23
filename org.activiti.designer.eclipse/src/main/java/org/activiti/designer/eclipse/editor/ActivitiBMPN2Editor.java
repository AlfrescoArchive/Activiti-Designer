package org.activiti.designer.eclipse.editor;

import java.io.File;

import org.activiti.designer.eclipse.bpmnimport.ImportBpmnUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

public class ActivitiBMPN2Editor extends StructuredTextEditor {

  @Override
  public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
    IFile associatedBPMN2File = ((IFileEditorInput) editorInput).getFile();
    final IFile diagramFile = getAssociatedDiagramIFile(associatedBPMN2File);
    if(diagramFile.exists() == false) {
      String bpmnFile = associatedBPMN2File.getRawLocation().toFile().getAbsolutePath();
      String processName = bpmnFile.substring(bpmnFile.lastIndexOf(File.separator) + 1);
      processName = processName.replace(".xml", "");
      processName = processName.replace(".bpmn20", "");
      ImportBpmnUtil.createDiagram(processName, bpmnFile, 
              associatedBPMN2File.getProject(), associatedBPMN2File.getParent());
    }
    super.init(site, editorInput);
  }
  
  private IFile getAssociatedDiagramIFile(final IFile bpmnFile) {

    IPath path = getAssociatedDiagramURI(bpmnFile);

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IFile diagramFile = workspace.getRoot().getFile(path);

    return diagramFile;
  }

  private IPath getAssociatedDiagramURI(final IFile bpmnFile) {

    final IPath originalPath = bpmnFile.getFullPath();

    final IPath parentPath = originalPath.removeLastSegments(1);

    String finalSegment = originalPath.lastSegment();
    finalSegment = finalSegment.replace(".bpmn20.xml", ".activiti");
    final IPath returnPath = parentPath.append(new Path(finalSegment));
    return returnPath;
  } 
}
