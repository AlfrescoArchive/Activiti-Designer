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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Abstract base class for diagram workers.
 * 
 * @author Tiese Barrell
 */
public abstract class AbstractDiagramWorker {

  public static final String ATTRIBUTE_NODE_ID = "nodeId";
  public static final String ATTRIBUTE_WORKER_ID = "workerId";

  private static final String DATE_TIME_PATTERN = "yyyyMMdd-HHmmss";

  private static final String REGEX_DATE_TIME = "\\" + ExportMarshaller.PLACEHOLDER_DATE_TIME + "";
  private static final String REGEX_FILENAME = "\\" + ExportMarshaller.PLACEHOLDER_ORIGINAL_FILENAME + "";
  private static final String REGEX_FILENAME_WITHOUT_EXTENSION = "\\" + ExportMarshaller.PLACEHOLDER_ORIGINAL_FILENAME_WITHOUT_EXTENSION + "";
  private static final String REGEX_EXTENSION = "\\" + ExportMarshaller.PLACEHOLDER_ORIGINAL_FILE_EXTENSION + "";

  private DiagramWorkerContext diagramWorkerContext;

  /**
   * Gets an {@link InputStream} to the contents of the
   * {@link DiagramWorkerContext} .
   * 
   * @return an input stream to the diagram's contents
   */
  protected InputStream getDiagramInputStream() {
    final IFile file = (IFile) getDiagramResource();
    if (file != null) {
      try {
        return file.getContents();
      } catch (CoreException e) {
        throw new IllegalArgumentException("Unable to get input stream for diagram worker context", e);
      }
    } else {
      throw new IllegalArgumentException("Unable to get input stream for diagram worker context because the file referenced for the diagram is null");
    }
  }
  /**
   * Gets the {@link IResource} associated with the {@link DiagramWorkerContext}
   * .
   * 
   * @return the resource associated with the diagram
   */
  protected IResource getDiagramResource() {
    return diagramWorkerContext.getBpmnModel().getModelFile();
  }

  /**
   * Gets the {@link URI} associated with the {@link DiagramWorkerContext}'s
   * resource.
   * 
   * @return the URI for the resource associated with the diagram
   */
  protected URI getDiagramURI() {
    return getDiagramResource().getLocationURI();
  }

