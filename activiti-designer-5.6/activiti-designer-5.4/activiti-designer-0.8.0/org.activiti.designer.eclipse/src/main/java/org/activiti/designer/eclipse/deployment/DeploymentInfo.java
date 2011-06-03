package org.activiti.designer.eclipse.deployment;

import org.eclipse.core.resources.IFolder;

public class DeploymentInfo {
	
	private String serverName = null;
	private String serverPort = null;
	private String serverDeployer = null;
	private IFolder processFolder = null;
	private Object[] classesAndResources = null;
	private Object[] filesAndFolders = null;

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public String getServerName() {
		return serverName;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	
	public String getServerPort() {
		return serverPort;
	}

	public void setServerDeployer(String serverDeployer) {
		this.serverDeployer = serverDeployer;
	}
	
	public String getServerDeployer() {
		return serverDeployer;
	}

	public void setProcessFolder(IFolder processFolder) {
		this.processFolder = processFolder;
	}
	
	public IFolder getProcessFolder() {
		return processFolder;
	}

	public void setClassesAndResources(Object[] classesAndResources) {
		this.classesAndResources = classesAndResources;
	}
	
	public Object[] getClassesAndResources() {
		return classesAndResources;
	}

	public void setFilesAndFolders(Object[] filesAndFolders) {
		this.filesAndFolders = filesAndFolders;
	}
	
	public Object[] getFilesAndFolders() {
		return filesAndFolders;
	}

}
