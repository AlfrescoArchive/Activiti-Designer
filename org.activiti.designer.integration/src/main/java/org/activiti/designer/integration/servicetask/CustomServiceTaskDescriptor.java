package org.activiti.designer.integration.servicetask;

public class CustomServiceTaskDescriptor {
    private Class <? extends CustomServiceTask> clazz;
    private String extensionName;
    private String extensionJarPath;
    
	public CustomServiceTaskDescriptor(
			Class<? extends CustomServiceTask> clazz, String extensionName,
			String extensionJarPath) {
		this.clazz = clazz;
		this.extensionName = extensionName;
		this.extensionJarPath = extensionJarPath;
	}

	public Class<? extends CustomServiceTask> getClazz() {
		return clazz;
	}

	public String getExtensionName() {
		return extensionName;
	}

	public String getExtensionJarPath() {
		return extensionJarPath;
	}
}
