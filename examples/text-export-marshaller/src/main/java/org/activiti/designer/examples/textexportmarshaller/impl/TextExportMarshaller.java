package org.activiti.designer.examples.textexportmarshaller.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.activiti.designer.eclipse.extension.export.AbstractExportMarshaller;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.eclipse.core.resources.IResource;

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
    System.out.println("Marshalling to Text Format!");

    final StringBuilder builder = new StringBuilder();

    try {
      getDiagramWorkerContext().getProgressMonitor().beginTask("Text output", 5);

      for (int i = 0; i < 3; i++) {
        Thread.currentThread().sleep(500l);
        getDiagramWorkerContext().getProgressMonitor().worked(1);
      }

      // Demonstrates getDiagramResource() method usage
      final IResource diagramResource = getDiagramResource();
      builder.append("Text generated from resource ").append(diagramResource.getName());

      final InputStream diagramInputStream = getDiagramInputStream();
      builder.append("\n\n").append("The original diagram stream is ").append(getStreamLength(getDiagramInputStream())).append(" bytes in size");

      final URI diagramURI = getDiagramURI();
      builder.append("\n\n").append("The original diagram URI is: ").append(diagramURI.toString());

      saveResource(getURIRelativeToDiagram(OUTPUT_FILE_NAME_PATTERN), new ByteArrayInputStream(builder.toString().getBytes()), getDiagramWorkerContext()
              .getProgressMonitor());

    } catch (InterruptedException e) {
      e.printStackTrace();
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
