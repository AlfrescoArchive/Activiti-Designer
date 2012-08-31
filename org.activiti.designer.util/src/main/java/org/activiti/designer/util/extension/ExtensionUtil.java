/**
 * 
 */
package org.activiti.designer.util.extension;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;

import org.activiti.designer.bpmn2.model.CustomProperty;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.integration.servicetask.CustomServiceTaskDescriptor;
import org.activiti.designer.integration.palette.AbstractDefaultPaletteCustomizer;
import org.activiti.designer.integration.palette.DefaultPaletteCustomizer;
import org.activiti.designer.integration.palette.PaletteEntry;
import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.eclipse.ExtensionConstants;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJarEntryResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarEntryDirectory;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Provides utilities for Extensions to the Designer.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public final class ExtensionUtil {

	public static final String USER_LIBRARY_NAME_EXTENSIONS = "Activiti Designer Extensions";

  public static final String DESIGNER_EXTENSIONS_USER_LIB_PATH = "org.eclipse.jdt.USER_LIBRARY/" + USER_LIBRARY_NAME_EXTENSIONS;
  
  public static List<CustomServiceTaskDescriptor> providedCustomServiceTaskDescriptors;
	
  private ExtensionUtil() {

  }
  
  public static void addProvidedCustomServiceTaskDescriptors(List<CustomServiceTaskDescriptor> descriptors){
	  if (providedCustomServiceTaskDescriptors == null) providedCustomServiceTaskDescriptors = new ArrayList<CustomServiceTaskDescriptor>();
	  providedCustomServiceTaskDescriptors.addAll(descriptors);
  }

  public static final Set<PaletteEntry> getDisabledPaletteEntries(IProject project) {

    Set<PaletteEntry> result = new HashSet<PaletteEntry>();

    // Determine the project
    IJavaProject javaProject = null;
    try {
      javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
    } catch (CoreException e) {
      // skip, not a Java project
    }

    if (javaProject != null) {

      try {

        // Get the container for the designer extensions. This is the
        // predefined user library linking to the extension libraries
        final IClasspathContainer userLibraryContainer = JavaCore
                .getClasspathContainer(new Path(DESIGNER_EXTENSIONS_USER_LIB_PATH), javaProject);

        // Get a list of the classpath entries in the container. Each of
        // these represents one jar containing zero or more designer
        // extensions
        final IClasspathEntry[] extensionJars = userLibraryContainer.getClasspathEntries();

        // If there are jars, inspect them; otherwise return because
        // there are no extensions
        if (extensionJars.length > 0) {

          for (final IClasspathEntry classpathEntry : extensionJars) {

            // Only check entries of the correct kind
            if (classpathEntry.getEntryKind() == 1 && classpathEntry.getContentKind() == 2) {

              final IPackageFragmentRoot packageFragmentRoot = javaProject.getPackageFragmentRoot(classpathEntry.getPath().toString());

              // Create a JarClassLoader to load any classes we
              // find for this extension
              final JarClassLoader cl = new JarClassLoader(packageFragmentRoot.getPath().toPortableString());

              // Inspect the jar by scanning its classpath and looking for
              // classes that implement
              // CustomServiceTask
              final IJavaElement[] javaElements = packageFragmentRoot.getChildren();
              for (final IJavaElement javaElement : javaElements) {
                if (javaElement.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
                  IPackageFragment fragment = (IPackageFragment) javaElement;
                  if (fragment.containsJavaResources()) {
                    final IClassFile[] classFiles = fragment.getClassFiles();
                    for (final IClassFile classFile : classFiles) {
                      if (classFile.isClass()) {

                        final IType type = classFile.getType();
                        if (!isConcretePaletteCustomizer(type)) {
                          continue;
                        }

                        try {
                          Class<DefaultPaletteCustomizer> clazz = (Class<DefaultPaletteCustomizer>) cl.loadClass(type.getFullyQualifiedName());

                          if (DefaultPaletteCustomizer.class.isAssignableFrom(clazz)) {
                            try {
                              DefaultPaletteCustomizer DefaultPaletteCustomizer = (DefaultPaletteCustomizer) clazz.newInstance();
                              // Add this DefaultPaletteCustomizer to the result
                              result.addAll(DefaultPaletteCustomizer.disablePaletteEntries());
                            } catch (InstantiationException e) {
                              // TODO Auto-generated catch block
                              e.printStackTrace();
                            } catch (IllegalAccessException e) {
                              // TODO Auto-generated catch block
                              e.printStackTrace();
                            }
                          }
                        } catch (ClassNotFoundException e) {
                          e.printStackTrace();
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      } catch (JavaModelException e) {
        // TODO: test when this exception occurs: if there is no user
        // lib for example?
        e.printStackTrace();
      }

    }

    return result;
  }

  private static boolean isConcreteCustomService(IType type) {

    boolean customserviceFound = containsAbstractClassOrInterface(type, AbstractCustomServiceTask.class, CustomServiceTask.class);

    try {
      if (customserviceFound && !Modifier.isAbstract(type.getFlags())) {
        customserviceFound = true;
      } else {
        customserviceFound = false;
      }
    } catch (JavaModelException e) {
      customserviceFound = false;
      e.printStackTrace();
    }

    return customserviceFound;

  }

  private static boolean isConcretePaletteCustomizer(IType type) {

    boolean paletteCustomizerFound = containsAbstractClassOrInterface(type, AbstractDefaultPaletteCustomizer.class, DefaultPaletteCustomizer.class);

    try {
      if (paletteCustomizerFound && !Modifier.isAbstract(type.getFlags())) {
        paletteCustomizerFound = true;
      } else {
        paletteCustomizerFound = false;
      }
    } catch (JavaModelException e) {
      paletteCustomizerFound = false;
      e.printStackTrace();
    }

    return paletteCustomizerFound;

  }

  // TODO: utilize type hierarchy in efficient manner
  private static boolean containsAbstractClassOrInterface(IType type, Class targetAbstractClass, Class targetInterface) {

    boolean result = false;

    try {
      // 1. Check whether the super classname of the type matches the abstract
      // superclass we favor
      // 2. Check whether the type implements the interface itself
      // 3. If the type has *other* superclasses than the one we favor,
      // recursively inspect the hierarchy
      // using
      // the same method
      if (targetAbstractClass.getCanonicalName().equalsIgnoreCase(type.getSuperclassName())) {
        result = true;
      } else if (type.getSuperInterfaceNames() != null && type.getSuperInterfaceNames().length > 0) {
        for (String interfaceName : type.getSuperInterfaceNames()) {
          if (targetInterface.getCanonicalName().equalsIgnoreCase(interfaceName)) {
            result = true;
          }
        }
      }
      // } else if (type.getSuperclassName() != null) {
      // result =
      // containsAbstractClassOrInterface(hierarchy.getSuperclass(type),
      // targetAbstractClass, targetInterface);
      // }

    } catch (Exception e) {
      result = false;
    }

    return result;

  }

  /**
   * Wraps the property id for the provided service tasks.
   * 
   * @param serviceTask
   *          the parent task the property belongs to
   * @param propertyId
   *          the id of the property to wrap
   * @return the wrapped id
   */
  public static final String wrapCustomPropertyId(final ServiceTask serviceTask, final String propertyId) {
    return serviceTask.getId() + ExtensionConstants.CUSTOM_PROPERTY_ID_SEPARATOR + propertyId;
  }

  /**
   * Unwraps the provided property id string.
   * 
   * @param wrappedCustomPropertyId
   *          the id string to unwrap
   * @return the unwrapped id
   */
  public static final String upWrapCustomPropertyId(final String wrappedCustomPropertyId) {
    return StringUtils.substringAfter(wrappedCustomPropertyId, ExtensionConstants.CUSTOM_PROPERTY_ID_SEPARATOR);
  }

  /**
   * Gets the id of the {@link CustomServiceTask} in the provided business
   * object, if it is in fact a {@link CustomServiceTask}.
   * 
   * @param bo
   *          the business object
   * @return the id if the business object is a custom service task, null
   *         otherwise
   */
  public static final String getCustomServiceTaskId(final Object bo) {
    String result = null;

    if (isCustomServiceTask(bo)) {
      final ServiceTask serviceTask = (ServiceTask) bo;
      final CustomProperty prop = getCustomProperty(serviceTask, ExtensionConstants.PROPERTY_ID_CUSTOM_SERVICE_TASK);

      if (prop != null) {
        result = prop.getSimpleValue();
      }
    }
    return result;
  }

  /**
   * Determines whether the provided business object is a custom service task.
   * 
   * @param bo
   *          the business object
   * @return true if the business object is a custom service task, false
   *         otherwise
   */
  public static final boolean isCustomServiceTask(final Object bo) {
    boolean result = false;
    if (bo instanceof ServiceTask) {
      final ServiceTask serviceTask = (ServiceTask) bo;
      return hasCustomProperty(serviceTask, ExtensionConstants.PROPERTY_ID_CUSTOM_SERVICE_TASK);
    }
    return result;
  }

  /**
   * Gets the {@link CustomProperty} identified by the provided name from the
   * serviceTask.
   * 
   * @param serviceTask
   *          the servicetask that holds the custom property
   * @param propertyName
   *          the name of the property
   * @return the {@link CustomProperty} found or null if no property was found
   */
  public static final CustomProperty getCustomProperty(final ServiceTask serviceTask, final String propertyName) {

    CustomProperty result = null;

    if (hasCustomProperty(serviceTask, propertyName)) {
      for (final CustomProperty customProperty : serviceTask.getCustomProperties()) {
        if (propertyName.equals(customProperty.getName())) {
          result = customProperty;
          break;
        }
      }
    }
    return result;
  }

  /**
   * Determines whether the {@link CustomProperty} identified by the provided
   * name is held by the serviceTask.
   * 
   * @param serviceTask
   *          the servicetask that holds the custom property
   * @param propertyName
   *          the name of the property
   * @return true if the serviceTask has the property, false otherwise
   */
  public static final boolean hasCustomProperty(final ServiceTask serviceTask, final String propertyName) {
    boolean result = false;
    for (final CustomProperty customProperty : serviceTask.getCustomProperties()) {
      if (propertyName.equals(customProperty.getName())) {
        result = true;
        break;
      }
    }

    return result;
  }

  /**
   * Gets a list of {@link CustomServiceTask} objects based on the
   * {@link TabbedPropertySheetPage} provided.
   * 
   * @param tabbedPropertySheetPage
   *          the property sheet page linked to a diagram in a project that has
   *          {@link CustomServiceTask}s defined
   * @return a list of all {@link CustomServiceTask}s or an empty list if none
   *         were found
   */
  public static List<CustomServiceTask> getCustomServiceTasks(final TabbedPropertySheetPage tabbedPropertySheetPage) {

    // Determine the part the property sheet page is in
    final IWorkbenchPart part = tabbedPropertySheetPage.getSite().getWorkbenchWindow().getPartService().getActivePart();

    // If the part is a diagram editor, get the project from the diagram
    if (part instanceof DiagramEditor) {
      final DiagramEditor editor = (DiagramEditor) part;
      final IProject project = ActivitiUiUtil.getProjectFromDiagram(editor.getDiagramTypeProvider().getDiagram());

      // Determine the custom service tasks using the project found
      return getCustomServiceTasks(project);
    }

    return null;
  }

  /**
   * Gets a list of {@link CustomServiceTask} objects based on the
   * {@link IProject} provided.
   * 
   * @param project
   *          the project that has {@link CustomServiceTask}s defined
   * @return a list of all {@link CustomServiceTask}s or an empty list if none
   *         were found
   */
  public static List<CustomServiceTask> getCustomServiceTasks(final IProject project) {

    List<CustomServiceTask> result = new ArrayList<CustomServiceTask>();

    // Determine the project
    IJavaProject javaProject = null;
    try {
      javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
    } catch (CoreException e) {
      // skip, not a Java project
    }

    if (javaProject != null) {

      // get the contexts first
      final List<CustomServiceTaskContext> cstContexts = getCustomServiceTaskContexts(project);

      // extract custom service tasks from the contexts
      for (final CustomServiceTaskContext customServiceTaskContext : cstContexts) {
        result.add(customServiceTaskContext.getServiceTask());
      }
    }

    return result;
  }

  /**
   * Gets a list of {@link CustomServiceTaskContext} objects based on the
   * {@link IProject} provided.
   * 
   * @param project
   *          the project that has {@link CustomServiceTask}s defined
   * @return a list containing the context of each {@link CustomServiceTask}
   *         object found or an empty list if {@link CustomServiceTask}s were
   *         found were found
   */
  public static List<CustomServiceTaskContext> getCustomServiceTaskContexts(final IProject project) {

    List<CustomServiceTaskContext> result = new ArrayList<CustomServiceTaskContext>();
    
    addToCustomServiceTasks(result);

    IJavaProject javaProject = null;
    try {
      javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
    } catch (CoreException e) {
      // skip, not a Java project
    }

    if (javaProject != null) {

      try {

        // Get the container for the designer extensions. This is the
        // predefined user library linking to the extension libraries
        final IClasspathContainer userLibraryContainer = JavaCore
                .getClasspathContainer(new Path(DESIGNER_EXTENSIONS_USER_LIB_PATH), javaProject);

        // Get a list of the classpath entries in the container. Each of
        // these represents one jar containing zero or more designer
        // extensions
        final IClasspathEntry[] extensionJars = userLibraryContainer.getClasspathEntries();

        // If there are jars, inspect them; otherwise return because
        // there are no extensions
        if (extensionJars.length > 0) {

          for (final IClasspathEntry classpathEntry : extensionJars) {

            // Only check entries of the correct kind
            if (classpathEntry.getEntryKind() == 1 && classpathEntry.getContentKind() == 2) {

              final IPackageFragmentRoot packageFragmentRoot = javaProject.getPackageFragmentRoot(classpathEntry.getPath().toString());

              // Determine the name of the extension
              String extensionName = null;
              // Extract the manifest and look for the
              // CustomServiceTask.MANIFEST_EXTENSION_NAME
              // property
              final Manifest manifest = extractManifest(packageFragmentRoot);
              if (manifest != null) {
                extensionName = manifest.getMainAttributes().getValue(CustomServiceTask.MANIFEST_EXTENSION_NAME);
              }
              // If there is no manifest or the property wasn't
              // defined, use the jar's name as extension name
              // instead
              if (extensionName == null) {
                extensionName = classpathEntry.getPath().lastSegment();
              }

              // Create a JarClassLoader to load any classes we
              // find for this extension

              final JarClassLoader cl = new JarClassLoader(packageFragmentRoot.getPath().toPortableString());

              // Inspect the jar by scanning its classpath and
              // looking for classes that implement
              // CustomServiceTask
              final IJavaElement[] javaElements = packageFragmentRoot.getChildren();
              for (final IJavaElement javaElement : javaElements) {
                if (javaElement.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
                  IPackageFragment fragment = (IPackageFragment) javaElement;
                  if (fragment.containsJavaResources()) {
                    final IClassFile[] classFiles = fragment.getClassFiles();
                    for (final IClassFile classFile : classFiles) {
                      if (classFile.isClass()) {

                        final IType type = classFile.getType();

                        if (!isConcreteCustomService(type)) {
                          continue;
                        }
                        // if (type.getFullyQualifiedName() !=
                        // "com.atosorigin.esuite.editors.servicetasks.ESuiteEndNode")
                        // {
                        // continue;
                        // }
                        try {
                          Class<CustomServiceTask> clazz = (Class<CustomServiceTask>) cl.loadClass(type.getFullyQualifiedName());

                          // Filter if the class is
                          // abstract: this probably
                          // means it is extended by
                          // concrete classes in the
                          // extension and will have
                          // any properties applied in
                          // that way; we can't
                          // instantiate the class
                          // anyway
                          if (!Modifier.isAbstract(clazz.getModifiers()) && CustomServiceTask.class.isAssignableFrom(clazz)) {
                            try {
                              CustomServiceTask customServiceTask = (CustomServiceTask) clazz.newInstance();
                              // Add this
                              // CustomServiceTask
                              // to
                              // the result,
                              // wrapped
                              // in its context
                              result.add(new CustomServiceTaskContextImpl(customServiceTask, extensionName, classpathEntry.getPath().toPortableString()));

                            } catch (InstantiationException e) {
                              // TODO
                              // Auto-generated
                              // catch block
                              e.printStackTrace();
                            } catch (IllegalAccessException e) {
                              // TODO
                              // Auto-generated
                              // catch block
                              e.printStackTrace();
                            }

                          }
                        } catch (ClassNotFoundException e) {
                          e.printStackTrace();
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      } catch (JavaModelException e) {
        // TODO: test when this exception occurs: if there is no user
        // lib for example?
        e.printStackTrace();
      }
    }

    return result;
  }
  
	private static void addToCustomServiceTasks(List<CustomServiceTaskContext> result) {
		if (providedCustomServiceTaskDescriptors != null) {
			for (CustomServiceTaskDescriptor dscr : providedCustomServiceTaskDescriptors) {
				Class<? extends CustomServiceTask> clazz = dscr.getClazz();
				if (clazz != null && !Modifier.isAbstract(clazz.getModifiers()) && CustomServiceTask.class.isAssignableFrom(clazz)) {
					try {
						CustomServiceTask customServiceTask = (CustomServiceTask)clazz.newInstance();
						result.add(new CustomServiceTaskContextImpl(customServiceTask,dscr.getExtensionName(),dscr.getExtensionJarPath()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
  
  /**
   * @param packageFragmentRoot
   * @throws JavaModelException
   * @throws CoreException
   */
  @SuppressWarnings("restriction")
  private static Manifest extractManifest(IPackageFragmentRoot packageFragmentRoot) throws JavaModelException {

    Manifest result = null;
    final Object[] nonJavaResources = packageFragmentRoot.getNonJavaResources();

    for (Object obj : nonJavaResources) {
      if (obj instanceof JarEntryDirectory) {
        final JarEntryDirectory jarEntryDirectory = (JarEntryDirectory) obj;
        final IJarEntryResource[] jarEntryResources = jarEntryDirectory.getChildren();
        for (IJarEntryResource jarEntryResource : jarEntryResources) {
          if ("MANIFEST.MF".equals(jarEntryResource.getName())) {
            try {
              final InputStream stream = jarEntryResource.getContents();
              result = new Manifest(stream);
            } catch (Exception e) {
              // no manifest as result
            }
          }
        }
      }
    }
    return result;
  }

}
