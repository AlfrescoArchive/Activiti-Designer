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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.Wizard;

/**
 * @author jbarrez
 */
public class SyncProcessWithRepositoryWizard extends Wizard {

	protected SyncDataHolder synchronizationDataHolder;
	protected SelectSyncLocationWizardPage selectSyncLocationPage;

	public SyncProcessWithRepositoryWizard(IFile sourceFile) {
		synchronizationDataHolder = new SyncDataHolder();
		synchronizationDataHolder.setSourceFile(sourceFile);
		synchronizationDataHolder.setTargetFileName(sourceFile.getName());
		selectSyncLocationPage = new SelectSyncLocationWizardPage(synchronizationDataHolder);
	}

	public SyncProcessWithRepositoryWizard(String fileName, String nodeId) {
		// this.nodeId = nodeId;
	}

	@Override
	public void addPages() {
		addPage(selectSyncLocationPage);
	}

	@Override
	public boolean performFinish() {
		if (synchronizationDataHolder.getDestination() != null) {
			// The actual upload is done in a background job
			SyncUtil.startProcessSynchronizationBackgroundJob(
					getShell(), 
					synchronizationDataHolder.getDestination(),
					synchronizationDataHolder.getTargetFileName(), 
					true,
					false,
					synchronizationDataHolder.getSourceFile());
		}
		return true;
	}

	@Override
	public boolean canFinish() {
		return synchronizationDataHolder.getDestination() != null; // Only allow to finish when something is selected
	}

}
