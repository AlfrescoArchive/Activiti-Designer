/**
 * 
 */
package org.activiti.designer.export.image.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.editor.ActivitiDiagramEditor;
import org.activiti.designer.eclipse.extension.export.AbstractExportMarshaller;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

/**
 * Exports an image of the diagram being saved to the workspace.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public class ImageExportMarshaller extends AbstractExportMarshaller {

  private static final String FILENAME_PATTERN = ExportMarshaller.PLACEHOLDER_ORIGINAL_FILENAME_WITHOUT_EXTENSION + ".png";

  private IProgressMonitor monitor;
  private Diagram diagram;

  /**
	 * 
	 */
  public ImageExportMarshaller() {
  }

  @Override
  public String getMarshallerName() {
    return ActivitiBPMNDiagramConstants.IMAGE_MARSHALLER_NAME;
  }

  @Override
  public String getFormatName() {
    return "Activiti Designer Image";
  }

  @Override
  public void marshallDiagram(Diagram diagram, IProgressMonitor monitor) {

    this.monitor = monitor;
    this.diagram = diagram;

    monitor.beginTask("", 100);

    // Clear problems for this marshaller first
    clearMarkers(getResource(diagram.eResource().getURI()));

    monitor.worked(10);

    marshallImage();

    monitor.worked(90);

    monitor.done();
  }

  private void marshallImage() {
    try {

      // Retrieve GraphicalViewer from the save handler
      final GraphicalViewer graphicalViewer = ActivitiDiagramEditor.getActiveGraphicalViewer();

      if (graphicalViewer == null || graphicalViewer.getEditPartRegistry() == null)
        return;
      final ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) graphicalViewer.getEditPartRegistry().get(LayerManager.ID);
      final IFigure rootFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.PRINTABLE_LAYERS);
      final IFigure gridFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.GRID_LAYER);
      final Rectangle rootFigureBounds = rootFigure.getBounds();

      final boolean toggleRequired = gridFigure.isShowing();

      final Display display = Display.getDefault();

      final Image img = new Image(display, rootFigureBounds.width, rootFigureBounds.height);
      final GC imageGC = new GC(img);
      final SWTGraphics grap = new SWTGraphics(imageGC);

      // Access UI thread from runnable to print the canvas to the image
      display.syncExec(new Runnable() {

        public void run() {
          if (toggleRequired) {
            // Disable any grids temporarily
            gridFigure.setVisible(false);
          }
          // Deselect any selections
          graphicalViewer.deselectAll();
          rootFigure.paint(grap);
        }
      });

      ImageLoader imgLoader = new ImageLoader();
      imgLoader.data = new ImageData[] { img.getImageData() };

      ByteArrayOutputStream baos = new ByteArrayOutputStream(imgLoader.data.length);

      imgLoader.save(baos, SWT.IMAGE_PNG);

      imageGC.dispose();
      img.dispose();

      // Access UI thread from runnable
      display.syncExec(new Runnable() {

        public void run() {
          if (toggleRequired) {
            // Re-enable any grids
            gridFigure.setVisible(true);
          }
        }
      });

      final byte[] bytes = baos.toByteArray();
      final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      saveResource(getRelativeURIForDiagram(diagram, FILENAME_PATTERN), bais, this.monitor);

    } catch (Exception e) {
      e.printStackTrace();
      addProblemToDiagram(diagram, "An exception occurred while creating the image: " + e.getCause(), null);
    }
  }
}
