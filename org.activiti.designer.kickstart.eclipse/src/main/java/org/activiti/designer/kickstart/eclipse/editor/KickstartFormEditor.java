package org.activiti.designer.kickstart.eclipse.editor;

import org.activiti.designer.kickstart.eclipse.ui.ActivitiEditorContextMenuProvider;
import org.activiti.designer.kickstart.eclipse.util.FileService;
import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class KickstartFormEditor extends DiagramEditor {

  private static GraphicalViewer activeGraphicalViewer;

  @Override
  protected ContextMenuProvider createContextMenuProvider() {
    return new ActivitiEditorContextMenuProvider(getGraphicalViewer(), getActionRegistry(), getDiagramTypeProvider());
  }

  public static GraphicalViewer getActiveGraphicalViewer() {
    return activeGraphicalViewer;
  }

  @Override
  public void dispose() {
    super.dispose();

    final KickstartDiagramEditorInput adei = (KickstartDiagramEditorInput) getEditorInput();

    ModelHandler.removeModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()));
    KickstartProcessDiagramCreator.dispose(adei.getDiagramFile());
  }
  
  @Override
  public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    IEditorInput finalInput = null;

    try {
      if (input instanceof KickstartDiagramEditorInput) {
        // No need to wrap, already using the right type of input
        finalInput = input;
      } else {
        // Wrap in a KickstartDiagramEditorInput and use that instead
        finalInput = createNewDiagramEditorInput(input);
      }
    } catch (CoreException exception) {
      exception.printStackTrace();
    }

    super.init(site, finalInput);
  }
  
  @Override
  protected void setInput(IEditorInput input) {
    super.setInput(input);
    
    final KickstartDiagramEditorInput adei = (KickstartDiagramEditorInput) input;
    final IFile dataFile = adei.getDataFile();

    final KickstartFormMemoryModel model = new KickstartFormMemoryModel(getDiagramTypeProvider().getFeatureProvider(), dataFile);
    ModelHandler.addModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()), model);
    
    // TODO: read form-definition from data-file and set on model
  }

  private KickstartDiagramEditorInput createNewDiagramEditorInput(final IEditorInput input) throws CoreException {

    final IFile dataFile = FileService.getDataFileForInput(input);

    // now generate the temporary diagram file
    final IPath dataFilePath = dataFile.getFullPath();

    // get or create the corresponding temporary folder
    final IFolder tempFolder = FileService.getOrCreateTempFolder(dataFilePath);

    // finally get the diagram file that corresponds to the data file
    final IFile diagramFile = FileService.getTemporaryDiagramFile(dataFilePath, tempFolder);

    // Create new temporary diagram file
    return new KickstartFormDiagramCreator().createFormDiagram(dataFile, diagramFile, this);
  }
  
  
}
