package org.activiti.designer.popupmenus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class DeploymentMenu implements org.eclipse.ui.IObjectActionDelegate{

	ISelection fSelection;
	List<IFile> memberList;

	@Override
	public void run(IAction action) {
		Object selection = ( (IStructuredSelection) fSelection).getFirstElement();
		final IJavaProject javaProject = (IJavaProject) selection;
		IFolder diagramFolder = null;
		try {
		  diagramFolder = javaProject.getProject().getFolder(ActivitiBPMNDiagramConstants.DIAGRAM_FOLDER);
		  if(diagramFolder == null) {
		    return;
		  }
		} catch(Throwable e) {
		  return;
		}
		final IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
    try {
      progressService.busyCursorWhile(new IRunnableWithProgress() {

        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
          
          try {
            IProject project = javaProject.getProject();
            
        		// Create folder structures
            IFolder deploymentFolder = project.getFolder("deployment");
            if (deploymentFolder.exists()) {
              deploymentFolder.delete(true, new NullProgressMonitor());
            }
        
            deploymentFolder.create(true, true, new NullProgressMonitor());
            
            IFolder tempbarFolder = project.getFolder("tempbar");
            if (tempbarFolder.exists()) {
              tempbarFolder.delete(true, new NullProgressMonitor());
            }
        
            tempbarFolder.create(true, true, new NullProgressMonitor());
            
            IFolder tempclassesFolder = project.getFolder("tempclasses");
            if (tempclassesFolder.exists()) {
              tempclassesFolder.delete(true, new NullProgressMonitor());
            }
        
            tempclassesFolder.create(true, true, new NullProgressMonitor());
        
            // processdefinition
            String processName = "";
            memberList = new ArrayList<IFile>();
            getMembersWithFilter(project, ".bpmn");
            if(memberList.size() > 0) {
              for (IFile bpmnResource : memberList) {
                String bpmnFilename = bpmnResource.getName();
                if(processName.length() == 0)
                  processName = bpmnFilename.substring(0, bpmnFilename.indexOf("."));
                
                //TODO temp fix because .bpmn files are not parsed by the Activiti Engine version 5.9. This is fixed for 5.10
                bpmnFilename = bpmnFilename.substring(0, bpmnFilename.lastIndexOf(".")) + ".bpmn20.xml";
                
                bpmnResource.copy(tempbarFolder.getFile(bpmnFilename).getFullPath(), true, new NullProgressMonitor());
              }
          
              // task forms
              memberList = new ArrayList<IFile>();
              getMembersWithFilter(project, ".form");
              for (IFile formResource : memberList) {
                String formFilename = formResource.getName();
                IPath packagePath = formResource.getFullPath().removeFirstSegments(4).removeLastSegments(1);
                IFolder newPackageFolder = tempbarFolder.getFolder(packagePath);
                createFolderStructure(newPackageFolder);
                formResource.copy(newPackageFolder.getFile(formFilename).getFullPath(), true, new NullProgressMonitor());
              }
              
              // png
              memberList = new ArrayList<IFile>();
              getMembersWithFilter(project, ".png");
              for (IFile pngResource : memberList) {
                String pngFilename = pngResource.getName();
                pngResource.copy(tempbarFolder.getFile(pngFilename).getFullPath(), true, new NullProgressMonitor());
              }
              
              // drl
              memberList = new ArrayList<IFile>();
              getMembersWithFilter(project, ".drl");
              for (IFile drlResource : memberList) {
                String drlFilename = drlResource.getName();
                drlResource.copy(tempbarFolder.getFile(drlFilename).getFullPath(), true, new NullProgressMonitor());
              }
          
              compressPackage(deploymentFolder, tempbarFolder, processName + ".bar");
              
              IFolder classesFolder = project.getFolder("target/classes");
              memberList = new ArrayList<IFile>();
              getMembersWithFilter(classesFolder, ".class");
              getMembersWithFilter(classesFolder, ".gif");
              if(memberList.size() > 0) {
                for (IFile classResource : memberList) {
                  String classFilename = classResource.getName();
                  IPath packagePath = classResource.getFullPath().removeFirstSegments(3).removeLastSegments(1);
                  IFolder newPackageFolder = tempclassesFolder.getFolder(packagePath);
                  createFolderStructure(newPackageFolder);
                  classResource.copy(newPackageFolder.getFile(classFilename).getFullPath(), true, new NullProgressMonitor());
                }
                compressPackage(deploymentFolder, tempclassesFolder, processName + ".jar");
              }
          
              // refresh the output folder to reflect changes
              deploymentFolder.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            }
            
          } catch(Exception e) {
            e.printStackTrace();
          }
        }
      });
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
	
	private void createFolderStructure(IFolder newFolder) throws Exception {
	  if(newFolder.exists()) return;
	  
	  if(newFolder.getParent().exists() == false) {
	    createFolderStructure((IFolder) newFolder.getParent());
	  }
	  newFolder.create(true, true, new NullProgressMonitor());
	}
	
	private void getMembersWithFilter(IContainer root, String extension) {
	  try {
  	  for (IResource resource : root.members()) {
        if (resource instanceof IFile) {
          if(resource.getName().endsWith(extension)) {
            memberList.add((IFile) resource);
          }
        } else if(resource instanceof IFolder && ((IFolder) resource).getName().contains("target") == false) {
          getMembersWithFilter((IFolder) resource, extension);
        }
      }
	  } catch(Exception e) {
	    e.printStackTrace();
	  }
	}
	
	private void compressPackage(final IFolder destination, final IFolder folderToPackage, final String fileName) throws Exception {
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    File base = folderToPackage.getLocation().toFile();
    final IFile archiveFile = workspace.getRoot().getFile(
            destination.getFile(fileName).getFullPath());
    final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archiveFile.getLocation().toFile()));
    final String absoluteDirPathToStrip = folderToPackage.getLocation().toFile().getAbsolutePath() + File.separator;
    try {
      zipDirectory(out, base, absoluteDirPathToStrip);
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }
	
	private void zipDirectory(final ZipOutputStream out, final File base, final String absoluteDirPathToStrip) throws Exception {
    File[] reportFiles = base.listFiles();
    for (final File file : reportFiles) {
      if (file.isDirectory()) {
        zipDirectory(out, file, absoluteDirPathToStrip);
        continue;
      }
      String entryName = StringUtils.removeStart(file.getAbsolutePath(), absoluteDirPathToStrip);
      entryName = backlashReplace(entryName);
      ZipEntry entry = new ZipEntry(entryName);
      out.putNextEntry(entry);
      if (file.isFile()) {
        FileInputStream fin = new FileInputStream(file);
        byte[] fileContent = new byte[(int)file.length()];
        fin.read(fileContent);
        out.write(fileContent);
      }
      out.closeEntry();
    }
  }

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
	}

  @Override
  public void setActivePart(IAction action, IWorkbenchPart part) {
  }
  
  private String backlashReplace(String myStr){
	  final StringBuilder result = new StringBuilder();
	  final StringCharacterIterator iterator = new StringCharacterIterator(myStr);
	  char character =  iterator.current();
	  while (character != CharacterIterator.DONE ){
	     
		  if (character == '\\') {
	         result.append("/");
	      }
	      else {
	        result.append(character);
	      }

	      
	      character = iterator.next();
	  }
	  return result.toString();
  }
}

