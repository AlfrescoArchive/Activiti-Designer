package org.activiti.designer.examples.textexportmarshaller.impl;

import org.activiti.designer.eclipse.extension.export.AbstractExportMarshaller;
import org.activiti.designer.eclipse.extension.export.ExportMarshallerContext;

public class TextExportMarshaller extends AbstractExportMarshaller {

  @Override
  public String getMarshallerName() {
    return "Text Export Marshaller";
  }

  @Override
  public String getFormatName() {
    return "Plain Text";
  }

  @Override
  public void marshallDiagram(final ExportMarshallerContext context) {
    System.out.println("Marshalling to Text Format!");

    try {
      context.getProgressMonitor().beginTask("Text output", 5);

      for (int i = 0; i < 5; i++) {
        Thread.currentThread().sleep(2000l);
        context.getProgressMonitor().worked(1);
      }

      getRelativeURIForDiagram(diagram, relativePath);

    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      context.getProgressMonitor().done();
    }

  }
}
