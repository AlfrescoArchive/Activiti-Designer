/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.kickstart.eclipse.navigator;

import java.net.URL;

import org.activiti.designer.kickstart.eclipse.navigator.listeners.CmisNavigatorSelectionChangedListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.internal.navigator.CommonNavigatorActionGroup;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.LinkHelperService;
import org.osgi.framework.Bundle;

public class CmisNavigator extends CommonNavigator {

	public CmisNavigator() {
	}

	protected Object getInitialInput() {

		// Add listener for selection changes in the tree
		getCommonViewer().addSelectionChangedListener(
		    new CmisNavigatorSelectionChangedListener());

		return new Root();
	}
	
	@Override
	protected ActionGroup createCommonActionGroup() {
		return new CmisNavigatorActionGroup(this, getCommonViewer(), getLinkHelperService());
	}
	
	/** A little hack to have custom buttons at the top of the navigator */
	static class CmisNavigatorActionGroup extends CommonNavigatorActionGroup {
		
		protected CommonNavigator commonNavigator;
		protected CommonViewer commonViewer;

		public CmisNavigatorActionGroup(CommonNavigator aNavigator, CommonViewer aViewer,LinkHelperService linkHelperService) {
	    super(aNavigator, aViewer, linkHelperService);
	    this.commonNavigator = aNavigator;
	    this.commonViewer = aViewer;
    }
		
		@Override
		protected void fillToolBar(IToolBarManager toolBar) {
		  
			// Refresh action
		  RefreshAction refreshAction = new RefreshAction(commonViewer);
		  Bundle bundle = Platform.getBundle("org.activiti.designer.kickstart.eclipse");
		  URL fullPathString = BundleUtility.find(bundle, "icons/refresh.gif");
		  ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(fullPathString);
		  refreshAction.setImageDescriptor(imageDescriptor);
		  refreshAction.setHoverImageDescriptor(imageDescriptor);
		  
			toolBar.add(refreshAction);
			
			// Defaults
			super.fillToolBar(toolBar);
		}
		
	}
	
	static class RefreshAction extends Action {

		private final CommonViewer commonViewer;
		
		public static final String COMMAND_ID = "org.activiti.designer.kickstart.command.refreshCmisNavigator";

		public RefreshAction(CommonViewer aViewer) {
			super("Refresh");
			setToolTipText("Refresh");
			setActionDefinitionId(COMMAND_ID);
			commonViewer = aViewer;
		}

		public void run() {
			// Clear the cached cmis tree
			CmisUtil.clearCaches();
			
			// Refresh the tree
			if (commonViewer != null) {
				commonViewer.refresh();
			}
		}
	}

}
