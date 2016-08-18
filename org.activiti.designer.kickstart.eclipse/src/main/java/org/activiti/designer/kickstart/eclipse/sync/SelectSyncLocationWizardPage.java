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
package org.activiti.designer.kickstart.eclipse.sync;

import org.activiti.designer.kickstart.eclipse.common.OpenFolderOnDoubleClickListener;
import org.activiti.designer.kickstart.eclipse.navigator.CmisContentProvider;
import org.activiti.designer.kickstart.eclipse.navigator.CmisLabelProvider;
import org.activiti.designer.kickstart.eclipse.navigator.Root;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class SelectSyncLocationWizardPage extends WizardPage {

	protected SyncDataHolder synchronizationDataHolder;
	
	public SelectSyncLocationWizardPage(SyncDataHolder synchronizationDataHolder) {
		super("select-sync-location");
		this.synchronizationDataHolder = synchronizationDataHolder;

		setTitle("Choose a destination");
		setDescription("Select a location in the repository to store the process");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		setControl(container);

		final TreeViewer repositoryTreeViewer = new TreeViewer(container, SWT.SINGLE);
		repositoryTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		repositoryTreeViewer.setContentProvider(new CmisContentProvider(".kickproc"));
		repositoryTreeViewer.setLabelProvider(new CmisLabelProvider());
		repositoryTreeViewer.addDoubleClickListener(new OpenFolderOnDoubleClickListener());
		repositoryTreeViewer.setInput(new Root());
		
		repositoryTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setPageComplete(false);
				if (event.getSelection() instanceof TreeSelection) {
					TreeSelection selection = (TreeSelection) event.getSelection();
					Object[] selectedElements = selection.toArray();
					if (selectedElements != null 
							&& selectedElements.length > 0 
							&& selectedElements[0] instanceof CmisObject) {
						synchronizationDataHolder.setDestination((CmisObject) selectedElements[0]); // Only single select is allowed
						setPageComplete(true);
					}
				}
			}
		});
		
		setPageComplete(false);
	}
	
}
