/**
 * 
 */
package org.activiti.designer.eclipse.extension.export;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public interface ExportMarshaller {

  /**
   * Placeholder parsed when creating a filename that is substituted with the
   * filename of the original file.
   */
  public static final String PLACEHOLDER_ORIGINAL_FILENAME = "$originalFile";

  /**
   * Placeholder parsed when creating a filename that is substituted with the
   * filename of the original file, stripped of the original file's extension.
   */
  public static final String PLACEHOLDER_ORIGINAL_FILENAME_WITHOUT_EXTENSION = "$originalNameWithoutExtension";

  /**
   * Placeholder parsed when creating a filename that is substituted with the
   * date and time at the moment of creation.
   */
  public static final String PLACEHOLDER_DATE_TIME = "$dateTime";

  /**
   * Placeholder parsed when creating a filename that is substituted with the
   * file extension of the original file.
   */
  public static final String PLACEHOLDER_ORIGINAL_FILE_EXTENSION = "$originalExtension";

  /**
   * The identifier for problems created by the Activiti Designer for
   * {@link ExportMarshaller}s.
   */
  public static final String MARKER_ID = "org.activiti.designer.eclipse.activitiMarshallerMarker";

  /**
   * Gets a descriptive name for the marshaller.
   * 
   * @return the marshaller's name
   */
  String getMarshallerName();

  /**
   * Gets a descriptive name for the format the marshaller produces.
   * 
   * @return the format's name
   */
  String getFormatName();

  /**
   * Transforms content in the original diagram into this marshaller's own
   * format.
   * 
   * <p>
   * The {@link IProgressMonitor} provided should be used to indicate progress
   * made in the marshaller and will be reported to the user.
   * 
   * @param diagram
   *          the diagram to be marshalled
   * @param monitor
   *          the monitor used to indicate progress of this marshaller
   * 
   * @return the transformed diagram as a byte[]
   */
  void marshallDiagram(Diagram diagram, IProgressMonitor monitor);

}
