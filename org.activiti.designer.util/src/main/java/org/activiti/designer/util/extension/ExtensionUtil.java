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

import org.activiti.bpmn.model.CustomProperty;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.integration.palette.AbstractDefaultPaletteCustomizer;
import org.activiti.designer.integration.palette.DefaultPaletteCustomizer;
import org.activiti.designer.integration.palette.PaletteEntry;
import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.integration.servicetask.CustomServiceTaskDescriptor;
import org.activiti.designer.integration.usertask.AbstractCustomUserTask;
import org.activiti.designer.integration.usertask.CustomUserTask;
import org.activiti.designer.integration.usertask.CustomUserTaskDescriptor;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.eclipse.ExtensionConstants;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
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
  
  public static List<CustomUserTaskDescriptor> providedCustomUserTaskDescriptors;
  
  private ExtensionUtil() {

  }

  public static void addProvidedCustomServiceTaskDescriptors(List<CustomServiceTaskDescriptor> descriptors) {
    if (providedCustomServiceTaskDescriptors == null) {
      providedCustomServiceTaskDescriptors = new ArrayList<CustomServiceTaskDescriptor>();
    }
    providedCustomServiceTaskDescriptors.addAll(descriptors);
  }
  
  public static void addProvidedCustomUserTaskDescriptors(List<CustomUserTaskDescriptor> descriptors) {
    if (providedCustomUserTaskDescriptors == null) {
      providedCustomUserTaskDescriptors = new ArrayList<CustomUserTaskDescriptor>();
    }
    providedCustomUserTaskDescriptors.addAll(descriptors);
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
        final IClasspathContainer userLibraryContainer = JavaCore.getClasspathContainer(new Path(DESIGNER_EXTENSIONS_USER_LIB_PATH), javaProject);

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

              IPackageFragment[] fragments = javaProject.getPackageFragments();
              for (IPackageFragment iPackageFragment : fragments) {
                
                if (classpathEntry.getPath().lastSegment().equalsIgnoreCase(iPackageFragment.getParent().getElementName())) {
                  
                  IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                  
                  String classPathFilename = null;
                  if (classpathEntry.getPath().toFile().exists()) {
                    classPathFilename = classpathEntry.getPath().toPortableString();
                  } else {
                    classPathFilename = root.getLocation().toPortableString() + classpathEntry.getPath().toPortableString();
                  }
                  
                  JarClassLoader cl = new JarClassLoader(classPathFilename);

                  // Inspect the jar by scanning its classpath and looking for classes that implement CustomServiceTask
                  for (final IJavaElement javaElement : iPackageFragment.getChildren()) {
                    if (javaElement.getElementType() == IJavaElement.CLASS_FILE) {
                      IClassFile classFile = (IClassFile) javaElement;
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
                            } catch (Exception e) {
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
  
  private static boolean isConcreteCustomUserTask(IType type) {

    boolean customUserTaskFound = containsAbstractClassOrInterface(type, AbstractCustomUserTask.class, CustomUserTask.class);

    try {
      if (customUserTaskFound && !Modifier.isAbstract(type.getFlags())) {
        customUserTaskFound = true;
      } else {
        customUserTaskFound = false;
      }
    } catch (JavaModelException e) {
      customUserTaskFound = false;
      e.printStackTrace();
    }

    return customUserTaskFound;
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
  
  public static final String wrapCustomPropertyId(final UserTask userTask, final String propertyId) {
    return userTask.getId() + ExtensionConstants.CUSTOM_PROPERTY_ID_SEPARATOR + propertyId;
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
      return StringUtils.isNotEmpty(serviceTask.getExtensionId());
    }
    return result;
  }
  
  public static final boolean isCustomUserTask(final Object bo) {
    boolean result = false;
    if (bo instanceof UserTask) {
      final UserTask userTask = (UserTask) bo;
      return StringUtils.isNotEmpty(userTask.getExtensionId());
    }
    return result;
  }
  
  public static final CustomProperty getCustomProperty(final Task task, final String propertyName) {
    if (task instanceof ServiceTask) {
      return getCustomProperty((ServiceTask) task, propertyName);
    } else if (task instanceof UserTask) {
      return getCustomProperty((UserTask) task, propertyName);
    } else {
      return null;
    }
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
    for (final CustomProperty customProperty : serviceTask.getCustomProperties()) {
      if (propertyName.equals(customProperty.getName())) {
        result = customProperty;
        break;
      }
    }
    return result;
  }
  
  public static final CustomProperty getCustomProperty(final UserTask userTask, final String propertyName) {
    CustomProperty result = null;
    for (final CustomProperty customProperty : userTask.getCustomProperties()) {
      if (propertyName.equals(customProperty.getName())) {
        result = customProperty;
        break;
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
   * Gets a list of {@link CustomUserTask} objects based on the
   * {@link IProject} provided.
   * 
   * @param project
   *          the project that has {@link CustomUserTask}s defined
   * @return a list of all {@link CustomUserTask}s or an empty list if none
   *         were found
   */
  public static List<CustomUserTask> getCustomUserTasks(final IProject project) {

    List<CustomUserTask> result = new ArrayList<CustomUserTask>();

    // Determine the project
    IJavaProject javaProject = null;
    try {
      javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
    } catch (CoreException e) {
      // skip, not a Java project
    }

    if (javaProject != null) {

      // get the contexts first
      final List<CustomUserTaskContext> cstContexts = getCustomUserTaskContexts(project);

      // extract custom service tasks from the contexts
      for (final CustomUserTaskContext customUserTaskContext : cstContexts) {
        result.add(customUserTaskContext.getUserTask());
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
        final IClasspathContainer userLibraryContainer = JavaCore.getClasspathContainer(new Path(DESIGNER_EXTENSIONS_USER_LIB_PATH), javaProject);
        
        // Get a list of the classpath entries in the container. Each of
        // these represents one jar containing zero or more designer
        // extensions
        final IClasspathEntry[] extensionJars = userLibraryContainer.getClasspathEntries();

        // If there are jars, inspect them; otherwise return because
        // there are no extensions
        if (extensionJars.length > 0) {

          for (final IClasspathEntry classpathEntry : extensionJars) {

            // Only check entries of the correct kind
            if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY && classpathEntry.getContentKind() == IPackageFragmentRoot.K_BINARY) {
              IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
              IPackageFragment[] fragments = javaProject.getPackageFragments();
              for (IPackageFragment iPackageFragment : fragments) {
                
                if (classpathEntry.getPath().lastSegment().equalsIgnoreCase(iPackageFragment.getParent().getElementName())) {
                  
                  Manifest manifest = null;
                  for (Object obj : iPackageFragment.getNonJavaResources()) {
                    if (obj instanceof JarEntryDirectory) {
                      final JarEntryDirectory jarEntryDirectory = (JarEntryDirectory) obj;
                      final IJarEntryResource[] jarEntryResources = jarEntryDirectory.getChildren();
                      for (IJarEntryResource jarEntryResource : jarEntryResources) {
                        if ("MANIFEST.MF".equals(jarEntryResource.getName())) {
                          try {
                            final InputStream stream = jarEntryResource.getContents();
                            manifest = new Manifest(stream);
                            
                          } catch (Exception e) {
                            // no manifest as result
                          }
                        }
                      }
                    }
                  }
                  
                  final IJavaElement[] javaElements = iPackageFragment.getChildren();
                  
                  String classPathFilename = null;
                  if (classpathEntry.getPath().toFile().exists()) {
                    classPathFilename = classpathEntry.getPath().toPortableString();
                  } else {
                    classPathFilename = root.getLocation().toPortableString() + classpathEntry.getPath().toPortableString();
                  }
                  
                  JarClassLoader cl = new JarClassLoader(classPathFilename);
                  
                  // Determine the name of the extension
                  String extensionName = null;
                  if (manifest != null) {
                    extensionName = manifest.getMainAttributes().getValue(CustomServiceTask.MANIFEST_EXTENSION_NAME);
                  }
                  // If there is no manifest or the property wasn't
                  // defined, use the jar's name as extension name
                  // instead
                  if (extensionName == null) {
                    extensionName = classpathEntry.getPath().lastSegment();
                  }
                  
                  for (final IJavaElement javaElement : javaElements) {
                    if (javaElement.getElementType() == IJavaElement.CLASS_FILE) {
                      IClassFile classFile = (IClassFile) javaElement;
                      if (classFile.isClass()) {

                        final IType type = classFile.getType();

                        if (!isConcreteCustomService(type)) {
                          continue;
                        }
                        
                        try {
                          Class<CustomServiceTask> clazz = (Class<CustomServiceTask>) cl.loadClass(type.getFullyQualifiedName());

                          // Filter if the class is abstract: this probably means it is extended by concrete classes in the
                          // extension and will have any properties applied in that way; we can't instantiate the class anyway
                          if (!Modifier.isAbstract(clazz.getModifiers()) && CustomServiceTask.class.isAssignableFrom(clazz)) {
                            try {
                              CustomServiceTask customServiceTask = (CustomServiceTask) clazz.newInstance();
                              // Add this CustomServiceTask to the result, wrapped in its context
                              result.add(new CustomServiceTaskContextImpl(customServiceTask, extensionName, classPathFilename));
                              
                            } catch (Exception e) {
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
        showExtensionExceptionMessage(String.format("There was a technical error when processing an extension to Activiti Designer: %s", e.getMessage()));
        e.printStackTrace();
      }
    }

    return result;
  }
  
  /**
   * Gets a list of {@link CustomUserTaskContext} objects based on the
   * {@link IProject} provided.
   * 
   * @param project
   *          the project that has {@link CustomUserTask}s defined
   * @return a list containing the context of each {@link CustomUserTask}
   *         object found or an empty list if {@link CustomUserTask}s were
   *         found were found
   */
  public static List<CustomUserTaskContext> getCustomUserTaskContexts(final IProject project) {

    List<CustomUserTaskContext> result = new ArrayList<CustomUserTaskContext>();

    addToCustomUserTasks(result);

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
        final IClasspathContainer userLibraryContainer = JavaCore.getClasspathContainer(new Path(DESIGNER_EXTENSIONS_USER_LIB_PATH), javaProject);
        
        // Get a list of the classpath entries in the container. Each of
        // these represents one jar containing zero or more designer
        // extensions
        final IClasspathEntry[] extensionJars = userLibraryContainer.getClasspathEntries();

        // If there are jars, inspect them; otherwise return because
        // there are no extensions
        if (extensionJars.length > 0) {

          for (final IClasspathEntry classpathEntry : extensionJars) {

            // Only check entries of the correct kind
            if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY && classpathEntry.getContentKind() == IPackageFragmentRoot.K_BINARY) {
              IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
              IPackageFragment[] fragments = javaProject.getPackageFragments();
              for (IPackageFragment iPackageFragment : fragments) {
                
                if (classpathEntry.getPath().lastSegment().equalsIgnoreCase(iPackageFragment.getParent().getElementName())) {
                  
                  Manifest manifest = null;
                  for (Object obj : iPackageFragment.getNonJavaResources()) {
                    if (obj instanceof JarEntryDirectory) {
                      final JarEntryDirectory jarEntryDirectory = (JarEntryDirectory) obj;
                      final IJarEntryResource[] jarEntryResources = jarEntryDirectory.getChildren();
                      for (IJarEntryResource jarEntryResource : jarEntryResources) {
                        if ("MANIFEST.MF".equals(jarEntryResource.getName())) {
                          try {
                            final InputStream stream = jarEntryResource.getContents();
                            manifest = new Manifest(stream);
                            
                          } catch (Exception e) {
                            // no manifest as result
                          }
                        }
                      }
                    }
                  }
                  
                  final IJavaElement[] javaElements = iPackageFragment.getChildren();
                  
                  String classPathFilename = null;
                  if (classpathEntry.getPath().toFile().exists()) {
                    classPathFilename = classpathEntry.getPath().toPortableString();
                  } else {
                    classPathFilename = root.getLocation().toPortableString() + classpathEntry.getPath().toPortableString();
                  }
                  
                  JarClassLoader cl = new JarClassLoader(classPathFilename);
                  
                  // Determine the name of the extension
                  String extensionName = null;
                  if (manifest != null) {
                    extensionName = manifest.getMainAttributes().getValue(CustomServiceTask.MANIFEST_EXTENSION_NAME);
                  }
                  // If there is no manifest or the property wasn't
                  // defined, use the jar's name as extension name
                  // instead
                  if (extensionName == null) {
                    extensionName = classpathEntry.getPath().lastSegment();
                  }
                  
                  for (final IJavaElement javaElement : javaElements) {
                    if (javaElement.getElementType() == IJavaElement.CLASS_FILE) {
                      IClassFile classFile = (IClassFile) javaElement;
                      if (classFile.isClass()) {

                        final IType type = classFile.getType();

                        if (!isConcreteCustomUserTask(type)) {
                          continue;
                        }
                        
                        try {
                          Class<CustomUserTask> clazz = (Class<CustomUserTask>) cl.loadClass(type.getFullyQualifiedName());

                          // Filter if the class is abstract: this probably means it is extended by concrete classes in the
                          // extension and will have any properties applied in that way; we can't instantiate the class anyway
                          if (!Modifier.isAbstract(clazz.getModifiers()) && CustomUserTask.class.isAssignableFrom(clazz)) {
                            try {
                              CustomUserTask customUserTask = (CustomUserTask) clazz.newInstance();
                              // Add this CustomServiceTask to the result, wrapped in its context
                              result.add(new CustomUserTaskContextImpl(customUserTask, extensionName, classPathFilename));
                              
                            } catch (Exception e) {
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
        showExtensionExceptionMessage(String.format("There was a technical error when processing an extension to Activiti Designer: %s", e.getMessage()));
        e.printStackTrace();
      }
    }

    return result;
  }

  private static void showExtensionExceptionMessage(final String detailMessage) {
    MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error in extension", detailMessage);
  }

  private static void addToCustomServiceTasks(List<CustomServiceTaskContext> result) {
    if (providedCustomServiceTaskDescriptors != null) {
      for (CustomServiceTaskDescriptor dscr : providedCustomServiceTaskDescriptors) {
        Class< ? extends CustomServiceTask> clazz = dscr.getClazz();
        if (clazz != null && !Modifier.isAbstract(clazz.getModifiers()) && CustomServiceTask.class.isAssignableFrom(clazz)) {
          try {
            CustomServiceTask customServiceTask = (CustomServiceTask) clazz.newInstance();
            result.add(new CustomServiceTaskContextImpl(customServiceTask, dscr.getExtensionName(), dscr.getExtensionJarPath()));
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
  
  private static void addToCustomUserTasks(List<CustomUserTaskContext> result) {
    if (providedCustomUserTaskDescriptors != null) {
      for (CustomUserTaskDescriptor dscr : providedCustomUserTaskDescriptors) {
        Class< ? extends CustomUserTask> clazz = dscr.getClazz();
        if (clazz != null && !Modifier.isAbstract(clazz.getModifiers()) && CustomUserTask.class.isAssignableFrom(clazz)) {
          try {
            CustomUserTask customUserTask = (CustomUserTask) clazz.newInstance();
            result.add(new CustomUserTaskContextImpl(customUserTask, dscr.getExtensionName(), dscr.getExtensionJarPath()));
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
