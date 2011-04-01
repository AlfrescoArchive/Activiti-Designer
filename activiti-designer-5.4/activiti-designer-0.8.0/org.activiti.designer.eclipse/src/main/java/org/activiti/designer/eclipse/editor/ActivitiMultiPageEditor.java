package org.activiti.designer.eclipse.editor;

import java.io.File;

import org.activiti.designer.eclipse.bpmnimport.ImportBpmnUtil;
import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.editor.sync.DiagramUpdater;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditorFactory;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * @author Yvo Swillens
 * @since 0.6.1
 * @version 1
 * 
 */
public class ActivitiMultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener {

  private static final String DIAGRAM_PANE_TILE = "Diagram";
  private static final String XML_PANE_TITLE = "BPMN2.0";

  /** The diagram editor used in page 0. */
  private ActivitiDiagramEditor diagramEditor;

  /** The XML editor used in page 1. */
  private StructuredTextEditor bpmnEditor;

  private IFile associatedBPMN2File;

  /**
   * Creates a multi-page editor example.
   */
  public ActivitiMultiPageEditor() {
    super();
    ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
  }

  private void createDiagramPage() {

    try {
      diagramEditor = new ActivitiDiagramEditor();
      int index = addPage(diagramEditor, getEditorInput());
      setPageText(index, ActivitiMultiPageEditor.DIAGRAM_PANE_TILE);
    } catch (PartInitException e) {
      ErrorDialog.openError(getSite().getShell(), "Error creating nested Activiti Diagram editor", null, e.getStatus());
    }
  }

  private void createBPMN2Page() {
    try {
      bpmnEditor = new StructuredTextEditor();
      int index = addPage(bpmnEditor, getBPMN2EditorInput());
      setPageText(index, ActivitiMultiPageEditor.XML_PANE_TITLE);
    } catch (PartInitException e) {
      ErrorDialog.openError(getSite().getShell(), "Error creating nested Activiti BPMN2.0 editor", null, e.getStatus());
    }
  }
  /**
   * Creates the pages of the multi-page editor.
   */
  protected void createPages() {
    createDiagramPage();
    createBPMN2Page();
  }

  public void dispose() {
    ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    super.dispose();
  }

  public void doSave(IProgressMonitor monitor) {
    int activePage = getActivePage();
    if (activePage == 0) {
      getEditor(0).doSave(monitor);
    } else if (activePage == 1) {
      
      // Save BPMN editor contents
      getEditor(1).doSave(monitor);

      // sync Activiti Diagram
      DiagramEditorInput diagramEditorInput = (DiagramEditorInput) getEditor(0).getEditorInput();
      Diagram diagram = diagramEditorInput.getDiagram();
      FileEditorInput bpmn2EditorInput = (FileEditorInput) getEditor(1).getEditorInput();
      
      IStorage bpmnStorage = bpmn2EditorInput.getStorage();
      DiagramUpdater.syncDiagram(diagramEditor, diagram, bpmnStorage);

      // Save BPMN editor contents
      getEditor(0).doSave(monitor);
    }
  }

  public void doSaveAs() {
    int activePage = getActivePage();
    if (activePage == 0) {
      IEditorPart editor = getEditor(0);
      editor.doSaveAs();
      setPageText(0, editor.getTitle());
      setInput(editor.getEditorInput());
    } else if (activePage == 1) {
      IEditorPart editor = getEditor(1);
      editor.doSaveAs();
      setPageText(1, editor.getTitle());
      setInput(editor.getEditorInput());
    }
  }

  public void gotoMarker(IMarker marker) {
    setActivePage(0);
    IDE.gotoMarker(getEditor(0), marker);
  }

  public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
    if (!(editorInput instanceof IFileEditorInput) && !(editorInput instanceof DiagramEditorInput))
      throw new PartInitException("Invalid Input: Must be Activiti Diagram or BPMN2.0 XML");

