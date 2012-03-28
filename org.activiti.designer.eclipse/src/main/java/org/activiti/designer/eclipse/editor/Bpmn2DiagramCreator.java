package org.activiti.designer.eclipse.editor;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class Bpmn2DiagramCreator {

	private final static String TEMPFILE_EXTENSION = "bpmn2d";
	private IFolder diagramFolder;
	private IFile diagramFile;
	private URI uri;

	public DiagramEditorInput createDiagram(boolean openEditor, String contentFileName) throws CoreException {
		
		if (diagramFolder != null && !diagramFolder.exists()) {
			diagramFolder.create(false, true, null);
		}

		final Diagram diagram = Graphiti.getPeCreateService().createDiagram(
		    "BPMNdiagram", diagramFile.getFullPath().removeFileExtension().lastSegment(), true);
		// permanently hide grid on diagram
		//diagram.setGridUnit(0);
		//diagram.setVerticalGridUnit(0);
		uri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);
		
		if(contentFileName != null) {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(contentFileName);
			IFile modelFile = getModelFile(new Path(uri.trimFragment().toPlatformString(true)));
			String filePath = modelFile.getLocationURI().getRawPath();
			filePath = filePath.replaceAll("%20", " ");
	    System.out.println("filePath " + filePath);
			try {
			  OutputStream out = new FileOutputStream(filePath);
				
				byte[] buf = new byte[1024];
			  int len;
			  while ((len = in.read(buf)) > 0){
			  out.write(buf, 0, len);
			  }
			  in.close();
			  out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		FileService.createEmfFileForDiagram(uri, diagram);

		String providerId = GraphitiUi.getExtensionManager()
		    .getDiagramTypeProviderId(diagram.getDiagramTypeId());
		
		final DiagramEditorInput editorInput = new DiagramEditorInput(EcoreUtil.getURI(diagram), providerId);
		
		if (openEditor) {
			openEditor(editorInput);
		}

		return editorInput;
	}

	private void openEditor(final DiagramEditorInput editorInput) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					    .openEditor(editorInput, ActivitiBPMNDiagramConstants.DIAGRAM_EDITOR_ID);

				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public IFolder getDiagramFolder() {
		return diagramFolder;
	}

	public void setDiagramFolder(IFolder diagramFolder) {
		this.diagramFolder = diagramFolder;
	}

	public IFile getDiagramFile() {
		return diagramFile;
	}

	public void setDiagramFile(IFile diagramFile) {
		this.diagramFile = diagramFile;
	}

	public URI getUri() {
		return uri;
	}

	/**
	 * Construct a temporary folder based on the given path. The folder is
	 * constructed in the project root and its name will be the same as the given
	 * path's file extension.
	 * 
	 * @param fullPath
	 *          - path of the actual BPMN2 model file
	 * @return an IFolder for the temporary folder.
	 * @throws CoreException
	 */
	public static IFolder getTempFolder(IPath fullPath) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		String name = fullPath.getFileExtension();
		if (name == null || name.length() == 0)
			name = "bpmn";
		IFolder folder = root.getProject(fullPath.segment(0)).getFolder("." + name);
		if (!folder.exists()) {
			folder.create(true, true, null);
		}
		String[] segments = fullPath.segments();
		for (int i = 1; i < segments.length - 1; i++) {
			String segment = segments[i];
			folder = folder.getFolder(segment);
			if (!folder.exists()) {
				folder.create(true, true, null);
			}
		}
		return folder;
	}

	/**
	 * Return the temporary file to be used as editor input. Conceptually, this is
	 * the "diagramFile" mentioned here which is just a placeholder for use by
	 * Graphiti as the DiagramEditorInput file.
	 * 
	 * @param fullPath
	 *          - path of the actual BPMN2 model file
	 * @param folder
	 *          - folder containing the model file
	 * @return an IFile for the temporary file. If the file exists, it is first
	 *         deleted.
	 */
	public static IFile getTempFile(IPath fullPath, IFolder folder) {
		IPath path = fullPath.removeFileExtension().addFileExtension(
		    TEMPFILE_EXTENSION);
		IFile tempFile = folder.getFile(path.lastSegment());

		// We don't need anything from that file and to be sure there are no side
		// effects we delete the file
		if (tempFile.exists()) {
			try {
				tempFile.delete(true, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return tempFile;
	}

	/**
	 * Return the BPMN2 model file given a path to either the "diagramFile"
	 * temporary file, or the actual model file.
	 * 
	 * @param fullPath
	 *          - path of the actual BPMN2 model file
	 * @return an IFile for the model file.
	 */
	public static IFile getModelFile(IPath fullPath) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getFile(fullPath).getProject();
		int matchingSegments = project.getFullPath().matchingFirstSegments(fullPath);
		int totalSegments = fullPath.segmentCount();
		String ext = fullPath.getFileExtension();
		// sanity check: make sure the fullPath is not the project
		if (totalSegments <= matchingSegments)
			return null;

		String[] segments = fullPath.segments();
		IPath path = null;

		if (TEMPFILE_EXTENSION.equals(ext)) {
			// this is a tempFile - rebuild the BPMN2 model file name from its path
			ext = fullPath.segment(matchingSegments);
			if (ext.startsWith("."))
				ext = ext.substring(1);
			path = project.getFullPath();
			for (int i = matchingSegments + 1; i < segments.length; ++i) {
				path = path.append(segments[i]);
			}
			path = path.removeFileExtension().addFileExtension(ext);
		} else {
			// this is a model file - normalize path
			path = fullPath.makeAbsolute();
		}

		return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	}

	/**
	 * Delete the temporary diagram file. If the containing folder hierarchy is
	 * empty, it will also be deleted.
	 * 
	 * @param file
	 *          - the temporary diagram file.
	 */
	public static void dispose(IFile file) {
		try {
			IContainer container = file.getParent();
			file.delete(true, null);
			while (isEmptyFolder(container)) {
				container.delete(true, null);
				container = container.getParent();
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Check if the given folder is empty. This is true if it contains no files,
	 * or only empty folders.
	 * 
	 * @param container
	 *          - folder to check
	 * @return true if the folder is empty.
	 */
	public static boolean isEmptyFolder(IContainer container) {
		try {
			IResource[] members = container.members();
			for (IResource res : members) {
				int type = res.getType();
				if (type == IResource.FILE || type == IResource.PROJECT
				    || type == IResource.ROOT)
					return false;
				if (!isEmptyFolder((IContainer) res))
					return false;
			}
		} catch (CoreException e) {
			return false;
		}
		return true;
	}
}
