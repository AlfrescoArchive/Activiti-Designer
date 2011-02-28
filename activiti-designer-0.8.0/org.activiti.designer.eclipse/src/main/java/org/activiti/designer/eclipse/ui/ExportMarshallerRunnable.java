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

package org.activiti.designer.eclipse.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Runnable that invokes {@link ExportMarshaller}s with progress display.
 * 
 * @author Tiese Barrell
 * @since 0.6.0
 * @version 1
 */
public class ExportMarshallerRunnable implements IRunnableWithProgress {

  /**
   * The number of work units allocated to a marshaller.
   */
  private static final int WORK_UNITS_PER_MARSHALLER = 100;

  private Diagram diagram;
  private Collection<ExportMarshaller> marshallers;

  public ExportMarshallerRunnable(final Diagram diagram, final Collection<ExportMarshaller> marshallers) {
    this.diagram = diagram;
    this.marshallers = marshallers;
  }

  public ExportMarshallerRunnable(final Diagram diagram, final ExportMarshaller marshaller) {
    this.diagram = diagram;
    this.marshallers = new ArrayList<ExportMarshaller>();
    this.marshallers.add(marshaller);
  }

  public ExportMarshallerRunnable(final Diagram diagram, final String marshallerName) {
    this.diagram = diagram;
    this.marshallers = new ArrayList<ExportMarshaller>();
    final ExportMarshaller marshaller = ExtensionPointUtil.getExportMarshaller(marshallerName);
    if (marshaller == null) {
      throw new IllegalArgumentException("Unable to invoke ExportMarshaller with name " + marshallerName);
    }
    this.marshallers.add(marshaller);
  }

  public void run(IProgressMonitor monitor) {

    try {
      monitor.beginTask("Saving to additional export formats", marshallers.size() * WORK_UNITS_PER_MARSHALLER + 25);

      if (marshallers.size() > 0) {

        monitor.worked(25);

        for (final ExportMarshaller marshaller : marshallers) {
          final IProgressMonitor subMonitor = new SubProgressMonitor(monitor, WORK_UNITS_PER_MARSHALLER);
          try {
            monitor.subTask(String.format("Saving diagram to %s format", marshaller.getFormatName()));
            invokeExportMarshaller(marshaller, diagram, subMonitor);
          } finally {
            // enforce calling of done() if the client hasn't
            // done so itself
            subMonitor.done();
          }
        }
      }
    } finally {
      monitor.done();
    }
  }

  private void invokeExportMarshaller(final ExportMarshaller exportMarshaller, final Diagram diagram, final IProgressMonitor monitor) {

    ISafeRunnable runnable = new ISafeRunnable() {

      @Override
      public void handleException(Throwable exception) {
        System.out.println("An exception occurred while running ExportMarshaller " + exportMarshaller.getMarshallerName() + ": " + exception.getMessage());
      }

      @Override
      public void run() throws Exception {
        exportMarshaller.marshallDiagram(diagram, monitor);
      }
    };
    SafeRunner.run(runnable);
  }

}
