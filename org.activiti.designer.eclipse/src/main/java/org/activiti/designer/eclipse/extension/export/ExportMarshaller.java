/**
 * 
 */
package org.activiti.designer.eclipse.extension.export;

/**
 * Produces customized output when exporting diagrams. ExportMarshallers are
 * invoked (if the user has the preference set in Eclipse's settings) when
 * diagrams are saved.
 * 
 * @author Tiese Barrell
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
   * Transforms content in the model into this marshaller's own format.
   * 
   * @param context
   *          the context for marshalling
   * 
   * @return the transformed diagram as a byte[]
   */
  void marshallDiagram(final ExportMarshallerContext context);

}
