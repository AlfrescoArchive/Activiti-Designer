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

package org.activiti.designer.util.workspace;

import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.designer.bpmn2.model.Process;
import org.eclipse.core.resources.IFile;

/**
 * Parses BPMN files for process elements.
 * 
 * @author tiesebarrell
 */
public class BpmnProcessParser {

  private final IFile bpmnFile;

  public BpmnProcessParser(final IFile bpmnFile) {
    super();
    this.bpmnFile = bpmnFile;
  }

  public Set<Process> getProcesses() {

    final Set<Process> result = new HashSet<Process>();

    try {
      XMLInputFactory xif = XMLInputFactory.newInstance();
      InputStreamReader in = new InputStreamReader(bpmnFile.getContents(), "UTF-8");
      XMLStreamReader xtr = xif.createXMLStreamReader(in);

      Process currentProcess = null;

      while (xtr.hasNext()) {
        xtr.next();

        if (isProcessStartElement(xtr)) {
          currentProcess = new Process();
          currentProcess.setId(xtr.getAttributeValue(null, "id"));
        } else if (isProcessEndElement(xtr)) {
          result.add(currentProcess);
          currentProcess = null;
        } else {
          continue;
        }
      }

    } catch (Exception e) {
      // Don't handle, parsing failed.
      // TODO: log and show message.
    }

    return result;

  }

  private static final boolean isProcessStartElement(final XMLStreamReader xtr) {
    return xtr.isStartElement() && "process".equalsIgnoreCase(xtr.getLocalName());
  }

  private static final boolean isProcessEndElement(final XMLStreamReader xtr) {
    return xtr.isEndElement() && "process".equalsIgnoreCase(xtr.getLocalName());
  }

}
