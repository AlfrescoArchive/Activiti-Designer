package org.activiti.designer.eclipse.editor;

import java.io.File;

import org.activiti.designer.eclipse.bpmnimport.ImportBpmnUtil;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.editor.sync.DiagramUpdater;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
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
  
  @Override
  public void doSave(IProgressMonitor progressMonitor) {
	  super.doSave(progressMonitor);
	  IFile associatedBPMN2File = ((IFileEditorInput) getEditorInput()).getFile();
	  final IFile diagramFile = getAssociatedDiagramIFile(associatedBPMN2File);
    if(diagramFile.exists() == false) {
      String bpmnFile = associatedBPMN2File.getRawLocation().toFile().getAbsolutePath();
      String processName = bpmnFile.substring(bpmnFile.lastIndexOf(File.separator) + 1);
      processName = processName.replace(".xml", "");
      processName = processName.replace(".bpmn20", "");
      ImportBpmnUtil.createDiagram(processName, bpmnFile, 
              associatedBPMN2File.getProject(), associatedBPMN2File.getParent());
    
    } else {
    	ResourceSet resourceSet = new ResourceSetImpl();
    	TransactionalEditingDomain domain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
  		final Diagram diagram = GraphitiUiInternal.getEmfService().getDiagramFromFile(diagramFile, domain.getResourceSet());
  		domain.getResourceSet().getResources().add(diagram.eResource());
  		
  		try {
  			
	      final IStorage storage = ((IFileEditorInput) getEditorInput()).getStorage();
	      DiagramUpdater operation = new DiagramUpdater( 
	      		domain, diagram, storage);
	      domain.getCommandStack().execute(operation);
      
      	diagram.eResource().save(null);
      } catch (Exception e) {
        IStatus status = new Status(IStatus.ERROR, ActivitiPlugin.getID(), e.getMessage(), e); //$NON-NLS-1$
        ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Failed to synchronize Activiti model file", e.getMessage(), status);
      }

      // Dispose the editing domain to eliminate memory leak
      domain.dispose();
    }
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
