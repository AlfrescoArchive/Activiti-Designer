package org.activiti.designer.kickstart.eclipse.navigator;

import org.eclipse.ui.navigator.CommonNavigator;

public class CmisNavigator extends CommonNavigator {
	
	public CmisNavigator() {
		System.out.println("----> NAVIGATOR wordt aangemaakt");
	}
	
	protected Object getInitialInput() {
		System.out.println("----> GETTING INITIAL INPUT");
        return new Root();
    }

}
