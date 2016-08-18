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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * 
 * Interface for validation workers.
 * 
 * @author Tiese Barrell
 * @since 5.6
 * @version 1
 */
public interface ProcessValidationWorker {

  Collection<ProcessValidationWorkerMarker> validate(Diagram diagram, Map<String, List<Object>> processNodes);

}
