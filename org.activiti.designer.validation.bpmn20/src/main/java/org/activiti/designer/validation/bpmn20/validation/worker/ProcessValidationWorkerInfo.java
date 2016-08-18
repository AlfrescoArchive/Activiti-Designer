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
package org.activiti.designer.validation.bpmn20.validation.worker;

/**
 * Container object for {@link ProcessValidationWorker}s and their work units.
 * 
 * @author Tiese Barrell
 * @since 5.6
 * @version 1
 */
public class ProcessValidationWorkerInfo {

  private final ProcessValidationWorker processValidationWorker;
  private final int work;

  public ProcessValidationWorkerInfo(ProcessValidationWorker processValidationWorker, int work) {
    super();
    this.processValidationWorker = processValidationWorker;
    this.work = work;
  }

  public ProcessValidationWorker getProcessValidationWorker() {
    return processValidationWorker;
  }

  public int getWork() {
    return work;
  }

}
