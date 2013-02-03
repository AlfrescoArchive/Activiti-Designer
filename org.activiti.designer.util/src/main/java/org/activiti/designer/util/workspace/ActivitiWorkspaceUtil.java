package org.activiti.designer.util.workspace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.activiti.bpmn.model.Process;
import org.activiti.designer.util.ActivitiConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class ActivitiWorkspaceUtil {

  /**
   * A cache of all project data files and some useful cache data like process IDs within this
   * project
   */
  private static Map<IResource, CacheData> cache = new HashMap<IResource, CacheData>();

  /**
   * Returns a set of all open activiti projects found in the workspace.
   *
   * @return a set of projects with the nature, or an empty set if none are found (which would be
   *     weird as at least one should be open to call this method ;-)
   */
  public static final Set<IProject> getOpenProjects() {
    final Set<IProject> result = new HashSet<IProject>();

    final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for (final IProject project : projects) {
      try {
        if (project.isOpen() && project.hasNature(ActivitiConstants.NATURE_ID)) {
          result.add(project);
        }
      } catch (CoreException exception) {
        // intentionally left blank
      }
    }
    return result;
  }

  /**
   * Retrieves all process IDs in the given diagram data file resource. In case the resource does
   * not reflect a data file for a BPMN process, the result will be empty.
   *
   * @param dataFile the data file of the diagram to use
   * @return all process IDs the data file contains
   */
  private static Set<String> getProcessIds(final IFile dataFile) {

    final Set<String> result = new HashSet<String>();
    final BpmnProcessParser parser = new BpmnProcessParser(dataFile);

    for (Process process : parser.getProcesses()) {
      result.add(process.getId());
    }

    return result;
  }

  /**
   * Returns all found diagram data files over all open activiti projects. Additionally this method
   * will build a cache for these data files, where for each entry all process IDs existing in the
   * cache are saved for faster retrieval, as retrieving process IDs involves calling the
   * {@link BpmnProcessParser}.
   *
   * @return a set of all diagram data files in all open projects
   */
  public static final Set<IFile> getAllDiagramDataFiles() {
    final Set<IFile> result = new HashSet<IFile>();
    final Set<IProject> projects = ActivitiWorkspaceUtil.getOpenProjects();

    for (final IProject project : projects) {
      final DiagramDataFileFinder visitor = new DiagramDataFileFinder();

      try {
        project.accept(visitor);
      } catch (CoreException exception) {
        // intentionally ignored
      }

      for (final IFile resource : visitor.getResources()) {
        result.add(resource);

        CacheData cachedResourceData = cache.get(resource);
        final long lastModified = resource.getModificationStamp();

        if (cachedResourceData == null || cachedResourceData.cacheIsExpired(lastModified)) {
          final Set<String> processIds = getProcessIds(resource);

          if (cachedResourceData == null) {
            cachedResourceData = new CacheData(processIds, lastModified);

            cache.put(resource, cachedResourceData);
          } else {
            cachedResourceData.setProcessIds(processIds);
            cachedResourceData.setLastModified(lastModified);
          }
        }
      }
    }

    return result;
  }

  /**
   * Maps all currently found diagrams in all open activiti projects to their included process IDs.
   *
   * @return a map where the key is the data file resource of a diagram and the value is a set of
   *    all processes defined in this diagram.
   */
  public static final Map<IFile, Set<String>> getAllProcessIdsByDiagramDataFile() {
    final Map<IFile, Set<String>> result = new HashMap<IFile, Set<String>>();
    final Set<IFile> projectResources = getAllDiagramDataFiles();

    for (final IFile projectResource : projectResources) {
      result.put(projectResource, cache.get(projectResource).getProcessIds());
    }

    return result;
  }

  /**
   * Returns the diagram data files that match the given process ID.
   *
   * @param processId the process ID to look for
   * @return a set of diagram data files or <code>null</code> in case no such process ID exists in
   *    any diagram.
   */
  public static final Set<IFile> getDiagramDataFilesByProcessId(final String processId) {
    final Set<IFile> result = new HashSet<IFile>();
    final Set<IFile> projectResources = getAllDiagramDataFiles();

    for (final IFile resource : projectResources) {
      final CacheData data = cache.get(resource);

      if (data.hasProcessId(processId)) {
        result.add(resource);
      }
    }

    return result;
  }

  /**
   * A resource visitor to find all activiti diagram files within a project. This visitor is
   * applied to each open Activiti project.
   */
  private static class DiagramDataFileFinder implements IResourceVisitor {

    private static final Set<String> IGNORED_ROOT_SEGMENTS = new HashSet<String>();
    private Set<IFile> visitResults = new HashSet<IFile>();

    static {
      IGNORED_ROOT_SEGMENTS.add("target");
      IGNORED_ROOT_SEGMENTS.add("tempbar");
    }

    @Override
    public boolean visit(IResource resource) throws CoreException {

      if (isIgnoredResource(resource)) {
        return false;
      }

      if (resource instanceof IFile
              && resource.getName().endsWith(ActivitiConstants.DATA_FILE_EXTENSION)) {
        visitResults.add((IFile) resource);
      }

      return true;
    }

    private boolean isIgnoredResource(IResource resource) {
      boolean result = false;

      if (resource instanceof IFolder) {
        final String rootSegment = ((IFolder) resource).getFullPath().segment(1);

        if (IGNORED_ROOT_SEGMENTS.contains(rootSegment)) {
          result = true;
        }
      }

      return result;
    }

    public Set<IFile> getResources() {
      return visitResults;
    }

  }

  /**
   * A data cache to cache process IDs for currently open diagrams as this involves calling the
   * {@link BpmnProcessParser}, which is rather time consuming.
   */
  private static class CacheData {

    private Set<String> processIds;
    private Long lastModified;

    public CacheData(final Set<String> bpmnIds, final Long lastModified) {
      this.processIds = bpmnIds;
      this.lastModified = lastModified;
    }

    public void setProcessIds(Set<String> processIds) {
      this.processIds = processIds;
    }

    public Set<String> getProcessIds() {
      return processIds;
    }

    public void setLastModified(Long lastModified) {
      this.lastModified = lastModified;
    }

    public boolean cacheIsExpired(final Long lastModified) {
      return this.lastModified.compareTo(lastModified) < 0;
    }

    public boolean hasProcessId(final String processId) {
      return processIds.contains(processId);
    }

  }

}
