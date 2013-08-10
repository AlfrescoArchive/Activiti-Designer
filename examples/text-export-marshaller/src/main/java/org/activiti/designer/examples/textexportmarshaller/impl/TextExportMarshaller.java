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

package org.activiti.designer.examples.textexportmarshaller.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.activiti.designer.eclipse.extension.export.AbstractExportMarshaller;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.SubProgressMonitor;

public class TextExportMarshaller extends AbstractExportMarshaller {

  private static final String OUTPUT_FILE_NAME_PATTERN = ExportMarshaller.PLACEHOLDER_ORIGINAL_FILENAME_WITHOUT_EXTENSION + "/"
          + ExportMarshaller.PLACEHOLDER_ORIGINAL_FILE_EXTENSION + "/text-export-" + ExportMarshaller.PLACEHOLDER_ORIGINAL_FILENAME + "-"
          + ExportMarshaller.PLACEHOLDER_DATE_TIME + ".txt";

  @Override
  public String getMarshallerName() {
    return "Text Export Marshaller";
  }

  @Override
  public String getFormatName() {
    return "Plain Text";
  }

  @Override
  public void doMarshallDiagram() {

    // Clear markers first
    clearMarkersForDiagram();

    System.out.println("Marshalling to Text Format!");

    final StringBuilder builder = new StringBuilder();

    try {
      getDiagramWorkerContext().getProgressMonitor().beginTask("Text output", 25);

      // Demonstrates progress using the monitor
      for (int i = 0; i < 5; i++) {
        Thread.currentThread().sleep(500l);
        getDiagramWorkerContext().getProgressMonitor().worked(1);
      }

      // Demonstrates getDiagramResource() method usage
      final IResource diagramResource = getDiagramResource();
      builder.append("Text generated from resource ").append(diagramResource.getName());
      getDiagramWorkerContext().getProgressMonitor().worked(5);

      final InputStream diagramInputStream = getDiagramInputStream();
      builder.append("\n\n").append("The original diagram stream is ").append(getStreamLength(diagramInputStream)).append(" bytes in size");
      getDiagramWorkerContext().getProgressMonitor().worked(5);

      final URI diagramURI = getDiagramURI();
      builder.append("\n\n").append("The original diagram URI is: ").append(diagramURI.toString());
      getDiagramWorkerContext().getProgressMonitor().worked(5);

      final URI newResourceURI = getURIRelativeToDiagram(OUTPUT_FILE_NAME_PATTERN);

      saveResource(newResourceURI, new ByteArrayInputStream(builder.toString().getBytes()), new SubProgressMonitor(getDiagramWorkerContext()
              .getProgressMonitor(), 5));

      // Add an example marker
      addInfoToDiagram("Additional information was saved for this diagram, at: " + newResourceURI.getPath(), null);

    } catch (Exception e) {
      e.printStackTrace();
      addProblemToDiagram(e.getMessage(), null);
    } finally {
      getDiagramWorkerContext().getProgressMonitor().done();
    }

  }
  private int getStreamLength(final InputStream diagramInputStream) {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      int data = diagramInputStream.read();
      while (data != -1) {
        char aChar = (char) data;
        baos.write(aChar);

        data = diagramInputStream.read();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        diagramInputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return baos.size();
  }
}