  /**
   * Gets a new URI based on the {@link DiagramWorkerContext} and relative to
   * the resource associated to the {@link DiagramWorkerContext}.
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
   * @param relativePath
   *          the relative path to the diagram provided
   * @return the URI for the resource associated with the diagram
   */
  protected URI getURIRelativeToDiagram(final String relativePath) {

    final String resolvedRelativePath = resolvePlaceholders(relativePath);

    final URI originalURI = getDiagramURI();

    URI result = originalURI;
    try {
      result = originalURI.resolve(resolvedRelativePath);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Resolves placeholders on the provided path as substitutions. Placeholders
   * that are supported are defined by the {@link ExportMarshaller}. All
   * occurences of matching placeholders are replaced.
   * 
   * @param path
   *          the path to substitute placeholders for
   * @return the substituted path
   */
  protected String resolvePlaceholders(final String path) {

    String result = path;

    final Calendar now = Calendar.getInstance();
    final SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
    result = result.replaceAll(REGEX_DATE_TIME, sdf.format(now.getTime()));

    final IResource resource = getDiagramResource();
    result = result.replaceAll(REGEX_EXTENSION, resource.getFileExtension());
    result = result.replaceAll(REGEX_FILENAME, resource.getName());

    final int separatorLocation = resource.getName().indexOf(".");
    final String resourceNameWithoutExtension = resource.getName().substring(0, separatorLocation);
    result = result.replaceAll(REGEX_FILENAME_WITHOUT_EXTENSION, resourceNameWithoutExtension);

    return result;
  }

  /**
   * Checks whether the provided resourceURI points to a resource that exists in
   * the workspace.
   * 
   * @param resourceURI
   *          the URI for the resource
   * @return true if the resource exists, false otherwise
   */
  protected boolean resourceExists(final URI resourceURI) {
    final IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(resourceURI.getPath());
    return fileResource != null && fileResource.exists();
  }

  /**
   * Gets a resource attached to the provided URI.
   * 
   * @param resourceURI
   *          the URI to the resource
   * @return a resource or null if there is none
   */
  protected IResource getResource(final URI resourceURI) {
    IResource result = null;
    if (resourceExists(resourceURI)) {
      result = ResourcesPlugin.getWorkspace().getRoot().findMember(resourceURI.getPath());
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
    final IFile file = (IFile) workspace.getRoot().findFilesForLocationURI(uri)[0];

    // TODO monitor
    try {
      if (file.exists()) {
        monitor.beginTask("update content", 10);
        file.setContents(content, true, true, new SubProgressMonitor(monitor, 5));
      } else {
        monitor.beginTask("create", 10);
        IFolder folder = (IFolder) file.getParent();
        prepareFolder(folder);
        file.create(content, true, monitor);
      }
      file.refreshLocal(IResource.DEPTH_INFINITE, null);
      monitor.worked(10);
    } catch (final CoreException e) {
      e.printStackTrace();
      addProblemToDiagram("A problem occured while saving a resource in the export marshaller: " + e.getMessage(), null);
    }
  }

  private void prepareFolder(final IFolder folder) throws CoreException {
    final IContainer parent = folder.getParent();
    if (parent instanceof IFolder) {
      prepareFolder((IFolder) parent);
    }
    if (!folder.exists()) {
      // TODO monitor
      folder.create(true, false, new NullProgressMonitor());
    }
  }

  /**
   * Adds a marker to the diagram that has an {@link IMarker#SEVERITY_INFO}
   * severity (info).
   */
  protected void addInfoToDiagram(final String message, final String nodeId) {
    addMarkerToDiagram(message, nodeId, IMarker.SEVERITY_INFO);
  }

  /**
   * Adds a marker to the diagram that has a {@link IMarker#SEVERITY_WARNING}
   * severity (warning).
   */
  protected void addWarningToDiagram(final String message, final String nodeId) {
    addMarkerToDiagram(message, nodeId, IMarker.SEVERITY_WARNING);
  }

  /**
   * Adds a marker to the diagram that has an {@link IMarker#SEVERITY_ERROR}
   * severity (error).
   */
  protected void addProblemToDiagram(final String message, final String nodeId) {
    addMarkerToDiagram(message, nodeId, IMarker.SEVERITY_ERROR);
  }

  private void addMarkerToDiagram(final String message, final String nodeId, final int severity) {
    final IResource resource = getDiagramResource();
    String markerId = getMarkerId();

    IMarker m;
    try {
      m = resource.createMarker(markerId);
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

  /**
   * Gets the id to be used for markers created by this diagram worker.
   * 
   * @return the marker id
   */
  protected abstract String getMarkerId();

  /**
   * Clears markers for the diagram associated with the
   * {@link DiagramWorkerContext}.
   * 
   * <p>
   * <strong>Note</strong>: this will only clean markers of the type of the
   * diagram worker as defined by {@link #getMarkerId()}.
   * 
   */
  protected void clearMarkersForDiagram() {
    clearMarkers(getDiagramResource());
  }

  /**
   * Clears markers for the provided {@link IResource}.
   * 
   * <p>
   * <strong>Note</strong>: this will only clean markers of the type of the
   * diagram worker as defined by {@link #getMarkerId()}.
   */
  protected void clearMarkers(final IResource resource) {
    try {
      final IMarker[] markers = resource.findMarkers(getMarkerId(), true, IResource.DEPTH_INFINITE);
      for (final IMarker marker : markers) {
        if (marker.getAttribute(ATTRIBUTE_WORKER_ID).equals(this.getClass().getCanonicalName())) {
          marker.delete();
        }
      }
    } catch (CoreException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets all markers for the diagram associated with the
   * {@link DiagramWorkerContext}.
   * 
   * <p>
   * <strong>Note</strong>: this will only get markers of the type of the
   * diagram worker as defined by {@link #getMarkerId()}.
   * 
   * @return the markers for the diagram
   */
  protected IMarker[] getMarkers() {
    return getMarkers(getDiagramResource());
  }

  /**
   * Gets all markers for the provided {@link IResource}.
   * 
   * <p>
   * <strong>Note</strong>: this will only get markers of the type of the
   * diagram worker as defined by {@link #getMarkerId()}.
   * 
   * @param resource
   *          the resource to get the markers for
   * @return the markers for the resource
   */
  protected IMarker[] getMarkers(final IResource resource) {
    IMarker[] markers = null;
    try {
      markers = resource.findMarkers(getMarkerId(), true, IResource.DEPTH_INFINITE);
    } catch (CoreException e) {
      e.printStackTrace();
    }
    return markers;
  }

  protected DiagramWorkerContext getDiagramWorkerContext() {
    return diagramWorkerContext;
  }

  protected void setDiagramWorkerContext(DiagramWorkerContext diagramWorkerContext) {
    this.diagramWorkerContext = diagramWorkerContext;
  }

}
