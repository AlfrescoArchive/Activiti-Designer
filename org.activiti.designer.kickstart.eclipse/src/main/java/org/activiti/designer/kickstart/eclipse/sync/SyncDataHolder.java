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

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.eclipse.core.resources.IFile;

/**
 * A bucket for all objects used by various classes during process synchronization.
 * 
 * @author jbarrez
 */
public class SyncDataHolder {

	protected IFile sourceFile;
	protected String targetFileName;
	protected CmisObject destination;

	
	public IFile getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(IFile sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getTargetFileName() {
		return targetFileName;
	}

	public void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}

	public CmisObject getDestination() {
		return destination;
	}

	public void setDestination(CmisObject destination) {
		this.destination = destination;
	}
	
}
