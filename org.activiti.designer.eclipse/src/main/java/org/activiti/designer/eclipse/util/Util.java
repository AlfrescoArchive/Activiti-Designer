/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.eclipse.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Collection of general static helper methods.
 */
public class Util {

  private static final String DIAGRAM_NAME_PATTERN = "name=\"%s\"";
  private static final String DIAGRAM_NAME_DEFAULT = String.format(DIAGRAM_NAME_PATTERN, "my_bpmn2_diagram");

  /**
   * Moves the object at the source index of the list to the _target index of
   * the list and returns the moved object.
   * 
   * @param targetIndex
   *          the new position for the object in the list.
   * @param sourceIndex
   *          the old position of the object in the list.
   * @return the moved object.
   * @exception IndexOutOfBoundsException
   *              if either index isn't within the size range.
   */
  public static Object moveElementInList(List<Object> list, int targetIndex, int sourceIndex) {
    if (targetIndex >= list.size() || targetIndex < 0)
      throw new IndexOutOfBoundsException("targetIndex=" + targetIndex + ", size=" + list.size()); //$NON-NLS-1$ //$NON-NLS-2$

    if (sourceIndex >= list.size() || sourceIndex < 0)
      throw new IndexOutOfBoundsException("sourceIndex=" + sourceIndex + ", size=" + list.size()); //$NON-NLS-1$ //$NON-NLS-2$

    Object object = list.get(sourceIndex);
    if (targetIndex != sourceIndex) {
      list.remove(sourceIndex);
      list.add(targetIndex, object);
    }
    return object;
  }

  /**
   * Returns true, if the given objects equal, while null is also a valid value.
   * In detail the check is: (o1 == null && o2 == null) || (o1.equals(o2)).
   * 
   * @param o1
   *          The first Object to compare.
   * @param o2
   *          The second Object to compare.
   * @return true, if the given objects equal, while null is also a valid value.
   */
  public static boolean equalsWithNull(Object o1, Object o2) {
    if (o1 == null && o2 == null)
      return true;
    if (o1 == null || o2 == null)
      return false;
    return o1.equals(o2);
  }

  public static BaseElement[] getAllBpmnElements(IProject project, ResourceSet rSet) {
    // FIXME: always unload to have our resources refreshed, this is highly
    // non-performant
    EList<Resource> resources = rSet.getResources();
    for (Resource resource : resources) {
      resource.unload();
    }
    IFolder folder = project.getFolder("src");
    IFolder folderDiagrams = project.getFolder("src/diagrams");
    Collection<Diagram> diagrams = new ArrayList<Diagram>();
    Set<BaseElement> bpmnElements = new HashSet<BaseElement>();
    if (folder.exists()) {
      List<IResource> membersList = new ArrayList<IResource>();
      try {
        membersList.addAll(Arrays.asList(folder.members()));
        membersList.addAll(Arrays.asList(folderDiagrams.members()));
      } catch (CoreException e) {
        return new BaseElement[0];
      }
      for (IResource resource : membersList) {
        if (resource instanceof IFile) {
          IFile file = (IFile) resource;
          if ("diagram".equals(file.getFileExtension()) || file.getName().equals("Predefined.data")) {
            // The following call extracts the diagram from the
            // given file. For the Tutorial diagrams always reside
            // in a file of their own and are the first root object.
            // This may of course be different in a concrete tool
            // implementation, so tool builders should use their own
            // way of retrieval here
            Diagram diag = org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal.getEmfService().getDiagramFromFile(file, rSet);
            if (diag != null) {
              diagrams.add(diag);
            } else {
              // The following call tries to retrieve a URI from
              // any of the found files to check if there are any
              // EClasses inside this file. Concrete tools should
              // use their own logic to browse through their files
              // (e.g. known by a special extension or residing in
              // a special folder) instead of this generic logic.
              URI uri = org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal.getEmfService().getFileURI(file, rSet);
              Resource fileResource = rSet.getResource(uri, true);
              if (fileResource != null) {
                EList<EObject> contents = fileResource.getContents();
                for (EObject object : contents) {
                  if (object instanceof BaseElement && !(object instanceof PictogramElement)) {
                    bpmnElements.add((BaseElement) object);
                  }
                }
              }
            }
          }
        }
      }
    }
    for (Diagram diagram : diagrams) {
      Resource resource = diagram.eResource();
      if (resource == null)
        return new BaseElement[0];
      EList<EObject> contents = resource.getContents();
      for (EObject object : contents) {
        if (object instanceof BaseElement && !(object instanceof PictogramElement)) {
          bpmnElements.add((BaseElement) object);
        }
      }
    }
    return bpmnElements.toArray(new BaseElement[bpmnElements.size()]);
  }

  public static InputStream getContentStream(final Content content) {
    return Util.class.getClassLoader().getResourceAsStream(content.getContentPath());
  }

  public enum Content {

    NEW_DIAGRAM_CONTENT("src/main/resources/content/new-diagram-content.xml"), NEW_SUBPROCESS_CONTENT("src/main/resources/content/new-subprocess-content.xml");

    private final String contentPath;

    private Content(String contentPath) {
      this.contentPath = contentPath;
    }

    public String getContentPath() {
      return contentPath;
    }

  }

  /**
   * Gets the {@link URI} where the diagram resource for a subprocess should be
   * stored.
   * 
   * @param diagram
   *          the parent diagram for the subprocess
   * @param subprocessId
   *          the id of the subprocess
   * @return the {@link URI} for the subprocess' resource
   */
  public static final URI getSubProcessURI(Diagram diagram, String subprocessId) {

    final URI baseURI = diagram.eResource().getURI().trimFileExtension();
    final URI subProcessURI = baseURI.appendFileExtension(subprocessId).appendFileExtension(diagram.eResource().getURI().fileExtension());

    return subProcessURI;
  }

  /**
   * Replaces the document name in the provided contentStream's content and
   * returns a new stream containing the new content.
   * 
   * @param diagramName
   *          the name of the document to use
   * @param contentStream
   *          the original content stream
   * @return
   */
  public static InputStream swapStreamContents(final String diagramName, final InputStream contentStream) {
    InputStream result = null;

    try {

      Writer writer = new StringWriter();
      char[] buffer = new char[1024];
      try {
        Reader reader = new BufferedReader(new InputStreamReader(contentStream, "UTF-8"));
        int n;
        while ((n = reader.read(buffer)) != -1) {
          writer.write(buffer, 0, n);
        }
      } finally {
        contentStream.close();
      }
      String contentString = writer.toString();
      contentString = contentString.replace(DIAGRAM_NAME_DEFAULT, String.format(DIAGRAM_NAME_PATTERN, diagramName));

      result = new ByteArrayInputStream(contentString.getBytes());
    } catch (Exception e) {
      // TODO
    }
    return result;
  }
}
