package org.activiti.designer.util.workspace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.bpmn2.Process;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class ActivitiWorkspaceUtil {

  private static Map<IResource, CacheData> cache = new HashMap<IResource, CacheData>();

  /**
   * Returns a Set of projects that have the provided project nature. The result
   * contains only projects that are open and available for use.
   * 
   * @param natureId
   *          the id of the project nature required
   * @return a set of projects with the nature, or an empty set if none are
   *         found
   */
  public static final Set<IProject> getOpenProjectsWithNature(final String natureId) {
    final Set<IProject> result = new HashSet<IProject>();

    final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for (final IProject project : projects) {
      try {
        if (project.isOpen() && project.hasNature(natureId)) {
          result.add(project);
        }
      } catch (CoreException e) {
        // don't handle; project may be closed and shouldn't be considered
      }
    }
    return result;
  }

  public static final Set<IFile> getBPMNResourcesById(String callElement) {
    final Set<IFile> result = new HashSet<IFile>();

    final Set<IFile> allBPMNFiles = getBPMNResources();
    for (final IFile resource : allBPMNFiles) {
      if (!cache.containsKey(resource)) {
        cache.put(resource, new CacheData(getProcessIds(resource), resource.getModificationStamp()));
      }

      final CacheData data = cache.get(resource);
      if (data.cacheIsExpired(resource.getModificationStamp())) {
        data.setProcessIds(getProcessIds(resource));
        data.setLastModified(resource.getModificationStamp());
      }

      if (data.hasProcessId(callElement)) {
        result.add(resource);
      }
    }
    return result;
  }

  private static Set<String> getProcessIds(IFile resource) {

    final Set<String> result = new HashSet<String>();

    final BpmnProcessParser parser = new BpmnProcessParser(resource);
    final Set<Process> processes = parser.getProcesses();

    for (final Process process : processes) {
      result.add(process.getId());
    }

    return result;
  }
  public static final Set<IFile> getBPMNResources() {
    final Set<IFile> result = new HashSet<IFile>();

    final Set<IProject> projects = ActivitiWorkspaceUtil.getOpenProjectsWithNature("org.activiti.designer.nature");

    for (final IProject project : projects) {
      final BPMNResourceVisitor visitor = new BPMNResourceVisitor();
      try {
        project.accept(visitor);
      } catch (CoreException e) {
        e.printStackTrace();
      }
      result.addAll(visitor.getResources());
    }

    return result;
  }

  private static class BPMNResourceVisitor implements IResourceVisitor {

    private static final Set<String> IGNORED_ROOT_SEGMENTS = new HashSet<String>();
    private Set<IFile> visitResults = new HashSet<IFile>();

    static {
      IGNORED_ROOT_SEGMENTS.add("target");
      IGNORED_ROOT_SEGMENTS.add("tempbar");
    }

    @Override
    public boolean visit(IResource resource) throws CoreException {
      // TODO externalize extension to method
      if (isIgnoredResource(resource)) {
        return false;
      }
      if (resource instanceof IFile && resource.getName().endsWith(".bpmn20.xml")) {
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

  private static class CacheData {

    private Set<String> processIds;
    private Long lastModified;

    public CacheData(final Set<String> bpmnIds, final Long lastModified) {
      this.processIds = bpmnIds;
      this.lastModified = lastModified;
    }

    public Set<String> getProcessIds() {
      return processIds;
    }

    public void setProcessIds(Set<String> processIds) {
      this.processIds = processIds;
    }

    public Long getLastModified() {
      return lastModified;
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
