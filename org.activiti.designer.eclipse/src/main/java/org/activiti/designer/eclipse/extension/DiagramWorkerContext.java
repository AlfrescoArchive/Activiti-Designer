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
package org.activiti.designer.eclipse.extension;

import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Context for classes that perform work on diagrams.
 * 
 * <p>
 * The {@link IProgressMonitor} obtained from {@link #getProgressMonitor()} can
 * be used to indicate progress made in the worker and will be reported to the
 * user as part of the overall progress of work on the diagram.
 * 
 * @author Tiese Barrell
 * 
 */
public interface DiagramWorkerContext {

  /**
   * Gets the {@link IProgressMonitor} to be used to report progress on the work
   * being performed.
   * 
   * @return the progress monitor
   */
  public IProgressMonitor getProgressMonitor();

  /**
   * Gets the internal BPMN memory model of the diagram being worked on.
   * 
   * @return the Bpmn2MemoryModel of the diagram
   */
  public BpmnMemoryModel getBpmnModel();

}
