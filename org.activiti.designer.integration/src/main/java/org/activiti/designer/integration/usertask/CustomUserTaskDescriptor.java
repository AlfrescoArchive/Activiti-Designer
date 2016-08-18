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