    // checks if the editor was opened with non Diagram file
    if (editorInput instanceof IFileEditorInput) {

      if (isBPM2FileType((IFileEditorInput) editorInput)) {
        associatedBPMN2File = ((IFileEditorInput) editorInput).getFile();
        final IFile diagramFile = getAssociatedDiagramIFile(associatedBPMN2File);
        if(diagramFile.exists() == false) {
          String bpmnFile = associatedBPMN2File.getRawLocation().toFile().getAbsolutePath();
          String processName = bpmnFile.substring(bpmnFile.lastIndexOf(File.separator) + 1);
          processName = processName.replace(".xml", "");
          processName = processName.replace(".bpmn20", "");
          ImportBpmnUtil.createDiagram(processName, bpmnFile, associatedBPMN2File.getProject());
        }
        final IEditorInput diagramFileEditorInput = new FileEditorInput(diagramFile);

        // creates DiagramEditorInput from FileEditorInput
        editorInput = new DiagramEditorFactory().createEditorInput(diagramFileEditorInput);
      }
    }
    super.init(site, editorInput);
  }

  public boolean isSaveAsAllowed() {
    return true;
  }

  public ActivitiDiagramEditor getActivitiDiagramEditor() {
    IEditorPart editor = getEditor(0);
    return (ActivitiDiagramEditor) editor;
  }

  /**
   * Closes all project files on project close.
   */
  public void resourceChanged(final IResourceChangeEvent event) {
    if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
      Display.getDefault().asyncExec(new Runnable() {

        public void run() {
          IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
          for (int i = 0; i < pages.length; i++) {
            if (((FileEditorInput) bpmnEditor.getEditorInput()).getFile().getProject().equals(event.getResource())) {
              IEditorPart editorPart = pages[i].findEditor(bpmnEditor.getEditorInput());
              pages[i].closeEditor(editorPart, true);
            }
          }
        }
      });
    }
  }

  /**
   * Reloads contents of BPMN2 pane when selected
   */
  protected void pageChange(int newPageIndex) {
    super.pageChange(newPageIndex);
    if (newPageIndex == 1) {
      bpmnEditor.setInput(getBPMN2EditorInput());
      bpmnEditor.doRevertToSaved();
    }
  }

  private IFile getAssociatedBPMN2IFile() {

    URI uri = getAssociatedBPMN2URI();

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IFile bpmn20File = workspace.getRoot().getFile(new Path(uri.toPlatformString(true)));

    return bpmn20File;
  }

  private URI getAssociatedBPMN2URI() {

    final Diagram diagram = ((DiagramEditorInput) diagramEditor.getEditorInput()).getDiagram();
    final URI originalURI = diagram.eResource().getURI();
    final URI parentURI = originalURI.trimSegments(1);
    final String REGEX_FILENAME = "\\$originalFile";

    String finalSegment = "$originalFile" + ".bpmn20.xml";
    finalSegment = finalSegment.replaceAll(REGEX_FILENAME, originalURI.lastSegment().substring(
            0, originalURI.lastSegment().indexOf(".")));

    return parentURI.appendSegment(finalSegment);
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

  private IFileEditorInput getBPMN2EditorInput() {
    associatedBPMN2File = getAssociatedBPMN2IFile();
    return new FileEditorInput(associatedBPMN2File);
  }

  private boolean isBPM2FileType(final IFileEditorInput editorInput) {

    boolean isBPMN2File = false;
    IFile file = editorInput.getFile();
    try {
      IContentDescription desc = file.getContentDescription();
      if (desc != null) {
        IContentType type = desc.getContentType();
        if (ActivitiBPMNDiagramConstants.BPMN2_CONTENTTYPE_ID.equals(type.getId())) {
          isBPMN2File = true;
        }
      }
    } catch (CoreException e) {
      e.printStackTrace();
      return isBPMN2File;
    }

    return isBPMN2File;
  }
}
