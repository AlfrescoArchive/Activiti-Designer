package org.activiti.designer.kickstart.eclipse.ui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.activiti.designer.util.ActivitiConstants;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class CreateActivitiProjectWizard extends BasicNewProjectResourceWizard {

	@Override
	public boolean performFinish() {
		if (!super.performFinish()) {
      return false;
    }

		IProject newProject = getNewProject();

		try {

			IProjectDescription description = newProject.getDescription();
			String[] newNatures = new String[2];
			newNatures[0] = JavaCore.NATURE_ID;
			newNatures[1] = ActivitiConstants.NATURE_ID;
			description.setNatureIds(newNatures);
			newProject.setDescription(description, null);

			IJavaProject javaProject = JavaCore.create(newProject);

			createSourceFolders(newProject);
			createOutputLocation(javaProject);

			IClasspathEntry[] entries = createClasspathEntries(javaProject);

			javaProject.setRawClasspath(entries, null);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private IClasspathEntry[] createClasspathEntries(IJavaProject javaProject) {

		IPath srcPath1 = javaProject.getPath().append("src/main/java");
		IPath srcPath2 = javaProject.getPath().append("src/main/resources");
		IPath srcPath3 = javaProject.getPath().append("src/test/java");
		IPath srcPath4 = javaProject.getPath().append("src/test/resources");

		IPath[] javaPath = new IPath[] { new Path("**/*.java") };
		IPath testOutputLocation = javaProject.getPath().append("target/test-classes");

		IClasspathEntry[] entries = { JavaCore.newSourceEntry(srcPath1, javaPath, null, null),
				JavaCore.newSourceEntry(srcPath2, javaPath),
				JavaCore.newSourceEntry(srcPath3, javaPath, null, testOutputLocation),
				JavaCore.newSourceEntry(srcPath4, javaPath, testOutputLocation),
				JavaRuntime.getDefaultJREContainerEntry() };

		return entries;
	}

	private void createSourceFolders(IProject project) throws CoreException {

		List<String> sourceFolders = Collections.synchronizedList(new LinkedList<String>());

		sourceFolders.add("src");
		sourceFolders.add("src/main");
		sourceFolders.add("src/main/java");
		sourceFolders.add("src/main/resources/");
		sourceFolders.add(ActivitiConstants.DIAGRAM_FOLDER);
		sourceFolders.add("src/test/");
		sourceFolders.add("src/test/java/");
		sourceFolders.add("src/test/resources");

		for (String folder : sourceFolders) {
			IFolder sourceFolder = project.getFolder(folder);
			sourceFolder.create(false, true, null);
		}
	}

	private void createOutputLocation(IJavaProject javaProject) throws JavaModelException {

		IPath targetPath = javaProject.getPath().append("target/classes");
		javaProject.setOutputLocation(targetPath, null);
	}
}
