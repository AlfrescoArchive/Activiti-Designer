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

package org.activiti.designer.eclipse.extension;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.extension.validation.ProcessValidator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 */
public abstract class AbstractDiagramWorker {

  public static final String ATTRIBUTE_NODE_ID = "nodeId";
  public static final String ATTRIBUTE_WORKER_ID = "workerId";

  private static final String DATE_TIME_PATTERN = "yyyy-MM-dd-HH-mm-ss";

  private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_TIME_PATTERN);

  private static final String REGEX_DATE_TIME = "\\" + ExportMarshaller.PLACEHOLDER_DATE_TIME + "";
  private static final String REGEX_FILENAME = "\\" + ExportMarshaller.PLACEHOLDER_ORIGINAL_FILENAME + "";
  private static final String REGEX_FILENAME_WITHOUT_EXTENSION = "\\" + ExportMarshaller.PLACEHOLDER_ORIGINAL_FILENAME_WITHOUT_EXTENSION + "";
  private static final String REGEX_EXTENSION = "\\" + ExportMarshaller.PLACEHOLDER_ORIGINAL_FILE_EXTENSION + "";

  /**
   * Gets an {@link InputStream} to the contents of the provided {@link Diagram}
   * .
   * 
   * @param diagram
   *          the diagram to get the input stream for
   * @return an input stream to the diagram's contents
   */
  protected InputStream getInputStreamForDiagram(final Diagram diagram) {

    InputStream result = null;

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IFile file = workspace.getRoot().getFile(new Path(getResourceForDiagram(diagram).getURI().toPlatformString(true)));

    try {
      result = file.getContents();
    } catch (CoreException e) {

    }
    return result;
  }

  /**
   * Gets the {@link Resource} associated with the provided {@link Diagram}.
   * 
   * @param diagram
   *          the diagram to find the resource for
   * @return the resource associated with the diagram
   */
  protected Resource getResourceForDiagram(final Diagram diagram) {
    return diagram.eResource();
  }

  /**
   * Gets the {@link URI} associated with the provided {@link Diagram}'s
   * resource.
   * 
   * @param diagram
   *          the diagram to find the URI for
   * @return the URI for the resource associated with the diagram
   */
  protected URI getURIForDiagram(final Diagram diagram) {
    return getResourceForDiagram(diagram).getURI();
  }

  /**
   * Gets a new URI based on the provided {@link Diagram}s location and relative
   * to the resource associated to the {@link Diagram}.
   * 
   * <p>
   * If replacement of {@link ExportMarshaller}'s replacement variables is
   * required, the provided relativePath should contain these variables in the
   * final segment of the path. Replacement variables in other segments will not
   * be parsed and will result in exceptions.
   * 
   * <p>
   * <strong> Example usage: </strong>
   * <p>
   * if you wish to get a URI for a file named "my-file.xml" to be saved in the
   * same directory as the original diagram, you would use:<br>
   * {@link #getRelativeURIForDiagram(diagram, "my-file.xml")}.
   * <p>
   * To store the same file in a subdirectory called "my-dir" of the original
   * diagram's directory, you would use<br>
   * {@link #getRelativeURIForDiagram(diagram, "my-dir/my-file.xml")}.
   * <p>
   * To store the same file in the same subdirectory and use the original
   * diagram's extension as the extension for the new resource you would use<br>
   * {@link #getRelativeURIForDiagram(diagram, "my-dir/my-file." +
   * ExportMarshaller.PLACEHOLDER_ORIGINAL_FILE_EXTENSION)}.
   * 
   * @param diagram
   *          the diagram to find the URI for
   * @param relativePath
   *          the relative path to the diagram provided
   * @return the URI for the resource associated with the diagram
   */
  protected URI getRelativeURIForDiagram(final Diagram diagram, final String relativePath) {
    final URI originalURI = getResourceForDiagram(diagram).getURI();

    String finalSegment = relativePath;
    final URI parentURI = originalURI.trimSegments(1);

    // Parse any replacement variables in the filename
    final Calendar now = Calendar.getInstance();

    finalSegment = finalSegment.replaceAll(REGEX_DATE_TIME, SDF.format(now.getTime()));
    finalSegment = finalSegment.replaceAll(REGEX_EXTENSION, originalURI.fileExtension());
    finalSegment = finalSegment.replaceAll(REGEX_FILENAME, originalURI.lastSegment());
    finalSegment = finalSegment.replaceAll(REGEX_FILENAME_WITHOUT_EXTENSION, originalURI.trimFileExtension().lastSegment());

    return parentURI.appendSegment(finalSegment);
  }

  /**
   * Checks whether the provided resourceURI points to a resource that exists in
   * the workspace.
   * 
   * @param resourceURI
   * @return
   */
  protected boolean resourceExists(URI resourceURI) {
    final IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(resourceURI.toPlatformString(true));

    return fileResource != null && fileResource.exists();
  }

  /**
   * Gets a resource attached to the provided URI.
   * 
   * @param resourceURI
   *          the URI to the resource
   * @return a resource or null if there is none
   */
  protected IResource getResource(URI resourceURI) {
    IResource result = null;
    if (resourceExists(resourceURI)) {
      result = ResourcesPlugin.getWorkspace().getRoot().findMember(resourceURI.toPlatformString(true));
    }
    return result;
  }

  /**
   * Saves a resource at the provided URI. Use this method to create or update
   * resources created by {@link ExportMarshaller}s. This method adheres to the
   * overwrite flag provided.
   * 
   * <p>
   * The URI provided is <strong>not</strong> parsed for replacements and is
   * considered final when invoking this method. When obtaining the URI for a
   * resource, replacements will be parsed if
   * {@link #getRelativeURIForDiagram(Diagram, String)} is used.
   * 
   * <p>
   * To obtain a URI for the new resource you wish to create, invoke
   * {@link #getRelativeURIForDiagram(Diagram, String)}.
   * 
   * @see #getRelativeURIForDiagram(Diagram, String)
   * 
   * @param uri
   *          the URI the resource should be saved to
   * @param content
   *          a stream to the content for the resource
   * @param overwriteFlag
   *          the flag for overwrite behavior
   * @param monitor
   *          the progress monitor to use
   */
  protected void saveResource(final URI uri, final InputStream content, final IProgressMonitor monitor) {

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    final IFile file = workspace.getRoot().getFile(new Path(uri.toPlatformString(true)));

    // TODO
    try {
      if (file.exists()) {
        // delete first
        monitor.beginTask("update content", 10);
        file.setContents(content, true, true, monitor);
      } else {
        monitor.beginTask("create", 10);
        file.create(content, true, monitor);
      }
      file.refreshLocal(IResource.DEPTH_INFINITE, null);
      monitor.worked(10);
    } catch (CoreException e) {
      // TODO
      e.printStackTrace();
      // addProblemToDiagram(diagram,
      // "A problem occured while exectuing the export marshaller: " +
      // e.getMessage(), null);
    }
  }

  /**
   * Adds a marker to the {@link Diagram} provided that has an
   * {@link IMarker#SEVERITY_INFO} severity (info).
   */
  protected void addInfoToDiagram(Diagram diagram, String message, String nodeId) {
    addMarkerToDiagram(diagram, message, nodeId, IMarker.SEVERITY_INFO);
  }

  /**
   * Adds a marker to the {@link Diagram} provided that has an
   * {@link IMarker#SEVERITY_WARNING} severity (warning).
   */
  protected void addWarningToDiagram(Diagram diagram, String message, String nodeId) {
    addMarkerToDiagram(diagram, message, nodeId, IMarker.SEVERITY_WARNING);
  }

  /**
   * Adds a marker to the {@link Diagram} provided that has an
   * {@link IMarker#SEVERITY_ERROR} severity (error).
   */
  protected void addProblemToDiagram(Diagram diagram, String message, String nodeId) {
    addMarkerToDiagram(diagram, message, nodeId, IMarker.SEVERITY_ERROR);
  }

  private void addMarkerToDiagram(Diagram diagram, String message, String nodeId, final int severity) {

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    final IFile file = workspace.getRoot().getFile(new Path(getURIForDiagram(diagram).toPlatformString(true)));

    // Determine marker id
    String markerId = ActivitiBPMNDiagramConstants.ACTIVITI_GENERAL_MARKER_ID;
    if (this instanceof ExportMarshaller) {
      markerId = ExportMarshaller.MARKER_ID;
    } else if (this instanceof ProcessValidator) {
      markerId = ProcessValidator.MARKER_ID;
    }

    IMarker m;
    try {
      m = file.createMarker(markerId);
      if (nodeId != null) {
        m.setAttribute(ATTRIBUTE_NODE_ID, nodeId);
      }

      m.setAttribute(ATTRIBUTE_WORKER_ID, this.getClass().getCanonicalName());

      m.setAttribute(IMarker.MESSAGE, message);
      m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
      m.setAttribute(IMarker.SEVERITY, severity);
    } catch (CoreException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  protected void clearMarkers(IResource resource) {

    // Determine marker id
    String markerId = ActivitiBPMNDiagramConstants.ACTIVITI_GENERAL_MARKER_ID;
    if (this instanceof ExportMarshaller) {
      markerId = ExportMarshaller.MARKER_ID;
    } else if (this instanceof ProcessValidator) {
      markerId = ProcessValidator.MARKER_ID;
    }

    try {
      final IMarker[] markers = resource.findMarkers(markerId, true, IResource.DEPTH_INFINITE);
      for (final IMarker marker : markers) {
        if (marker.getAttribute(ATTRIBUTE_WORKER_ID).equals(this.getClass().getCanonicalName())) {
          marker.delete();
        }
      }
    } catch (CoreException e) {
      e.printStackTrace();
    }
  }
  
  protected IMarker[] getMarkers(IResource resource) {
    IMarker[] markers = null;
    try {
      markers = resource.findMarkers(ActivitiBPMNDiagramConstants.ACTIVITI_GENERAL_MARKER_ID, true, IResource.DEPTH_INFINITE);
    } catch (CoreException e) {
      e.printStackTrace();
    }
    return markers;
  }

}
