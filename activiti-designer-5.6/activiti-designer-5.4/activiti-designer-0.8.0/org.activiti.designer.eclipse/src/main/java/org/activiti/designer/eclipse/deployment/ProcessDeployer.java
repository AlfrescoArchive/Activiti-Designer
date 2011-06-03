package org.activiti.designer.eclipse.deployment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class ProcessDeployer {
	Shell shell;
	IFolder processFolder;
	String targetLocation;
	List classesAndResources;
	List filesAndFolders;
	boolean useCredentials = false;
	//newly added variables
	String activitiProPath;
    
	
	public void setShell(Shell shell) {
		this.shell = shell;
	}
	
	public void setActivitiPropPath(String path) {
		activitiProPath = path;
	}
	
	public void setProcessFolder(IFolder processFolder) {
		this.processFolder = processFolder;
	}
	
	public void setTargetLocation(String targetLocation) {
		this.targetLocation = targetLocation;
	}
	
	public void setClassesAndResources(List classesAndResources) {
		this.classesAndResources = classesAndResources;
	}
	
	public void setFilesAndFolders(List filesAndFolders) {
		this.filesAndFolders = filesAndFolders;
	}
	
	public void setUseCredentials(boolean useCredentials) {
		this.useCredentials = useCredentials;
	}
	
	public boolean deploy() {
		try {
			showProgressMonitorDialog();
			showSuccessDialog();
			return true;
		}
		catch (ConnectException e) {
			MessageDialog dialog = new MessageDialog(shell, "Server Not Found", null,
					"The server could not be reached. Check your connection parameters.",
					SWT.ICON_INFORMATION, new String[] { "OK" }, 0);
			dialog.open();
			return false;
        }
		catch (IOException e) {
			if (e.getMessage().contains("Server returned HTTP response code: 403 for URL")) {
				MessageDialog dialog = new MessageDialog(shell, "Not Allowed", null,
						"The server refused to perform the deployment. Check your credentials.",
						SWT.ICON_INFORMATION, new String[] { "OK" }, 0);
				dialog.open();
				return false;
			} else {
				showErrorDialog(e);
				return false;
			}
		}
        catch (Exception e) {
            // NOTE that Error's are not caught because that might halt the JVM and mask the original Error.
			showErrorDialog(e);
			return false;
		}
	}
	
	public void saveWithoutDeploying() {
		try {
			saveParFile(createParBytes(getProjectClasspathUrls()));
		} catch (Exception e) {
			Logger.logError(e);
			ErrorDialog dialog = new ErrorDialog(shell,
					"Unexpected Exception While Saving",
					"An exception happened while saving the process definition archive",
					new Status(
							Status.ERROR,
							ActivitiPlugin.getDefault().getBundle()
									.getSymbolicName(),
							Status.ERROR,
							"An unexpected exception caused the save operation to fail",
							e), Status.ERROR);
			dialog.open();
		}
	}
	
	private void showProgressMonitorDialog() throws Exception {
		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(shell);
		progressMonitorDialog.run(false, false, new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InterruptedException {
				try {
					byte[] baos = createParBytes(getProjectClasspathUrls());
					if (targetLocation != null) {
						saveParFile(baos);
					}
					deployProcess();
					return;
				} catch (IOException e) {
					if (e.getMessage().contains("Server returned HTTP response code: 403 for URL")) {
						Logger.logError(
								"The server refused to execute the deployment. Check your credentials.",
								e);
					}
				} catch (Exception e) {
					Logger
							.logError(
									"Exception happened while deploying",
									e);
				}
				throw new InterruptedException(
						"Error while deploying, look in the Error Log for more info");
			}
		});
	}
	
	public void deployProcess() {
		/*ProcessEngine processEngine = new ProcessEngineBuilder().configureFromPropertiesResource("activiti.properties").buildProcessEngine();
		ZipInputStream inputStream  = null;
		 try {
			 inputStream = new ZipInputStream(new FileInputStream(targetLocation));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		RepositoryService ser  = processEngine.getRepositoryService();
		DeploymentBuilder build = ser.createDeployment();
		build.addZipInputStream(inputStream);
		build.deploy();*/
		
	}
	
	private void saveParFile(byte[] parBytes) throws IOException {
		File file = new Path(targetLocation).toFile();
		String barFileName = "/activiti.bar";
		int idx = targetLocation.indexOf(".");
		if(idx>0){
			idx = targetLocation.lastIndexOf("/");
			barFileName = targetLocation.substring(idx, targetLocation.length());
			targetLocation = targetLocation.substring(0, idx+1);
			file = null;
			file = new Path(targetLocation).toFile();
		}
		if (!file.exists()) {
			file.mkdirs();
		}
		targetLocation = file.getAbsolutePath()+barFileName;
		file = null;
		file = new Path(targetLocation).toFile();
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(parBytes);
		fos.close();
	}

	private byte[] createParBytes(URL[] urls) throws Exception {
		URLClassLoader newLoader = new URLClassLoader(urls, getClass()
				.getClassLoader());
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
		addFilesAndFolders(zipOutputStream);
		addClassesAndResources(zipOutputStream, newLoader);
		zipOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}

	private void addFilesAndFolders(ZipOutputStream zipOutputStream) throws Exception {
		for (int i = 0; i < filesAndFolders.size(); i++) {
			IResource resource = (IResource)filesAndFolders.get(i);
			int index = processFolder.getProjectRelativePath().toString().length() + 1;
			addFile(zipOutputStream, resource.getProjectRelativePath().toString().substring(index));
		}
	}

	private void showSuccessDialog() {
		MessageDialog dialog = new MessageDialog(shell, "Deployment Successful", null,
				"The process archive deployed successfully.",
				SWT.ICON_INFORMATION, new String[] { "OK" }, 0);
		dialog.open();
	}

	private void showErrorDialog(Throwable t) {
		ErrorDialog dialog = new ErrorDialog(shell,
				"Unexpected Deployment Exception",
				"An exception happened during the deployment of the process",
				new Status(
						Status.ERROR,
						ActivitiPlugin.getDefault().getBundle()
								.getSymbolicName(),
						Status.ERROR,
						"An unexpected exception caused the deployment to fail",
						t), Status.ERROR);
		dialog.open();
	}

	private void addClassesAndResources(ZipOutputStream zos, ClassLoader loader)
			throws CoreException, IOException {
		for (int i = 0; i < classesAndResources.size(); i++) {
			addClassOrResource(zos, loader, (String)classesAndResources.get(i));
		}
	}
	
	private void addClassOrResource(ZipOutputStream zos, ClassLoader loader, String classOrResource) throws IOException {
		byte[] buff = new byte[256];
		zos.putNextEntry(new ZipEntry(classOrResource));
		InputStream is = loader.getResourceAsStream(classOrResource);
		int read;
		while ((read = is.read(buff)) != -1) {
			zos.write(buff, 0, read);
		}
		is.close();
		if (classOrResource.endsWith(".class")) {
			final String className = 
				classOrResource.substring(classOrResource.lastIndexOf('/') + 1, classOrResource.length() - 6) + '$';
			URL url = loader.getResource(classOrResource);
			File file = new File(url.getFile());
			File folder = new File(file.getParent());
			String nestedClasses[] = folder.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.startsWith(className);
				}
			});
			if (nestedClasses != null) {
				for (int i = 0; i < nestedClasses.length; i++) {
					String fileName = classOrResource.substring(0, classOrResource.lastIndexOf("/") + 1) + nestedClasses[i];
					zos.putNextEntry(new ZipEntry(fileName));
					is = loader.getResourceAsStream(fileName);
					while ((read = is.read(buff)) != -1) {
						zos.write(buff, 0, read);
					}
					is.close();
				}
			}
		}
	}

	private void addFile(ZipOutputStream zos, String fileName)
			throws CoreException, IOException {
		byte[] buff = new byte[256];
		IFile file = processFolder.getFile(fileName);
		if (!file.exists()) return;
		InputStream is = file.getContents();
		zos.putNextEntry(new ZipEntry(fileName));
		int read;
		while ((read = is.read(buff)) != -1) {
			zos.write(buff, 0, read);
		}
	}

	private URL[] getProjectClasspathUrls() throws CoreException, MalformedURLException {
		IProject project = processFolder.getProject();
		IJavaProject javaProject = JavaCore.create(project);
		String[] pathArray = JavaRuntime
				.computeDefaultRuntimeClassPath(javaProject);
		URL[] urls = new URL[pathArray.length];
		for (int i = 0; i < pathArray.length; i++) {
			urls[i] = new File(pathArray[i]).toURI().toURL();
		}
		return urls;
	}
	
}
