package org.activiti.designer.popupmenus;

import java.io.File;

import org.activiti.designer.eclipse.bpmnimport.ImportBpmnElementsCommand;
import org.activiti.designer.eclipse.bpmnimport.ImportBpmnUtil;
import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class ImportBpmnMenu implements org.eclipse.ui.IObjectActionDelegate{

	ISelection fSelection;

	@Override
	public void run(IAction action) {
		Object selection = ( (IStructuredSelection) fSelection).getFirstElement();
		IJavaProject javaProject = (IJavaProject) selection;
		IFolder diagramFolder = null;
		try {
		  diagramFolder = javaProject.getProject().getFolder(ActivitiBPMNDiagramConstants.DIAGRAM_FOLDER);
		  if(diagramFolder == null) {
		    return;
		  }
		} catch(Throwable e) {
		  return;
		}
		
		FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
    fd.setText("Choose BPMN 2.0 XML file to import");
    if(OSUtil.getOperatingSystem() == OSEnum.Windows) {
      fd.setFilterPath("C:/");
    } else {
      fd.setFilterPath("/");
    }
    String[] filterExt = { "*.xml"};
    fd.setFilterExtensions(filterExt);
    String bpmnFile = fd.open();
    
    if(bpmnFile == null || bpmnFile.length() == 0) return;
    
    String processName = bpmnFile.substring(bpmnFile.lastIndexOf(File.separator) + 1);
    processName = processName.replace(".xml", "");
    processName = processName.replace(".bpmn20", "");

    ImportBpmnElementsCommand operation = ImportBpmnUtil.createDiagram(processName, bpmnFile, 
            javaProject.getProject(), diagramFolder);

    // Open the editor
    String platformString = operation.getCreatedResource().getURI().toPlatformString(true);
    IFile file = javaProject.getProject().getParent().getFile(new Path(platformString));
    IFileEditorInput input = new FileEditorInput(file);
    
    try {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, ActivitiBPMNDiagramConstants.DIAGRAM_EDITOR_ID);
      
    } catch (PartInitException e) {
      String error = "Error while opening diagram editor";
      IStatus status = new Status(IStatus.ERROR, ActivitiPlugin.getID(), error, e);
      ErrorDialog.openError(Display.getCurrent().getActiveShell(), "An error occured", null, status);
      return;
    }
  }

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
	}

  @Override
  public void setActivePart(IAction action, IWorkbenchPart part) {
  }
}

