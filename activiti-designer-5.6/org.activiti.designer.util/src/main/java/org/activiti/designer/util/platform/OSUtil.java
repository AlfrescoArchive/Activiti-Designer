package org.activiti.designer.util.platform;

public class OSUtil {
	
	private static String osName = null;
	
	public static OSEnum getOperatingSystem() {
		if(osName == null) {
			osName = System.getProperty("os.name");
		}
		if(osName.contains("Mac")) {
			return OSEnum.Mac;
		} else {
			return OSEnum.Windows;
		}
	}

}
