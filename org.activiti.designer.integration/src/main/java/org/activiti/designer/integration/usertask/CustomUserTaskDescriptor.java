package org.activiti.designer.integration.usertask;

public class CustomUserTaskDescriptor {
    private Class <? extends CustomUserTask> clazz;
    private String extensionName;
    private String extensionJarPath;
    
	public CustomUserTaskDescriptor(
			Class<? extends CustomUserTask> clazz, String extensionName,
			String extensionJarPath) {
		this.clazz = clazz;
		this.extensionName = extensionName;
		this.extensionJarPath = extensionJarPath;
	}

	public Class<? extends CustomUserTask> getClazz() {
		return clazz;
	}

	public String getExtensionName() {
		return extensionName;
	}

	public String getExtensionJarPath() {
		return extensionJarPath;
	}
}
